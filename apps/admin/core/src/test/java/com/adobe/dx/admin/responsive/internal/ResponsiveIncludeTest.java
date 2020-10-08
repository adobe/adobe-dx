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
package com.adobe.dx.admin.responsive.internal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.adobe.dx.testing.AbstractOakTest;
import com.adobe.dx.testing.extensions.ResponsiveContext;

import java.io.IOException;
import java.util.Collections;

import javax.jcr.RepositoryException;
import javax.script.Bindings;
import javax.script.SimpleBindings;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.MockSlingScriptHelper;
import org.apache.sling.testing.mock.sling.servlet.MockRequestDispatcherFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ResponsiveContext.class)
class ResponsiveIncludeTest extends AbstractOakTest {

    final static String DIALOG_ROOT = "/apps/dx/component/cq:dialog";
    final static String INCLUDE_ROOT = DIALOG_ROOT + "/content/items/tabs";
    final static String CACHE_ROOT_TEST = "/var/dx/test/responsiveinclude";
    final static String CACHE_ROOT = CACHE_ROOT_TEST + INCLUDE_ROOT;

    ResponsiveInclude include;

    @BeforeEach
    public void setup() {
        String path = "/mnt/override" + INCLUDE_ROOT;
        context.build().resource(path);
        context.currentResource(path);
        include = new ResponsiveInclude();
        context.registerInjectActivateService(include, "appChange", true,
            "dialogRoots", new String[] {"/apps/dx"},
            "cacheRoot", CACHE_ROOT_TEST);
        context.build().resource(INCLUDE_ROOT, "sling:resourceType", ResponsiveInclude.RESOURCE_TYPE, "resourceType","dialog/panel")
            .siblingsMode()
            .resource("items/general", "sling:resourceType", "text", "name","./generalText")
            .resource("items/resp", "dxResponsiveItem", true);
        context.build().resource(INCLUDE_ROOT + "/items/resp/items").siblingsMode()
            .resource("text", "sling:resourceType", "text", "name", "./respText")
            .resource("check", "sling:resourceType", "check", "name", "./check")
            .resource("checkType", "sling:resourceType", "checkType", "name", "./check@TypeHint")
            .commit();
        context.currentResource(context.resourceResolver().getResource(INCLUDE_ROOT));
        context.requestPathInfo().setSuffix(CONTENT_ROOT);
    }

    @Test
    public void testPath() {
        assertEquals("/var/dx/test/responsiveinclude" + INCLUDE_ROOT,
            include.getIncludePath(context.currentResource()));
    }

    @Test
    public void testIncludeResourceBuilding() throws LoginException, RepositoryException {
        Resource resource = include.getIncludeResource(include.getBreakpoints(context.request()), context.currentResource());
        assertNotNull(resource);
        assertEquals(CACHE_ROOT, resource.getPath());
        assertEquals("dialog/panel", resource.getValueMap().get("sling:resourceType"));
        ValueMap generalItem = getVM(CACHE_ROOT + "/items/general");
        assertNotNull(generalItem);
        assertEquals("./generalText", generalItem.get("name"));
        String tabletItem = CACHE_ROOT + "/items/respTablet";
        ValueMap vmTablet = getVM(tabletItem);
        assertNotNull(vmTablet);
        assertEquals("Tablet", vmTablet.get("jcr:title"));
        ValueMap check = getVM(tabletItem + "/items/check");
        assertNotNull(check);
        assertEquals("./checkTablet", check.get("name"));
        ValueMap checkType = getVM(tabletItem + "/items/checkType");
        assertNotNull(checkType);
        assertEquals("./checkTablet@TypeHint", checkType.get("name"));
    }

    @Test
    public void handleDialogPath() {
        assertNull(include.handleDialogPath(context.resourceResolver(),"/apps/just/does/not/exist"));
        String notIncludedPath = DIALOG_ROOT + "/some/other";
        context.build().resource(notIncludedPath);
        assertNull(include.handleDialogPath(context.resourceResolver(), notIncludedPath));
        String realPath = INCLUDE_ROOT + "/some/path";
        context.build().resource(realPath);
        assertEquals(INCLUDE_ROOT, include.handleDialogPath(context.resourceResolver(), realPath));
    }

    @Test
    public void validationChangeDialog() throws RepositoryException {
        include.getIncludeResource(include.getBreakpoints(context.request()), context.currentResource());
        assertTrue(include.isValidInclude(context.resourceResolver().getResource(CACHE_ROOT)));
        ResourceChange change = mock(ResourceChange.class);
        when(change.getPath()).thenReturn(INCLUDE_ROOT + "/some/random/child");
        include.onChange(Collections.singletonList(change));
        assertFalse(include.isValidInclude(context.resourceResolver().getResource(CACHE_ROOT)));
    }

    @Test
    @Disabled
    // we need a suitable dependency on granite ui commons before we activate this again
    public void testInclude() throws ServletException, IOException {

        Bindings bindings = new SimpleBindings();
        bindings.put("sling", new MockSlingScriptHelper(context.request(), context.response(), context.bundleContext()));
        bindings.put("request", context.request());
        bindings.put("response", context.response());
        MockRequestDispatcherFactory mockRequestDispatcherFactory = new MockRequestDispatcherFactory() {
            @Override
            public RequestDispatcher getRequestDispatcher(String s, RequestDispatcherOptions requestDispatcherOptions) {
                return mock(RequestDispatcher.class);
            }

            @Override
            public RequestDispatcher getRequestDispatcher(Resource resource,
                                                          RequestDispatcherOptions requestDispatcherOptions) {
                return mock(RequestDispatcher.class);
            }
        };
        context.request().setRequestDispatcherFactory(mockRequestDispatcherFactory);
        context.request().setAttribute(SlingBindings.class.getName(), bindings);

        include.doGet(context.request(), context.response());
        assertEquals(200, context.response().getStatus());
    }
}