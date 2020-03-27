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

import static org.junit.jupiter.api.Assertions.*;

import com.adobe.dx.admin.config.fonts.Tag;
import com.adobe.dx.testing.AbstractRequestModelTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TagImplTest extends AbstractRequestModelTest {
    @BeforeEach
    private void setup() {
        context.load().json("/mocks/admin.adobefonts/configuration-tree.json", CONF_ROOT);
        context.load().json("/mocks/admin.adobefonts/content-tree.json", CONTENT_ROOT);
        context.addModelsForPackage(TagImpl.class.getPackage().getName());
        context.registerInjectActivateService(new SettingsProviderImpl());
        context.currentResource(CONTENT_ROOT + "/us/en");
    }

    @Test
    public void basicTest() throws ReflectiveOperationException {
        Tag tag = getModel(Tag.class);
        assertNotNull(tag);
        assertEquals("foo", tag.getEmbedType());
        assertEquals("https://use.typekit.net", tag.getUrl());
        assertEquals("bar", tag.getId());
    }
}