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

import com.adobe.dx.admin.rendercondition.AbstractRenderCondition;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition;
import com.day.cq.wcm.api.policies.ContentPolicy;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

/**
 * check for a configured property presence in the current policy, that will lock (vote false),
 * or for a configured group
 */
@Component(
    service = Servlet.class,
    property = {
        SLING_SERVLET_RESOURCE_TYPES + "=dx/admin/rendercondition/lockproperty",
        SLING_SERVLET_METHODS + "=" + METHOD_GET})
public class LockPropertyRenderCondition extends AbstractRenderCondition {

    private static final String PN_PROPERTY = "property";

    @Override
    protected RenderCondition computeRenderCondition(@NotNull SlingHttpServletRequest request) {
        boolean vote = true;
        ContentPolicy policy = getContentPolicy(request);
        if (policy != null) {
            String lockProperty = getConfig(request.getResource()).get(PN_PROPERTY);
            ValueMap designProperties = policy.getProperties();
            //presence of lock property is considered to be
            vote = StringUtils.isBlank(designProperties.get(lockProperty, String.class));
        }
        return new SimpleRenderCondition(vote);
    }
}
