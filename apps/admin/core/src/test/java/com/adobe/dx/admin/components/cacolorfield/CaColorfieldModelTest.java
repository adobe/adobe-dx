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
package com.adobe.dx.admin.components.cacolorfield;

import static org.junit.jupiter.api.Assertions.*;

import com.adobe.dx.admin.datasource.internal.ContextAwareDatasource;
import com.adobe.dx.testing.AbstractRequestModelTest;

import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CaColorfieldModelTest extends AbstractRequestModelTest {

    @BeforeEach
    public void setup() {
        String dialogPath = "/apps/my/color/field";
        context.build().resource(CONTENT_ROOT, "./myColor", "blue");
        context.create().resource(dialogPath, "name", "./myColor");
        context.currentResource(dialogPath);
        MockSlingHttpServletRequest request = context.request();
        ((MockRequestPathInfo)request.getRequestPathInfo()).setSuffix(CONTENT_ROOT);
        context.addModelsForClasses(ContextAwareDatasource.class);
    }

    @Test
    public void testStandardUsage() {
        CaColorfieldModel model = getModel(CaColorfieldModel.class);
        assertNotNull(model);
        assertEquals("./myColor", model.getName());
    }
}