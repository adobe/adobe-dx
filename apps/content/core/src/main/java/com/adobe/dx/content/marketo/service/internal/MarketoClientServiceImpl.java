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

import static org.apache.commons.lang.StringUtils.EMPTY;

import com.adobe.dx.content.marketo.service.MarketoClientService;
import com.adobe.dx.content.marketo.service.MarketoFormData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE, service = MarketoClientService.class)
public class MarketoClientServiceImpl implements MarketoClientService {

    private static final Logger LOG = LoggerFactory.getLogger(MarketoClientServiceImpl.class);

    private static final String IDENTITY_REST_API = "/identity/oauth/token?grant_type=client_credentials";

    private static final String FORMS_REST_API = "/rest/asset/v1/forms.json?access_token=";

    private static final int MAX_FORMS = 200;

    private static final int OFFSET_BUFFER = 5;

    private static final String PROTOCOL = "https:";


    @Override
    public MarketoAccessToken getAuthToken(String baseUrl, String clientId, String clientSecret) {
        String authTokenUrl = PROTOCOL + baseUrl + IDENTITY_REST_API
            + "&client_id=" + clientId + "&client_secret=" + clientSecret;
        Calendar executionCallStart = Calendar.getInstance();
        MarketoAccessTokenInstance accessTokenInstance = executeCall(authTokenUrl, MarketoAccessTokenInstance.class);
        if (null != accessTokenInstance) {
            executionCallStart.add(Calendar.SECOND, accessTokenInstance.getExpiresIn() - OFFSET_BUFFER);
            accessTokenInstance.setValidUntil(executionCallStart);
        }
        return accessTokenInstance;
    }

    @Override
    public MarketoForms getMarketoForms(String baseUrl, String authToken) {
        return executeCall(PROTOCOL + baseUrl + FORMS_REST_API
            + authToken + "&maxReturn=" + MAX_FORMS, MarketoForms.class);
    }

    private <T> T  executeCall(String url, Class<T> mapperClass) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try (CloseableHttpResponse httpResponse = httpClient.execute(new HttpGet(url))) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if(HttpStatus.SC_OK == statusCode) {
                ObjectMapper objectMapper = setupObjectMapper();
                InputStream content = httpResponse.getEntity().getContent();
                return objectMapper.readValue(content, mapperClass);
            } else {
                LOG.error("The response from Marketo had issues. Status Code - {}", statusCode);
                return null;
            }
        } catch (IOException e) {
            LOG.error("IO Exception when reading response ", e);
        }
        return null;
    }

    private ObjectMapper setupObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addAbstractTypeMapping(MarketoAccessToken.class, MarketoAccessTokenInstance.class);
        module.addAbstractTypeMapping(MarketoForms.class, MarketoFormsInstance.class);
        module.addAbstractTypeMapping(MarketoFormData.class, MarketoFormDataInstance.class);
        objectMapper.registerModule(module);
        return objectMapper;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SuppressWarnings("UnusedDeclaration")
    private static class MarketoAccessTokenInstance implements MarketoAccessToken {

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("expires_in")
        private int expiresIn;

        private Calendar validUntil;

        @Override
        public String getAccessToken() {
            return accessToken;
        }

        @Override
        public Calendar getValidUntil() {
            return validUntil;
        }

        int getExpiresIn() {
            return expiresIn;
        }

        void setValidUntil(Calendar validUntil) {
            this.validUntil = validUntil;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SuppressWarnings("UnusedDeclaration")
    private static class MarketoFormsInstance implements MarketoForms {

        @JsonProperty("success")
        private boolean isSuccess;
        private List<MarketoFormData> result;
        private List<MarketoFormError> errors = Collections.emptyList();

        @Override
        public boolean isSuccess() {
            return isSuccess;
        }

        @Override
        public List<MarketoFormData> getResult() {
            return result;
        }

        @Override
        public String getErrorMessage() {
            return null != errors ? errors.toString() : EMPTY;
        }

        @Override
        public boolean isTokenInvalid() {
            return  null != errors && errors.stream()
                .anyMatch(error -> StringUtils.equalsAny(error.getCode(), "600", "601"));
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SuppressWarnings("UnusedDeclaration")
    private static class MarketoFormError {

        private String code;
        private String message;

        String getCode() {
            return code;
        }

        String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return code + " " + message;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SuppressWarnings("UnusedDeclaration")
    private static class MarketoFormDataInstance implements MarketoFormData {

        private int id;
        private String name;
        private String locale;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String getLocale() {
            return locale;
        }

        @Override
        public String toString() {
            return id + " (" + name + ")" + " (" + locale + ")";
        }
    }

}   
