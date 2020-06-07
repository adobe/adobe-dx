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

import java.util.Arrays;
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

@Model(adaptables = SlingHttpServletRequest.class)
public class MarketoFooter {

    @OSGiService
    private CloudConfigReader cloudConfigReader = null;

    @ScriptVariable
    private Page currentPage = null;

    private static final String CONFIG_NAME = "marketo-config";

    private static final String MARKETO_FORM_ID = "marketoFormId";

    private MarketoConfBasicInfo marketoConfBasicInfo;

    private Set<String> marketoFormIds =  new HashSet<>();

    @PostConstruct
    private void init() {
        marketoConfBasicInfo = cloudConfigReader.getContextAwareCloudConfigRes(
            currentPage.getPath(), CONFIG_NAME, MarketoConfBasicInfo.class);
        if (null != marketoConfBasicInfo) {
            Set<String> marketoComponentTypes = new HashSet<>(Arrays
                .asList(marketoConfBasicInfo.getMarketoComponentTypes()));
            marketoFormIds = getMarketoFormIds(marketoComponentTypes, currentPage);
        }
    }

    public Set<String> getMarketoFormIds() {
        return marketoFormIds;
    }

    public MarketoConfBasicInfo getMarketoConfBasicInfo() {
        return marketoConfBasicInfo;
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

        private static final Set<String> POTENTIAL_XF_REFERENCE_PROPERTY_NAMES = new HashSet<>();
        static {
            POTENTIAL_XF_REFERENCE_PROPERTY_NAMES.add(PN_FRAGMENT_PATH);
            POTENTIAL_XF_REFERENCE_PROPERTY_NAMES.add("appBannerPath");
        }
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
                getXfResource(resource).ifPresent(this::testAndTriggerRecursion);
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

        private Optional<Resource> getXfResource(Resource resource) {
            ValueMap properties = resource.getValueMap();
            ResourceResolver resolver = resource.getResourceResolver();
            for (String propName : POTENTIAL_XF_REFERENCE_PROPERTY_NAMES) {
                String propValue = properties.get(propName, String.class);
                if (StringUtils.isNotEmpty(propValue)) {
                     return Optional.ofNullable(resolver
                         .getResource(StringUtils.appendIfMissing(propValue, "/" + JCR_CONTENT)));
                }
            }
            return Optional.empty();
        }
    }

}
