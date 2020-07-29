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

package com.adobe.dx.structure.utils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BackgroundGradient {

    private static final String CQ_STYLEGUIDE_BUCKETNAME = "cq:styleguide";
    private static final String GRADIENT_NAME = "gradient";
    private static final String GRADIENT_KEY = "gradientCss";
    private static final String GRADIENTS_CONFIG_NAME = "gradients";

    @OSGiService
    private ConfigurationResourceResolver configurationResolver;

    @SlingObject
    protected Resource resource;

    private String resolveGradient(String gradient) {
        if (gradient != null && configurationResolver != null) {
            Resource gradientConfigs = configurationResolver.getResource(resource, CQ_STYLEGUIDE_BUCKETNAME, GRADIENTS_CONFIG_NAME);
            if (gradientConfigs != null) {
                Resource gradientConfig = gradientConfigs.getChild(gradient);
                if (gradientConfig != null) {
                    ValueMap valueMap = gradientConfig.getValueMap();
                    if (valueMap != null) {
                        return valueMap.get(GRADIENT_KEY, String.class);
                    }
                }
            }
        }
        return null;
    }

    public String getGradient() {
        String gradient = resource.getValueMap().get(GRADIENT_NAME, String.class);
        return resolveGradient(gradient);
    }

    public String getGradient(String gradient) {
        return resolveGradient(gradient);
    }
}
