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

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;

import com.adobe.dx.content.marketo.service.CloudConfigReader;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = CloudConfigReader.class)
public class CloudConfigReaderImpl implements CloudConfigReader {

    private static final Logger LOG = LoggerFactory.getLogger(CloudConfigReaderImpl.class);

    private static final String BUCKET_NAME = "settings/cloudconfigs";

    private static final Map<String, Object> SERVICE_USER = Collections.singletonMap(SUBSERVICE,
        "readService");
    private static final String SLASH = "/";

    @Reference
    private ConfigurationResourceResolver configurationResolver = null;

    @Reference
    private ResourceResolverFactory resourceResolverFactory = null;

    @Override
    public <T> T getContextAwareCloudConfigRes(String resourcePath, String configName, Class<T> type) {
        try (ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(SERVICE_USER)) {
            Resource resource = getResourceFromResourceTree(resolver, resourcePath);
            Resource confRes = null != resource ? configurationResolver.getResource(resource, BUCKET_NAME,
                configName) : null;
            if (null != confRes) {
                Resource jcrContentRes = confRes.getChild(JCR_CONTENT);
                return null != jcrContentRes ? jcrContentRes.adaptTo(type) : confRes.adaptTo(type);
            }
        } catch (LoginException e) {
            LOG.error("Login Exception occurred when reading config ", e);
            return null;
        }
        return null;
    }

    private Resource getResourceFromResourceTree(ResourceResolver resolver, String resourcePath) {
        Resource resource = resolver.getResource(resourcePath);
        while(resource == null && StringUtils.isNotEmpty(resourcePath)) {
            resourcePath = StringUtils.substringBeforeLast(resourcePath, SLASH);
            resource = resolver.getResource(resourcePath);
        }
        return resource;
    }
}
