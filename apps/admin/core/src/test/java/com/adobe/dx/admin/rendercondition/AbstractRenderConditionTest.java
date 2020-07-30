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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adobe.dx.testing.AbstractOakTest;
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
import org.apache.sling.testing.mock.caconfig.MockContextAwareConfig;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.wcm.testing.mock.aem.junit5.AemContext;

class AbstractRenderConditionTest extends AbstractOakTest {

    AbstractRenderCondition condition;
    
    @BeforeEach
    void setup(AemContext context) throws RepositoryException {
        condition = new AbstractRenderCondition() {
            @Override
            protected RenderCondition computeRenderCondition(@NotNull SlingHttpServletRequest request) {
                return null;
            }
        };
        UserManager userManager = ((JackrabbitSession)context.resourceResolver().adaptTo(Session.class)).getUserManager();
        User user = userManager.createUser("test", "test");
        userManager.createUser("another", "another");
        Group group = userManager.createGroup("containsTest");
        Group superGroup = userManager.createGroup("containsAll");
        Group blank = userManager.createGroup("containsNothing");
        group.addMember(user);
        superGroup.addMember(group);
        String resourceType = "some/type";
        context.create().resource(CONTENT_ROOT, "sling:configRef", CONF_ROOT,"sling:resourceType",resourceType);
        MockContextAwareConfig.registerAnnotationClasses(context, RenderConditionConfiguration.class);
        MockContextAwareConfig.writeConfiguration(context, CONTENT_ROOT, RenderConditionConfiguration.class,
            "passthroughGroups", new String[] {"doesNotExist","containsAll","containsNothing"});
        context.currentResource(CONTENT_ROOT);
        ((MockRequestPathInfo)context.request().getRequestPathInfo()).setSuffix(CONTENT_ROOT);
    }

    @Test
    void getConfig() {
        context.create()
            .resource("/content/some/component", "test", "foobar");
        Config config = condition.getConfig(context.currentResource("/content/some/component"));
        assertNotNull(config);
        assertEquals("foobar", config.get("test"));
    }

    @Test
    @DisplayName("admin should passthrough")
    void shouldPassthroughAdminUser() {
        assertTrue(condition.shouldPassthrough("admin", context.request()));
    }

    @Test
    @DisplayName("'another' user does not belong to any configured group, so the condition should not passthrough")
    void shouldNotPassthroughAnotherUser() throws RepositoryException {
        assertFalse(condition.shouldPassthrough("another", context.request()));
    }

    @Test
    @DisplayName("test user belongs to passthrough groups, so the condition should passthrough")
    void shouldPassthroughTestUser() throws RepositoryException {
        assertTrue(condition.shouldPassthrough("test", context.request()));
    }

    @Test
    void doGet() throws ServletException, IOException {
        SlingHttpServletResponse response = new MockSlingHttpServletResponse();
        condition.doGet(context.request(), response);
        assertNotNull(context.request().getAttribute(RenderCondition.class.getName()));
    }
}