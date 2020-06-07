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

import com.adobe.dx.content.marketo.models.internal.MarketoConfDetailedInfo;
import com.adobe.dx.content.marketo.service.MarketoClientService;
import com.adobe.dx.content.marketo.service.MarketoClientService.MarketoAccessToken;
import com.adobe.dx.content.marketo.service.MarketoClientService.MarketoForms;
import com.adobe.dx.content.marketo.service.MarketoFormData;
import com.adobe.dx.content.marketo.service.MarketoService;
import com.adobe.dx.utils.service.CloudConfigReader;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = MarketoService.class)
public class MarketoServiceImpl implements MarketoService {

    private static final Logger LOG = LoggerFactory.getLogger(MarketoServiceImpl.class);

    private static final String CONFIG_NAME = "marketo-config";

    private static final int MAX_RETRIES = 3;

    @Reference
    private CloudConfigReader cloudConfigReader = null;

    @Reference
    private MarketoClientService marketoClientService = null;

    private Map<String, MarketoAccessToken> authTokenMap = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public List<MarketoFormData> getMarketoForms(@NotNull String resourcePath) {
        MarketoConfDetailedInfo marketoDetailedInfo = cloudConfigReader.getContextAwareCloudConfigRes(resourcePath,
            CONFIG_NAME, MarketoConfDetailedInfo.class);
        if (null != marketoDetailedInfo && StringUtils.isNotEmpty(marketoDetailedInfo.getClientSecret())) {
            MarketoForms marketoForms = retrieveMarketoForms(marketoDetailedInfo);
            if (null != marketoForms) {
                if (!marketoForms.isSuccess()) {
                    LOG.error("There was an error when trying to retrieve marketo forms {}",
                        marketoForms.getErrorMessage());
                    return Collections.emptyList();
                }
                return Optional.ofNullable(marketoForms.getResult()).orElse(Collections.emptyList());
            }
        }
        return Collections.emptyList();
    }

    private MarketoForms retrieveMarketoForms(MarketoConfDetailedInfo marketoDetailedInfo) {
        MarketoForms marketoForms = getMarketoFormsFromClient(marketoDetailedInfo);
        marketoForms = retryFetchForm(marketoForms, marketoDetailedInfo);
        return marketoForms;
    }

    private MarketoForms retryFetchForm(MarketoForms marketoForms, MarketoConfDetailedInfo marketoDetailedInfo) {
        int retries = 0;
        while (null != marketoForms && marketoForms.isTokenInvalid() && retries < MAX_RETRIES) {
            marketoForms = getMarketoFormsFromClient(marketoDetailedInfo);
            retries++;
        }
        return marketoForms;
    }

    private MarketoForms getMarketoFormsFromClient(MarketoConfDetailedInfo marketoDetailedInfo) {
        String authToken = getAuthToken(marketoDetailedInfo);
        // Prevent Forms call if auth token was not available.
        if (StringUtils.isNotEmpty(authToken)) {
            return marketoClientService.getMarketoForms(marketoDetailedInfo.getRestApiBaseUrl(),
                getAuthToken(marketoDetailedInfo));
        }
        return null;
    }

    private String getAuthToken(MarketoConfDetailedInfo marketoDetailedInfo) {
        MarketoAccessToken authToken = readAuthToken(marketoDetailedInfo);
        if (authToken == null || isAuthTokenInValid(authToken.getAccessToken(), authToken.getValidUntil())) {
            authToken = writeAuthToken(marketoDetailedInfo);
        }
        return null != authToken ? authToken.getAccessToken() : EMPTY;
    }

    private boolean isAuthTokenInValid(String authToken, Calendar authValidUntil) {
        return StringUtils.isEmpty(authToken) || Calendar.getInstance().after(authValidUntil);
    }

    private MarketoAccessToken readAuthToken(MarketoConfDetailedInfo marketoDetailedInfo) {
        lock.readLock().lock();
        try {
           return authTokenMap.get(marketoDetailedInfo.getClientInfo());
        } finally {
            lock.readLock().unlock();
        }
    }

    private MarketoAccessToken writeAuthToken(MarketoConfDetailedInfo marketoDetailedInfo) {
        lock.writeLock().lock();
        try {
            return updateAuthTokenMap(marketoDetailedInfo);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private MarketoAccessToken updateAuthTokenMap(MarketoConfDetailedInfo marketoDetailedInfo) {
        String clientInfo = marketoDetailedInfo.getClientInfo();
        MarketoAccessToken authToken = authTokenMap.get(clientInfo);
        if (authToken == null || isAuthTokenInValid(authToken.getAccessToken(), authToken.getValidUntil())) {
            authToken = fetchAuthToken(marketoDetailedInfo);
            if (null != authToken) {
                authTokenMap.put(clientInfo, authToken);
            }
        }
        return authToken;
    }

    private MarketoAccessToken fetchAuthToken(MarketoConfDetailedInfo marketoDetailedInfo) {
        return marketoClientService.getAuthToken(marketoDetailedInfo.getRestApiBaseUrl(),
            marketoDetailedInfo.getClientId(),
            marketoDetailedInfo.getClientSecret());
    }

}
