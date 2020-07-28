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
package com.adobe.dx.admin.rendercondition;

import static org.apache.sling.testing.mock.caconfig.ContextPlugins.CACONFIG;
import static org.apache.sling.testing.mock.sling.ResourceResolverType.JCR_OAK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adobe.dx.testing.AbstractTest;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wcm.testing.mock.aem.junit5.AemContextBuilder;

class AbstractRenderConditionTest extends AbstractTest {

    AbstractRenderCondition condition;

    @BeforeEach
    private void setup() {
        context = new AemContextBuilder(JCR_OAK).plugin(CACONFIG).build();
        condition = new AbstractRenderCondition() {
            @Override
            protected RenderCondition computeRenderCondition(@NotNull SlingHttpServletRequest request) {
                return null;
            }
        };
    }

    @Test
    void getConfig() {
        context.create()
            .resource("/content/some/component", "test", "foobar");
        Config config = condition.getConfig(context.currentResource("/content/some/component"));
        assertNotNull(config);
        assertEquals("foobar", config.get("test"));
    }

    SlingHttpServletRequest prepareAuthorizationContext() throws RepositoryException {
        UserManager userManager = ((JackrabbitSession)context.resourceResolver().adaptTo(Session.class)).getUserManager();
        User user = userManager.createUser("test", "test");
        userManager.createUser("another", "another");
        Group group = userManager.createGroup("containsTest");
        Group superGroup = userManager.createGroup("containsAll");
        Group blank = userManager.createGroup("containsNothing");
        group.addMember(user);
        superGroup.addMember(group);
        String resourcePath = "/content/some/resource";
        String resourceType = "some/type";
        context.create().resource(resourcePath, "sling:resourceType",resourceType);
        // create a content policy with mapping for resource type
        context.contentPolicyMapping(resourceType,
            "passthroughGroup", new String[] {"doesNotExist","containsAll","containsNothing"});
        ((MockRequestPathInfo)context.request().getRequestPathInfo()).setSuffix(resourcePath);

        return context.request();
    }

    @Test
    void shouldPassthroughAdminUser() {
        assertTrue(condition.shouldPassthrough("admin", context.request()));
    }

    @Test
    void shouldNotPassthroughAnotherUser() throws RepositoryException {
        assertFalse(condition.shouldPassthrough("another", prepareAuthorizationContext()));
    }

    @Test
    void shouldPassthroughTestUser() throws RepositoryException {
        assertTrue(condition.shouldPassthrough("test", prepareAuthorizationContext()));
    }

    @Test
    void doGet() throws RepositoryException, ServletException, IOException {
        SlingHttpServletRequest request = prepareAuthorizationContext();
        SlingHttpServletResponse response = new MockSlingHttpServletResponse();
        condition.doGet(request, response);
        assertNotNull(request.getAttribute(RenderCondition.class.getName()));
    }
}