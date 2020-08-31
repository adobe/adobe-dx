/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.adobe.dx.structure.flex;

import com.adobe.dx.domtagging.IDTagger;
import com.adobe.dx.responsive.Breakpoint;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FlexModel {
    private static final String NN_DEFINITIONS = "definitions";

    @SlingObject
    protected SlingHttpServletRequest request;

    @OSGiService
    IDTagger idTagger;

    @ScriptVariable
    Breakpoint[] breakpoints;

    Map<String, Breakpoint> bpMap;
    Map<String, List<ValueMap>> definitionsMap = new HashMap<>();

    String id;

    @PostConstruct
    void init() {
        bpMap = new HashMap<>();
        if (breakpoints != null) {
            for (Breakpoint breakpoint : breakpoints) {
                bpMap.put(breakpoint.key(), breakpoint);
            }
        }
    }

    public boolean isStyleNeeded() {
        return true;
    }

    public String getId() {
        if (StringUtils.isBlank(id) && idTagger != null) {
            id = idTagger.computeComponentId(request, null);
        }
        return id;
    }

    private Resource getPolicyResource(String name) {
        ResourceResolver resolver = request.getResourceResolver();
        ContentPolicyManager policyManager = resolver.adaptTo(ContentPolicyManager.class);
        if (policyManager != null) {
            ContentPolicy contentPolicy = policyManager.getPolicy(request.getResource());
            if (contentPolicy != null) {
                String path = contentPolicy.getPath();
                return resolver.getResource(path + "/" + name);
            }
        }
        return null;
    }

    private String computeResponsiveResourceName(String name, Breakpoint breakpoint) {
        return name + breakpoint.propertySuffix();
    }

    public List<ValueMap> getDefinitions(String breakpointKey) {
        if (definitionsMap.containsKey(breakpointKey)) {
            return definitionsMap.get(breakpointKey);
        }
        if (bpMap.containsKey(breakpointKey)) {
            String resourceName = computeResponsiveResourceName(NN_DEFINITIONS, bpMap.get(breakpointKey));
            Resource parent = request.getResource().getChild(resourceName);
            if (parent == null) {
                parent = getPolicyResource(resourceName);
            }
            if (parent != null) {
                definitionsMap.put(breakpointKey, StreamSupport.stream(parent.getChildren().spliterator(), false)
                    .map(Resource::getValueMap)
                    .collect(Collectors.toList()));
            }
        }
        return definitionsMap.get(breakpointKey);
    }
}
