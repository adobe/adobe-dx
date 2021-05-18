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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.any;

import com.adobe.dx.testing.AbstractRequestModelTest;
import com.day.cq.commons.Externalizer;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MarketoFormTest extends AbstractRequestModelTest {
    private static final String CURRENT_PAGE_PATH = "/content/dx/us/en/somePageWithMarketo";
    @BeforeEach
    public void setup() {
        context.load().json("/mocks/marketo/form.json", CURRENT_PAGE_PATH);
        context.currentPage(CURRENT_PAGE_PATH);
        Externalizer externalizer = mock(Externalizer.class);
        when(externalizer.publishLink(any(ResourceResolver.class), anyString())).thenReturn("/dest");
        context.registerService(Externalizer.class, externalizer);
    }

    @Test
    void testCloudConfigLoads() throws ReflectiveOperationException {
        MarketoForm form = getModel(MarketoForm.class, CURRENT_PAGE_PATH + "/jcr:content/marketo");
        assertEquals(form.isProfiling(), true);
        assertEquals(form.getMarketoFormId(), "1234");
        assertEquals(form.isAutoSubmitForm(), true);
        assertEquals(form.getSubmitText(), "Submit");
        assertEquals(form.getDestinationUrl(), "/dest");
    }
}
