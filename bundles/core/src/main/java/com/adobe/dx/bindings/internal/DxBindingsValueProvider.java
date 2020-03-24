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
package com.adobe.dx.bindings.internal;

import static com.day.cq.wcm.scripting.WCMBindingsConstants.NAME_CURRENT_CONTENT_POLICY;

import com.adobe.dx.responsive.ResponsiveService;
import com.adobe.dx.responsive.internal.ResponsiveProperties;
import com.day.cq.wcm.api.policies.ContentPolicy;

import javax.script.Bindings;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.wrappers.CompositeValueMap;
import org.apache.sling.scripting.api.BindingsValuesProvider;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

@Component(service = BindingsValuesProvider.class,
    property = {
        Constants.SERVICE_RANKING + ":Integer=1500",
        "javax.script.name=sightly",
        "javax.script.name=sling-models"
    },
    configurationPolicy = ConfigurationPolicy.REQUIRE
)
/**
 * provides DX bindings
 */
public class DxBindingsValueProvider implements BindingsValuesProvider {

    private static final String POLICY_KEY = "dxPolicy";

    private static final String RESP_PROPS_KEY = "respProperties";

    @Reference
    ResponsiveService responsiveService;

    @Override
    public void addBindings(@NotNull Bindings bindings) {
        if (!bindings.containsKey(POLICY_KEY)) {
            Resource resource = bindings.containsKey(SlingBindings.RESOURCE)
                ? (Resource) bindings.get(SlingBindings.RESOURCE) : null;
            if (resource != null) {
                ContentPolicy policy = bindings.containsKey(NAME_CURRENT_CONTENT_POLICY)
                    ? (ContentPolicy) bindings.get(NAME_CURRENT_CONTENT_POLICY) : null;
                ValueMap dxPolicy = policy != null ? new CompositeValueMap(resource.getValueMap(), policy.getProperties()) :
                    resource.getValueMap();
                bindings.put(POLICY_KEY, dxPolicy);
                bindings.put(RESP_PROPS_KEY, new ResponsiveProperties(responsiveService.getBreakpoints(), dxPolicy));
            }
        }
    }
}
