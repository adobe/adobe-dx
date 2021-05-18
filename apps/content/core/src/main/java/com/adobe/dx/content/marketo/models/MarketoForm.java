/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

package com.adobe.dx.content.marketo.models;

import javax.annotation.PostConstruct;

import com.day.cq.commons.Externalizer;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class)
public class MarketoForm {

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean profiling;
    
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String marketoFormId;
    
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean autoSubmitForm;
    
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String submitText;
    
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String destinationUrl;
   
    @OSGiService(injectionStrategy = InjectionStrategy.OPTIONAL)
    private Externalizer externalizer;

    @SlingObject
    private ResourceResolver resourceResolver;

    @PostConstruct
    private void init() {
        if (destinationUrl != null) {
            destinationUrl = externalizer.publishLink(resourceResolver, destinationUrl);
        }
    }

    public boolean isProfiling() {
        return profiling;
    }

    public String getMarketoFormId() {
        return marketoFormId;
    }

    public boolean isAutoSubmitForm() {
        return autoSubmitForm;
    }

    public String getSubmitText() {
        return submitText;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }
}
