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
package com.adobe.dx.admin.config.fonts.internal;

import com.adobe.dx.admin.config.fonts.Settings;
import com.adobe.dx.admin.config.fonts.SettingsProvider;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SettingsProviderImpl implements SettingsProvider {
    private static final Logger LOG = LoggerFactory.getLogger(SettingsProviderImpl.class);

    private static final String CONF_CONTAINER_BUCKET_NAME = "settings";

    private static final String CLOUDCONFIG_PARENT = "cloudconfigs/";

    private static final String SERVICE_USER = "repository-reader-service";

    @Reference
    private ConfigurationResourceResolver configResourceResolver;

    @Override
    public Settings getSettings(SlingHttpServletRequest request, String configName) {
        String configPath = CLOUDCONFIG_PARENT + configName;
        LOG.trace("Obtaining ResourceResolver with service user [{}]", SERVICE_USER);
        PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
        Page currentPage = pageManager.getContainingPage(request.getResource());
        Resource environmentResource = getEnvironmentResource(pageManager, currentPage, configPath);
        if (environmentResource != null) {
            return environmentResource.adaptTo(Settings.class);
        }
        return null;
    }

    private Resource getEnvironmentResource(PageManager pageManager, Page currentPage, String configPath) {
        Page configPage = null;
        LOG.trace("Resolving context-aware configuration for resource [{}]", currentPage.getContentResource().getPath());
        Resource configResource = configResourceResolver.getResource(
                currentPage.getContentResource(),
                CONF_CONTAINER_BUCKET_NAME,
                configPath);
        if (configResource != null) {
            configPage = pageManager.getContainingPage(configResource);
        } else {
            LOG.debug("No configuration found.");
        }
        return configPage != null && configPage.hasContent() ? configPage.getContentResource() : null;
    }
}
