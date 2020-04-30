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

package com.adobe.dx.structure.flex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.adobe.dx.testing.AbstractTest;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

public class FlexModelTest extends AbstractTest {

    ResourceResolver resolver;

    @BeforeEach
    public void setUp() {
        context.load().json("/mocks/flexcontainer/flexcontainer.json", "/content");
        context.addModelsForClasses(FlexModel.class);
        resolver = context.resourceResolver();
    }

    @Test
    public void testEmpty() {
        Resource resource = resolver.getResource("/content/flex_empty");
        FlexModel flex = resource.adaptTo(FlexModel.class);
        assertNotNull(flex);
    }

//    @Test
//    public void testId() {
//        Resource resource = resolver.getResource("/content/flex_id");
//        FlexModel flex = resource.adaptTo(FlexModel.class);
//        assertEquals("dc3fa17f4", flex.getId(), "ID should have an alpha prefix");
//    }
}
