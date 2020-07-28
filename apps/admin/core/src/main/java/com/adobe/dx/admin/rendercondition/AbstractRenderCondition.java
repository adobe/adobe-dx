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

package com.adobe.dx.admin.rendercondition;

import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRenderCondition extends SlingSafeMethodsServlet {
    Logger log = LoggerFactory.getLogger(AbstractRenderCondition.class);

    protected abstract RenderCondition computeRenderCondition(@NotNull SlingHttpServletRequest request);
    private static final String PN_GROUP = "passthroughGroup";
    private static final String PASSTHROUGH_USER = "admin";
    private static final Collection<String> DEFAULT_PASSTHROUGH_GROUPS = Arrays.asList("template-authors", "administrators");

    protected @NotNull Config getConfig(@NotNull Resource dialogResource) {
        return new Config(dialogResource);
    }

    protected @Nullable ContentPolicy getContentPolicy(@NotNull SlingHttpServletRequest request) {
        ResourceResolver resolver = request.getResourceResolver();
        String resourcePath = request.getRequestPathInfo().getSuffix();
        Resource componentResource = resolver.getResource(resourcePath);
        if (componentResource != null) {
            ContentPolicyManager policyManager = resolver.adaptTo(ContentPolicyManager.class);
            if (policyManager != null) {
                return policyManager.getPolicy(componentResource, request);
            }
        }
        return null;
    }

    protected Collection<Group> getConfiguredGroups(@NotNull SlingHttpServletRequest request,
                                                    @NotNull UserManager userManager) throws RepositoryException {
        Collection<Group> groups = new ArrayList<>();
        Collection<String> groupNames = DEFAULT_PASSTHROUGH_GROUPS;
        ContentPolicy policy = getContentPolicy(request);
        if (policy != null) {
            String[] configuredGroups = policy.getProperties().get(PN_GROUP, String[].class);
            if (configuredGroups != null) {
                groupNames = Arrays.asList(configuredGroups);
            }
            for (String groupName : groupNames) {
                Group group = (Group) userManager.getAuthorizable(groupName);
                if (group != null) {
                    groups.add(group);
                }
            }
        }
        return groups;
    }

    protected boolean shouldPassthrough(@NotNull String uid, @NotNull SlingHttpServletRequest request) {
        ResourceResolver resolver = request.getResourceResolver();
        if (PASSTHROUGH_USER.equals(uid)) {
            return true;
        }
        Session session = resolver.adaptTo(Session.class);
        try {
            UserManager userManager = ((JackrabbitSession)session).getUserManager();
            Authorizable user = userManager.getAuthorizable(uid);
            for (Group group : getConfiguredGroups(request, userManager)) {
                if (group.isMember(user)) {
                    return true;
                }
            }
            return false;
        } catch (RepositoryException e) {
            log.error("unable to check membership, will passthrough", e);
        }
        return true;
    }

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws ServletException, IOException {
        RenderCondition renderCondition = new SimpleRenderCondition(true);
        if (!shouldPassthrough(request.getResourceResolver().getUserID(), request)) {
            renderCondition = computeRenderCondition(request);
        }
        request.setAttribute(RenderCondition.class.getName(), renderCondition);
    }
}
