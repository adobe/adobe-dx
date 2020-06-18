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

package com.adobe.dx.admin.servlet;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.adobe.dx.admin.mocks.MockSlingXssApi;
import com.adobe.dx.mocks.MockCryptoSupport;
import com.adobe.dx.testing.AbstractTest;
import com.adobe.granite.crypto.CryptoSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.apache.sling.xss.XSSAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EncryptionServletTest extends AbstractTest {

    private MockSlingHttpServletResponse response;

    private MockSlingHttpServletRequest request;

    private EncryptionServlet encryptionServlet = new EncryptionServlet();
    private MockCryptoSupport mockCryptoSupport = new MockCryptoSupport();

    @BeforeEach
    private void setup() {
        context.load().json("/mocks/encryptionServlet/encryptionComp.json",
            "/apps/dx/services/private/encryptValues");
        context.currentResource( "/apps/dx/services/private/encryptValues");
        context.registerService(CryptoSupport.class, mockCryptoSupport);
        context.registerService(XSSAPI.class, new MockSlingXssApi());
        request = context.request();
        response = context.response();
        context.registerInjectActivateService(encryptionServlet);
    }

    @Test
    void testNoReqParamEncryption() throws IOException {
        triggerAndAssertResponse(SC_BAD_REQUEST, "{\"error\" : \"encryption issue\"}");
    }

    @Test
    void testEmptyReqParamEncryption() throws IOException {
        request.setParameterMap(generateRequestMap( ""));
        triggerAndAssertResponse(SC_BAD_REQUEST, "{\"error\" : \"encryption issue\"}");
    }

    @Test
    void testSingleValueEncryption() throws IOException {
        request.setParameterMap(generateRequestMap("someValue"));
        triggerAndAssertResponse(SC_OK, "{\"encrypted\" : \"protected_someValue\"}");
    }

    @Test
    void testSingleJsonEncryption() throws IOException {
        request.setParameterMap(generateRequestMap("{\"first\" : \"firstValue\", "
            + "\"second\" : \"secondValue\"}"));
        triggerAndAssertResponse(SC_OK, "{\"first\":\"protected_firstValue\","
            + "\"second\":\"protected_secondValue\"}");
    }

    @Test
    void testAnythingOtherThanJson() throws IOException {
        request.setParameterMap(generateRequestMap("[\"someValue\"]"));
        triggerAndAssertResponse(SC_OK, "{\"encrypted\" : \"protected_[\"someValue\"]\"}");
    }

    @Test
    void testWhenCryptoSupportFailsForSingleValue() throws IOException {
        mockCryptoSupport.setException();
        request.setParameterMap(generateRequestMap("someValue"));
        triggerAndAssertResponse(SC_OK, "{\"encrypted\" : \"\"}");
    }

    @Test
    void testWhenCryptoSupportFailsForJson() throws IOException {
        mockCryptoSupport.setException();
        request.setParameterMap(generateRequestMap("{\"first\" : \"firstValue\", "
            + "\"second\" : \"secondValue\"}"));
        triggerAndAssertResponse(SC_OK, "{\"first\":\"\",\"second\":\"\"}");
    }

    private void triggerAndAssertResponse(int status, String responseString) throws IOException {
        encryptionServlet.doGet(request, response);
        assertEquals(status, response.getStatus());
        assertEquals(responseString, response.getOutputAsString());
    }

    private Map<String, Object> generateRequestMap(String value) {
        Map<String, Object> ret = new HashMap<>();
        ret.put("toBeEncrypted", value);
        return ret;
    }


}
