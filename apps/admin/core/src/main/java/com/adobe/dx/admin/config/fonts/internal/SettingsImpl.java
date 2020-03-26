/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.dx.admin.config.fonts.internal;

import com.adobe.dx.admin.config.fonts.Settings;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = {
        Resource.class
    },
    adapters = {
        Settings.class
    }
)
public class SettingsImpl implements Settings {

    private static final String BASE_URL = "https://use.typekit.net";

    @Self(injectionStrategy = InjectionStrategy.REQUIRED)
    private Resource resource;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String projectId;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "linkTag")
    private String embedType;

    @Override
    public String getId() {
        return projectId;
    }

    @Override
    public String getEmbedType() {
        return embedType;
    }

    @Override
    public String getUrl() {
        return BASE_URL;
    }
}
