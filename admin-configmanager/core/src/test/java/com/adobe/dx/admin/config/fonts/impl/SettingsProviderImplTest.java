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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.adobe.dx.admin.config.fonts.Settings;
import com.adobe.dx.admin.config.fonts.SettingsProvider;
import com.adobe.dx.testing.AbstractTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SettingsProviderImplTest extends AbstractTest {

    SettingsProvider provider;

    @BeforeEach
    private void setup() {
        context.load().json("/mocks/admin.adobefonts/configuration-tree.json", CONF_ROOT);
        context.load().json("/mocks/admin.adobefonts/content-tree.json", CONTENT_ROOT);
        context.addModelsForPackage("com.adobe.dx.admin.config.fonts.impl");
        context.currentResource(CONTENT_ROOT + "/us/en");
        provider = new SettingsProviderImpl();
        context.registerInjectActivateService(provider);
    }

    @Test
    public void getSettingTest() {
        Settings settings = provider.getSettings(context.request(), "some");
        assertNotNull(settings);
        assertEquals("linkTag", settings.getEmbedType());
        assertEquals("https://use.typekit.net", settings.getUrl());
        assertEquals("foo", settings.getId());
    }

    @Test
    public void getNoSettings() {
        Settings settings = provider.getSettings(context.request(), "not existing");
        assertNull(settings);
    }

}