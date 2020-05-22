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

import static org.apache.commons.lang.StringUtils.EMPTY;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class)
public class MarketoConfDetailedInfo extends MarketoConfBasicInfo {

    private static final Logger LOG = LoggerFactory.getLogger(MarketoConfDetailedInfo.class);

    @OSGiService
    private CryptoSupport cryptoSupport = null;

    @ValueMapValue
    private String clientId = null;

    @ValueMapValue
    private String restApiBaseUrl = null;

    @ValueMapValue
    private String clientSecret;

    @PostConstruct
    private void init() {
        clientSecret = decrypt(clientSecret).orElse(EMPTY);
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRestApiBaseUrl() {
        return restApiBaseUrl;
    }

    public String getClientInfo() {
        return getRestApiBaseUrl() + clientId + clientSecret;
    }

    private Optional<String> decrypt(String propertyValue) {
        try {
            if (cryptoSupport.isProtected(propertyValue)) {
                return Optional.of(cryptoSupport.unprotect(propertyValue));
            }
        } catch (CryptoException e) {
            LOG.error("Error while decrypting: ", e);
        }
        return Optional.empty();
    }

}
