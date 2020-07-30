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

package com.adobe.dx.content.marketo.models;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.adobe.dx.content.marketo.models.internal.MarketoConfBasicInfo;
import com.adobe.dx.content.mocks.MockCloudConfigReader;
import com.adobe.dx.testing.AbstractRequestModelTest;
import com.adobe.dx.utils.service.CloudConfigReader;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MarketoFooterTest extends AbstractRequestModelTest {

    @SuppressWarnings("squid:S1075")
    private static final String CURRENT_PAGE_PATH = "/content/dx/us/en/somePageWithMarketo";

    private static final String CLOUD_CONFIG = "/conf/global/settings/cloudconfigs/marketo-config";

    @BeforeEach
    public void setup() {
        context.load().json("/mocks/marketo/cloudconfig.json",
            CLOUD_CONFIG);
        context.load().json("/mocks/marketo/simplepage.json",
            CURRENT_PAGE_PATH);
        Resource config = context.resourceResolver()
            .getResource(CLOUD_CONFIG + "/jcr:content");
        MockCloudConfigReader mockCloudConfigReader = new MockCloudConfigReader();
        if (null != config) {
            mockCloudConfigReader.setWhatToReturn(CURRENT_PAGE_PATH,
                "marketo-config",
                config.adaptTo(MarketoConfBasicInfo.class));
        }
        context.registerService(CloudConfigReader.class, mockCloudConfigReader);
    }

    @Test
    void testCloudConfigLoads() throws ReflectiveOperationException {
        MarketoFooter footer = getModel(MarketoFooter.class, CURRENT_PAGE_PATH);
        MarketoConfBasicInfo marketoConfig = footer.getMarketoConfBasicInfo();
        assertNotNull(marketoConfig);
        assertArrayEquals(new String[]{"dx/content/components/marketo"}, marketoConfig.getMarketoComponentTypes());
        assertEquals("//app-ab12.marketo.com", marketoConfig.getBaseUrl());
        assertEquals("123-shs-456", marketoConfig.getMunchkinId());
    }
}
