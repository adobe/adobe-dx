/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.dx.admin.rendercondition.internal;

import static org.apache.sling.api.servlets.HttpConstants.METHOD_GET;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.adobe.dx.admin.rendercondition.AbstractRenderCondition;
import com.adobe.dx.utils.service.CloudConfigReader;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Checks whether a cloud config property is set with a text value or as a boolean(true).
 */
@Component(service = Servlet.class, property = {
        SLING_SERVLET_RESOURCE_TYPES + "=dx/admin/rendercondition/cloudconfigproperty",
        SLING_SERVLET_METHODS + "=" + METHOD_GET })
public class CloudConfigPropertyRenderCondition extends AbstractRenderCondition {

    @Reference
    private CloudConfigReader cloudConfigReader;

    private static final String PN_CLOUD_CONFIG_NAME = "cloudConfigName";
    private static final String PN_PROPERTY = "propertyName";

    protected boolean isPropertySet(Map<String, Object> cloudConfigRes, String propertyName) {
        boolean vote = false;
        Object propertyValue = cloudConfigRes.get(propertyName);
        if (propertyValue instanceof String) {
            vote = StringUtils.isNotBlank((String) propertyValue);
        } else if (propertyValue instanceof Boolean) {
            vote = (Boolean) propertyValue;
        }
        return vote;
    }

    @Override
    protected RenderCondition computeRenderCondition(@NotNull SlingHttpServletRequest request) {
        boolean vote = false;
        Config renderConditionConfig = new Config(request.getResource());
        String cloudConfigName = renderConditionConfig.get(PN_CLOUD_CONFIG_NAME);
        String propertyName = renderConditionConfig.get(PN_PROPERTY);
        if (StringUtils.isAnyBlank(cloudConfigName, propertyName)) {
            return new SimpleRenderCondition(false);
        }
        PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
        Page page = pageManager.getContainingPage(request.getRequestPathInfo().getSuffix());
        if (page != null) {
            Map<String, Object> resourceCloudConfig = cloudConfigReader.getContextAwareCloudConfigRes(page.getPath(),
                    cloudConfigName);
            vote = isPropertySet(resourceCloudConfig, propertyName);
        }
        return new SimpleRenderCondition(vote);
    }

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute(RenderCondition.class.getName(), computeRenderCondition(request));
    }
}
