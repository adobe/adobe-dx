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
package com.adobe.dx.testing;

import static com.adobe.dx.testing.AbstractTest.CONTENT_ROOT;
import static com.adobe.dx.testing.AbstractTest.buildContext;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class AbstractRequestModelTestTest {

    AemContext context = buildContext();
    AbstractRequestModelTest test;

    @BeforeEach
    public void setup() {
        test = new AbstractRequestModelTest();
        test.context = context;
    }

    @Test
    public void testGetExistingResource() throws Exception {
        context.build().resource(CONTENT_ROOT).commit();
        context.currentResource(context.resourceResolver().getResource(CONTENT_ROOT));
        TestModel model = test.getModel(TestModel.class);
        assertEquals(CONTENT_ROOT, model.resource.getPath());
    }

    @Test
    public void testGetNonExistingResource() throws Exception {
        TestModel model = test.getModel(TestModel.class);
        assertNull(model.resource);
    }

    @Model(adaptables = { SlingHttpServletRequest.class })
    public static class TestModel {

        @SlingObject
        Resource resource;
    }

}