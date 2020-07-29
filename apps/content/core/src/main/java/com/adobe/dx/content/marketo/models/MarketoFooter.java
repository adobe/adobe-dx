/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Copyright 2020 Adobe
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.adobe.dx.content.marketo.models;

import static com.adobe.cq.xf.ExperienceFragmentsConstants.PN_FRAGMENT_PATH;
import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static org.apache.commons.lang.StringUtils.EMPTY;

import com.adobe.dx.content.marketo.models.internal.MarketoConfBasicInfo;
import com.adobe.dx.utils.service.CloudConfigReader;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.AbstractResourceVisitor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Model(adaptables = SlingHttpServletRequest.class)
public class MarketoFooter {

    @OSGiService
    private CloudConfigReader cloudConfigReader = null;

    @ScriptVariable
    private Page currentPage = null;

    private static final String CONFIG_NAME = "marketo-config";

    private static final String MARKETO_FORM_ID = "marketoFormId";

    private static final String EMPTY_JSON_ARRAY = "[]";

    private MarketoConfBasicInfo marketoConfBasicInfo;

    private Set<String> marketoFormIds =  new HashSet<>();

    private String marketoFormIdsJsonRep = EMPTY_JSON_ARRAY;

    @PostConstruct
    private void init() {
        marketoConfBasicInfo = cloudConfigReader.getContextAwareCloudConfigRes(
            currentPage.getPath(), CONFIG_NAME, MarketoConfBasicInfo.class);
        if (null != marketoConfBasicInfo) {
            Set<String> marketoComponentTypes = new HashSet<>(Arrays
                .asList(marketoConfBasicInfo.getMarketoComponentTypes()));
            marketoFormIds = getMarketoFormIds(marketoComponentTypes, currentPage);
            marketoFormIdsJsonRep = getJsonArrayRepresentation();
        }
    }

    public Set<String> getMarketoFormIds() {
        return marketoFormIds;
    }

    public String getMarketoFormIdsJsonRep() {
        return marketoFormIdsJsonRep;
    }

    public MarketoConfBasicInfo getMarketoConfBasicInfo() {
        return marketoConfBasicInfo;
    }

    private String getJsonArrayRepresentation() {
        try {
            return new ObjectMapper().writeValueAsString(marketoFormIds);
        } catch (JsonProcessingException e) {
            return EMPTY_JSON_ARRAY;
        }
    }

    // TODO All the code below this to be replaced with the generic service.
    private Set<String> getMarketoFormIds(Set<String> marketoComponentTypes, Page currentPage) {
        ComponentReferenceFinder visitor = new ComponentReferenceFinder(marketoComponentTypes);
        visitor.accept(currentPage.getContentResource());
        return visitor.getMatchingResources().stream()
            .map(this::getMarketoFormId)
            .filter(StringUtils::isNotEmpty).collect(Collectors.toSet());
    }

    private String getMarketoFormId(Resource resource) {
        return resource.getValueMap().get(MARKETO_FORM_ID, EMPTY);
    }

    private static class ComponentReferenceFinder extends AbstractResourceVisitor {

        private static final Collection<String> POTENTIAL_XF_REFERENCE_PROPERTY_NAMES =
            Arrays.asList(PN_FRAGMENT_PATH, "appBannerPath");
        private static final int MAX_XF_COUNT = 50;

        private Set<String> requiredResourceTypes;
        private Set<Resource> matchingResources = new HashSet<>();
        private int currentXfCount;

        ComponentReferenceFinder(@NotNull Set<String> requiredResourceTypes) {
            this.requiredResourceTypes = requiredResourceTypes;
            this.currentXfCount = 0;
        }

        Set<Resource> getMatchingResources() {
            return matchingResources;
        }

        @Override
        protected void visit(@NotNull Resource resource) {
            if (isMatchingResourceType(resource)) {
                matchingResources.add(resource);
            } else  {
                Resource xfResource = getXfResource(resource);
                if (xfResource != null) {
                    testAndTriggerRecursion(xfResource);
                }
            }
        }

        private void testAndTriggerRecursion(Resource xf) {
            if (currentXfCount < MAX_XF_COUNT) {
                currentXfCount++;
                this.accept(xf);
            }
        }

        private boolean isMatchingResourceType(Resource resource) {
            return requiredResourceTypes
                .stream()
                .anyMatch(resource::isResourceType);
        }

        @Nullable
        private Resource getXfResource(Resource resource) {
            ValueMap properties = resource.getValueMap();
            ResourceResolver resolver = resource.getResourceResolver();
            for (String propName : POTENTIAL_XF_REFERENCE_PROPERTY_NAMES) {
                String propValue = properties.get(propName, String.class);
                if (StringUtils.isNotEmpty(propValue)) {
                     return resolver
                         .getResource(StringUtils.appendIfMissing(propValue, "/" + JCR_CONTENT));
                }
            }
            return null;
        }
    }

}
