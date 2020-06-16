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

package com.adobe.dx.content.marketo.service.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adobe.dx.content.marketo.mocks.service.MockMarketoFormData;
import com.adobe.dx.content.marketo.models.internal.MarketoConfBasicInfo;
import com.adobe.dx.content.marketo.models.internal.MarketoConfDetailedInfo;
import com.adobe.dx.content.marketo.service.MarketoClientService;
import com.adobe.dx.content.marketo.service.MarketoFormData;
import com.adobe.dx.content.mocks.MockCloudConfigReader;
import com.adobe.dx.mocks.MockCryptoSupport;
import com.adobe.dx.testing.AbstractTest;
import com.adobe.dx.utils.service.CloudConfigReader;
import com.adobe.granite.crypto.CryptoSupport;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MarketoServiceImplTest extends AbstractTest {

    @SuppressWarnings("squid:S1075")
    private static final String RESOURCE_PATH = "/content/dx/us/en/somePageWithMarketo";

    private static final String CLOUD_CONFIG = "/conf/global/settings/cloudconfigs/marketo-config";

    private MarketoServiceImpl marketoServiceImpl = new MarketoServiceImpl();

    private MockMarketoClientService mockMarketoClientService = new MockMarketoClientService();

    private  MockCloudConfigReader mockCloudConfigReader = new MockCloudConfigReader();


    @BeforeEach
    private void setup() {
        context.load().json("/mocks/marketo/cloudconfig.json",
            CLOUD_CONFIG);
        Resource config = context.resourceResolver()
            .getResource(CLOUD_CONFIG + "/jcr:content");
        context.addModelsForClasses(MarketoConfBasicInfo.class);
        context.addModelsForClasses(MarketoConfDetailedInfo.class);
        context.registerService(CryptoSupport.class, new MockCryptoSupport());
        if (null != config) {
            mockCloudConfigReader.setWhatToReturn(RESOURCE_PATH,
                "marketo-config",
                config.adaptTo(MarketoConfDetailedInfo.class));
            context.registerService(CloudConfigReader.class, mockCloudConfigReader);
        }
        context.registerService(MarketoClientService.class, mockMarketoClientService);
        context.registerInjectActivateService(marketoServiceImpl);
    }

    @Test
    void testWhenClientDataIsGood() {
        mockMarketoClientService.setReturnConditions(true, true);
        List<MarketoFormData> forms = marketoServiceImpl.getMarketoForms(RESOURCE_PATH);
        new FormDataValidator().validateFormData(forms, Arrays.asList("form1", "form2", "form3"),
            Arrays.asList(1, 2, 3),
            Arrays.asList("de_DE", "en_US", "en_GB"));
        assertEquals(0, mockMarketoClientService.getRetries());
    }

    @Test
    void testWhenClientDataIsEmpty() {
        mockMarketoClientService.setReturnConditions(true, false);
        assertFormIsEmptyAndRetryCountIs(0);
    }

    @Test
    void testRetryAttemptsWhenTokenIsInValid() {
        mockMarketoClientService.setReturnConditions(false, true);
        assertFormIsEmptyAndRetryCountIs(3);
    }

    @Test
    void testWhenClientFormsResultsIsNullAndTokenInvalid() {
        mockMarketoClientService.setNullFormResult(false);
        assertFormIsEmptyAndRetryCountIs(3);
    }

    @Test
    void testWhenClientFormsResultsIsNullAndTokenValid() {
        mockMarketoClientService.setNullFormResult(true);
        assertFormIsEmptyAndRetryCountIs(0);
    }

    @Test
    void testWhenMarketoConfigIsNotAvailable() {
        mockCloudConfigReader.setWhatToReturn(RESOURCE_PATH,
            "marketo-config", null);
        assertFormIsEmptyAndRetryCountIs(-1);
    }

    private void assertFormIsEmptyAndRetryCountIs(int retryCount) {
        List<MarketoFormData> forms = marketoServiceImpl.getMarketoForms(RESOURCE_PATH);
        assertTrue(CollectionUtils.isEmpty(forms));
        assertEquals(retryCount, mockMarketoClientService.getRetries());
    }

    private static class MockMarketoClientService implements MarketoClientService {

        private boolean validToken;

        private boolean validForm;

        private boolean nullForm;

        private int retry = -1;

        void setReturnConditions(boolean validToken, boolean validForm) {
            this.validToken = validToken;
            this.validForm = validForm;
            retry = -1;
        }

        void setNullFormResult(boolean validToken) {
            this.validToken = validToken;
            nullForm = true;
            retry = -1;
        }

        int getRetries() {
            return retry;
        }

        @Override
        public MarketoAccessToken getAuthToken(String baseUrl, String clientId, String clientSecret) {
            Calendar validUntil = Calendar.getInstance();
            int minutesAdjust = validToken ? 60 : -60;
            validUntil.add(Calendar.MINUTE, minutesAdjust);
            return new MockMarketoAccessToken("token", validUntil);
        }

        @Override
        public MarketoForms getMarketoForms(String baseUrl, String authToken) {
            retry++;
            if (nullForm) {
                return  new MockMarketoForms(false, !validToken, null, "Error");
            }
            return new MockMarketoForms(validForm, !validToken, validForm && validToken  ?
                Arrays.asList(new MockMarketoFormData("form1", 1, "de_DE"),
                new MockMarketoFormData("form2", 2, "en_US"),
                new MockMarketoFormData("form3", 3, "en_GB")) : Collections.emptyList(),
                "Some Error");
        }

        private static class MockMarketoAccessToken implements MarketoClientService.MarketoAccessToken {

            private String accessToken;

            private Calendar validUntil;

            MockMarketoAccessToken(String accessToken, Calendar validUntil) {
                this.accessToken = accessToken;
                this.validUntil = validUntil;
            }

            @Override
            public String getAccessToken() {
                return accessToken;
            }

            @Override
            public Calendar getValidUntil() {
                return validUntil;
            }
        }

        private static class MockMarketoForms implements MarketoClientService.MarketoForms {

            private boolean success;

            private boolean isTokenInvalid;

            private List<MarketoFormData> result;

            private String errorMessage;

            MockMarketoForms(boolean success, boolean isTokenInvalid, List<MarketoFormData> result,
                             String errorMessage) {
                this.success = success;
                this.isTokenInvalid = isTokenInvalid;
                this.result = result;
                this.errorMessage = errorMessage;
            }

            @Override
            public boolean isSuccess() {
                return success;
            }

            @Override
            public boolean isTokenInvalid() {
                return isTokenInvalid;
            }

            @Override
            public List<MarketoFormData> getResult() {
                return result;
            }

            @Override
            public String getErrorMessage() {
                return errorMessage;
            }
        }
    }
}
