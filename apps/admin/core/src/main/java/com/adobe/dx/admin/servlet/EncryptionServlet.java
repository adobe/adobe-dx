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
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.sling.api.servlets.HttpConstants.METHOD_GET;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.adobe.xfa.ut.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.xss.XSSAPI;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = Servlet.class,
    property = {
        SLING_SERVLET_RESOURCE_TYPES + "=dx/components/services/encryption",
        SLING_SERVLET_METHODS + "=" + METHOD_GET})
public class EncryptionServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(EncryptionServlet.class);

    private static final String TO_BE_ENCRYPTED = "toBeEncrypted";

    private static final String SIMPLE_JSON_TEMPLATE = "{\"protected\" : \"protectedValue\"}";

    private static final String ERROR = "{\"error\" : \"encryption issue\"}";

    private static final String MIME_APPLICATION_JSON = "application/json";

    private static final String UTF8_ENCODING_NAME = "UTF-8";

    @Reference
    private transient CryptoSupport cryptoSupport = null;

    @Reference
    private transient XSSAPI xssApi = null;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
        throws IOException {
        try {
            String valueToBeEncrypted = request.getParameter(TO_BE_ENCRYPTED);
            if (StringUtils.isEmpty(valueToBeEncrypted)) {
                LOG.error("Error in Encryption Servlet: Request Param missing");
                writeResponse(response, SC_BAD_REQUEST, ERROR);
                return;
            }
            Map toBeConverted = getJsonMap(valueToBeEncrypted);
            String responseStr = (null == toBeConverted)
                ? encryptPlainStr(valueToBeEncrypted) : encryptJsonMap(toBeConverted);
            writeResponse(response, SC_OK, responseStr);
        } catch(Exception e) {
            LOG.error("Error in Encryption Servlet: ", e);
            writeResponse(response, SC_INTERNAL_SERVER_ERROR, ERROR);
        }
    }

    private String encryptPlainStr(String valueToBeEncrypted) {
        return SIMPLE_JSON_TEMPLATE.replace("protectedValue", encrypt(valueToBeEncrypted).orElse(EMPTY));
    }

    @SuppressWarnings("unchecked")
    private String encryptJsonMap(Map toBeConverted) throws JsonProcessingException {
        Map<String, String> encryptedMap = new HashMap<>();
        toBeConverted.forEach((k,v) -> encryptedMap.put(xssApi.encodeForJSString(String.valueOf(k)),
            encrypt(String.valueOf(v)).orElse(EMPTY)));
        return new ObjectMapper().writeValueAsString(encryptedMap);
    }

    private void writeResponse(SlingHttpServletResponse response, int code, String responseJson) throws IOException {
        response.setStatus(code);
        response.setContentType(MIME_APPLICATION_JSON);
        response.setCharacterEncoding(UTF8_ENCODING_NAME);
        response.getWriter().write(responseJson);
    }

    private Map getJsonMap(String valueToBeEncrypted) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
           return objectMapper.readValue(valueToBeEncrypted, Map.class);
        } catch (IOException e) {
           return null;
        }
    }

    private Optional<String> encrypt(String authToken) {
        try {
            return Optional.of(cryptoSupport.protect(authToken));
        } catch (CryptoException e) {
            LOG.error("Error while encrypting: ", e);
        }
        return Optional.empty();
    }

}   
