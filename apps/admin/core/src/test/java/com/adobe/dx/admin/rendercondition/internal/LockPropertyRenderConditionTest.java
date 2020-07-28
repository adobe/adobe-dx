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
package com.adobe.dx.admin.rendercondition.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adobe.dx.testing.AbstractTest;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;

import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LockPropertyRenderConditionTest extends AbstractTest {

    LockPropertyRenderCondition condition;
    final String propertyName = "foo";
    final String resourceType = "some/type";
    final String resourcePath = "/content/some/resource";

    @BeforeEach
    public void setup() {
        condition = new LockPropertyRenderCondition();
        context.create().resource(resourcePath, "sling:resourceType",resourceType,
            "property", propertyName);
        context.currentResource(resourcePath);
        ((MockRequestPathInfo)context.request().getRequestPathInfo()).setSuffix(resourcePath);
    }

    @Test
    void computeRenderConditionLocked() throws RepositoryException, ServletException, IOException {
        context.contentPolicyMapping(resourceType, propertyName, "true");
        RenderCondition result = condition.computeRenderCondition(context.request());
        assertNotNull(result);
        assertFalse(result.check());
    }

    @Test
    void computeRenderConditionUnlocked() throws RepositoryException, ServletException, IOException {
        RenderCondition result = condition.computeRenderCondition(context.request());
        assertNotNull(result);
        assertTrue(result.check());
    }
}