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
package com.adobe.dx.admin.config.fonts.impl;

import javax.annotation.PostConstruct;

import com.adobe.dx.admin.config.fonts.Settings;
import com.adobe.dx.admin.config.fonts.SettingsProvider;
import com.adobe.dx.admin.config.fonts.Tag;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = { SlingHttpServletRequest.class }, adapters = { Tag.class })
public class TagImpl implements Tag {

    private static final String configName = "adobe-fonts";

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private SettingsProvider settingsProvider;

    private Settings settings;

    @PostConstruct
    public void postConstruct() {
        settings = settingsProvider.getSettings(request, configName);
    }

    @Override
    public String getId() {
        if (settings != null) {
            return settings.getId();
        }
        return null;
    }

    @Override
    public String getEmbedType() {
        return settings.getEmbedType();
    }

    @Override
    public String getUrl() {
        return settings.getUrl();
    }
}
