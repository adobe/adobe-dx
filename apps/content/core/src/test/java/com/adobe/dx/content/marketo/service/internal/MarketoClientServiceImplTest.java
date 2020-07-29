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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adobe.dx.content.marketo.mocks.service.FakeMarketoHttpServer;
import com.adobe.dx.content.marketo.service.MarketoClientService;
import com.adobe.dx.testing.AbstractTest;

import java.util.Arrays;
import java.util.Calendar;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MarketoClientServiceImplTest extends AbstractTest {

    private MarketoClientServiceImpl marketoClientService = new MarketoClientServiceImpl();

    private static boolean serverStarted = true;

    private static FakeMarketoHttpServer fakeMarketoHttpServer = null;

    private static final int DEFAULT_PORT = 8123;

    private static final String TEST_HOST = "http://localhost:" + DEFAULT_PORT;

    @BeforeAll
    private static void startFakeMarketoServer() throws Exception {
        fakeMarketoHttpServer = new FakeMarketoHttpServer(DEFAULT_PORT, "/");
        try {
            fakeMarketoHttpServer.start();
        } catch (Exception e) {
            serverStarted = false;
        }
        Assumptions.assumeTrue(serverStarted);
    }

    @AfterAll
    private static void stopFakeServer() {
        if (null != fakeMarketoHttpServer) {
            fakeMarketoHttpServer.stop();
        }
    }

    @BeforeEach
    private void setUp() {
        context.registerInjectActivateService(marketoClientService);
    }

    @Test
    void testAuthTokenValidResponse() {
        Calendar approxValidUntilThreshold = Calendar.getInstance();
        approxValidUntilThreshold.add(Calendar.MINUTE, 40);
        MarketoClientService.MarketoAccessToken authToken = marketoClientService.getAuthToken(TEST_HOST,
            "validClient", "validClientSecret");
        assertEquals("validTokenValue" , authToken.getAccessToken());
        assertTrue(authToken.getValidUntil().after(approxValidUntilThreshold));
    }


    @Test
    void testAuthTokenFailureResponse() {
        MarketoClientService.MarketoAccessToken authToken = marketoClientService.getAuthToken(TEST_HOST,
            "validClient", "invalidSecret");
        assertNull(authToken);
    }

    @Test
    void testFormsValidResponse() {
        MarketoClientService.MarketoForms formsData =
            marketoClientService.getMarketoForms(TEST_HOST, "validAuthToken");
        assertTrue(formsData.isSuccess());
        assertFalse(formsData.isTokenInvalid());
        assertTrue(formsData.getResult().get(0).toString().contains("1 (Form 1)"));
        new FormDataValidator().validateFormData(formsData.getResult(), Arrays.asList("Form 1", "Form 2"),
            Arrays.asList(1, 2),
            Arrays.asList("en_US", "de_DE"));
        assertEquals("", formsData.getErrorMessage());
    }

    @Test
    void testFormsFailureResponse() {
        MarketoClientService.MarketoForms formsData =
            marketoClientService.getMarketoForms(TEST_HOST, "invalidAuthToken");
        assertFalse(formsData.isSuccess());
        assertTrue(formsData.isTokenInvalid());
        assertNull(formsData.getResult());
        assertEquals("[601 Access token invalid]", formsData.getErrorMessage());
    }

}
