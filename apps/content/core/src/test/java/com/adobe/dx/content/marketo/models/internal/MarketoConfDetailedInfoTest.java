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

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.adobe.dx.mocks.MockCryptoSupport;
import com.adobe.dx.testing.AbstractTest;
import com.adobe.granite.crypto.CryptoSupport;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MarketoConfDetailedInfoTest extends AbstractTest {

    private static final String CLOUD_CONFIG = "/conf/global/settings/cloudconfigs/marketo-config";

    private Resource config = null;

    private MockCryptoSupport mockCryptoSupport =  new MockCryptoSupport();

    @BeforeEach
    private void setup() {
        context.load().json("/mocks/marketo/cloudconfig.json", CLOUD_CONFIG);
        config = context.resourceResolver().getResource(CLOUD_CONFIG + "/jcr:content");
        context.registerService(CryptoSupport.class, mockCryptoSupport);
        context.addModelsForClasses(MarketoConfBasicInfo.class);
        context.addModelsForClasses(MarketoConfDetailedInfo.class);
        assertNotNull(config);
    }

    @Test
    void testModelDataWithSuccessfulDecryption() {
        testAllProps("validSecret");
    }

    @Test
    void testModelDataDecryptionIssue() {
        mockCryptoSupport.setException();
        testAllProps(EMPTY);
    }

    private void testAllProps(String expectedDecryptedValue) {
        MarketoConfDetailedInfo marketoConfDetailedInfo = config.adaptTo(MarketoConfDetailedInfo.class);
        assertNotNull(marketoConfDetailedInfo);
        assertEquals(marketoConfDetailedInfo.getClientId() , "validClient");
        assertEquals(marketoConfDetailedInfo.getClientSecret() , expectedDecryptedValue);
        assertEquals(marketoConfDetailedInfo.getRestApiBaseUrl() , "//123-shs-456.mktorest.com");
        assertEquals(marketoConfDetailedInfo.getClientInfo() , "//123-shs-456.mktorest.comvalidClient"
            + expectedDecryptedValue);
        assertEquals(marketoConfDetailedInfo.getBaseUrl() , "//app-ab12.marketo.com");
    }
}
