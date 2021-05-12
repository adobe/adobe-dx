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

package com.adobe.dx.utils.service.internal;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;

import com.adobe.dx.utils.service.CloudConfigReader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = CloudConfigReader.class)
public class CloudConfigReaderImpl implements CloudConfigReader {

    private static final Logger LOG = LoggerFactory.getLogger(CloudConfigReaderImpl.class);

    private static final String BUCKET_NAME = "settings/cloudconfigs";

    private static final Map<String, Object> SERVICE_USER = Collections.singletonMap(SUBSERVICE, "readService");

    @Reference
    private ConfigurationResourceResolver configurationResolver = null;

    @Reference
    private ResourceResolverFactory resourceResolverFactory = null;

    @Override
    public <T> T getContextAwareCloudConfigRes(@NotNull String resourcePath, String configName, Class<T> type) {
        try (ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(SERVICE_USER)) {
            return getContextAwareCloudConfigRes(resolver, resourcePath, configName, type);
        } catch (LoginException e) {
            LOG.error("Login Exception occurred when reading config ", e);
        }
        return null;
    }

    @Override
    public Map<String, Object> getContextAwareCloudConfigRes(@NotNull String resourcePath, String configName) {
        try (ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(SERVICE_USER)) {
            ValueMap vm = getContextAwareCloudConfigRes(resolver, resourcePath, configName, ValueMap.class);
            return new HashMap<>(vm);
        } catch (LoginException e) {
            LOG.error("Login Exception occurred when reading config ", e);
        }
        return null;
    }

    private <T> T getContextAwareCloudConfigRes(ResourceResolver resolver, @NotNull String resourcePath,
            String configName, Class<T> type) {
        Resource resource = resolver.getResource(resourcePath);
        Resource confRes = null != resource ? configurationResolver.getResource(resource, BUCKET_NAME, configName)
                : null;
        if (null != confRes) {
            Resource jcrContentRes = confRes.getChild(JCR_CONTENT);
            return null != jcrContentRes ? jcrContentRes.adaptTo(type) : confRes.adaptTo(type);
        }
        return null;
    }
}
