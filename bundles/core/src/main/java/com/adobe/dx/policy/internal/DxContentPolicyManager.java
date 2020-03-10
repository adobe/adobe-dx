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
package com.adobe.dx.policy.internal;

import static com.day.cq.wcm.scripting.WCMBindingsConstants.NAME_CURRENT_CONTENT_POLICY;

import com.day.cq.wcm.api.policies.ContentPolicy;

import javax.script.Bindings;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.wrappers.CompositeValueMap;
import org.apache.sling.scripting.api.BindingsValuesProvider;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

@Component(service = BindingsValuesProvider.class,
    property = {
        Constants.SERVICE_RANKING + ":Integer=1500",
        "javax.script.name=sightly",
        "javax.script.name=sling-models"
    }
)
/**
 * provides DexterPolicy script and model bindings
 */
public class DxContentPolicyManager implements BindingsValuesProvider {

    private static final String KEY = "dxPolicy";

    @Override
    public void addBindings(Bindings bindings) {
        if (!bindings.containsKey(KEY)) {
            Resource resource = bindings.containsKey(SlingBindings.RESOURCE)
                ? (Resource) bindings.get(SlingBindings.RESOURCE) : null;
            ContentPolicy policy = bindings.containsKey(NAME_CURRENT_CONTENT_POLICY)
                ? (ContentPolicy) bindings.get(NAME_CURRENT_CONTENT_POLICY) : null;
            bindings.put(KEY, new CompositeValueMap(resource.getValueMap(), policy.getProperties()));
        }
    }
}
