/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.dx.admin.config.fonts.impl;

import java.util.Collections;
import java.util.Map;

import com.adobe.dx.admin.config.fonts.Settings;
import com.adobe.dx.admin.config.fonts.SettingsProvider;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SettingsProviderImpl implements SettingsProvider {

    private static String CONF_CONTAINER_BUCKET_NAME = "settings";

    public static String CLOUDCONFIG_PARENT = "cloudconfigs";

    static final String SERVICE_USER = "repository-reader-service";

    static final String CONFIG_HEADER_SUFFIX = "/cloudconfig-header/cloudconfig-header";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private ConfigurationResourceResolver configResourceResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

@Override
public Settings getSettings(SlingHttpServletRequest request, String configName) {
    String configPath = CLOUDCONFIG_PARENT  + "/" + configName;
    Map<String, Object> serviceMap = Collections.<String, Object>singletonMap(ResourceResolverFactory.SUBSERVICE, SERVICE_USER);
    try (ResourceResolver configResolver = resolverFactory.getServiceResourceResolver(serviceMap)) {
        log.trace("Obtaining ResourceResolver with service user [{}]", SERVICE_USER);
        Resource environmentResource = getEnvironmentResource(configResolver, request, configPath);
        if (environmentResource != null) {
            return environmentResource.adaptTo(Settings.class);
        }
    } catch (LoginException e) {
        log.error("Unable to obtain ResourceResolver with service user [{}]", SERVICE_USER);
    }
    return null;
}

    private Resource getEnvironmentResource(ResourceResolver resolver, SlingHttpServletRequest request, String configPath) throws LoginException {
        PageManager pageMgr = resolver.adaptTo(PageManager.class);
        Page page = pageMgr != null ? pageMgr.getContainingPage(request.getResource()) : null;
        if (page != null && page.hasContent()) {
            log.trace("Resolving context-aware configuration for resource [{}]", page.getContentResource().getPath());
            Resource configResource = configResourceResolver.getResource(
                    page.getContentResource(),
                    CONF_CONTAINER_BUCKET_NAME,
                    configPath);
            if (configResource != null) {
                Page configPage = pageMgr.getContainingPage(configResource);
                if (configPage != null) {
                    return configPage.hasContent() ? configPage.getContentResource() : null;
                }
            } else {
                log.debug("No configuration found.");
            }
        } else {
            log.debug("Resource [{}] is not adaptable to Page or has no content", request != null ? request.getResource() : null);
        }
        return null;
    }
}
