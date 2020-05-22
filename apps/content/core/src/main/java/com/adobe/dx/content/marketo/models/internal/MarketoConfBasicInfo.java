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

package com.adobe.dx.content.marketo.models.internal;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class)
public class MarketoConfBasicInfo {

    @ValueMapValue
    private String baseUrl;

    @ValueMapValue
    private String munchkinId;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getMunchkinId() {
        return munchkinId;
    }
}
