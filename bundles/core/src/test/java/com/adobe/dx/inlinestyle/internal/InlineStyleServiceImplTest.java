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
package com.adobe.dx.inlinestyle.internal;

import static org.junit.jupiter.api.Assertions.*;

import com.adobe.dx.bindings.internal.DxBindingsValueProvider;
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.responsive.internal.ResponsivePropertiesImpl;
import com.adobe.dx.responsive.internal.ResponsivePropertiesImplTest;
import com.adobe.dx.inlinestyle.InlineStyleWorker;
import com.adobe.dx.testing.AbstractTest;
import com.adobe.dx.utils.RequestUtil;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingBindings;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InlineStyleServiceImplTest extends AbstractTest {

    private static String getFromRequest(String cssKey, String bpKey, String prop, SlingHttpServletRequest request) {
        String value = null;
        Map resprops = RequestUtil.getResponsiveProperties(request);
        if (resprops != null) {
            if (resprops.get(prop) != null) {
                Map map = (Map)resprops.get(prop);
                if (map != null) {
                    value = map.get(bpKey) != null ? map.get(bpKey).toString() : null;
                }
            }
        }
        if (StringUtils.isNotBlank(value)) {
            return cssKey + ": " + value;
        }
        return null;
    }

    InlineStyleWorker worker1 = new InlineStyleWorker() {
        @Override
        public String getKey() {
            return "worker1";
        }

        @Override
        public @Nullable String getDeclaration(Breakpoint breakpoint, SlingHttpServletRequest request) {
            if (breakpoint == null) {
                return "color: " + RequestUtil.getPolicy(request).get("color", String.class);
            } else {
                return getFromRequest("min-height", breakpoint.key(), "minheight", request);
            }
        }

        @Override
        public @Nullable String getRule(@Nullable Breakpoint breakpoint, @Nullable String id,
                                        SlingHttpServletRequest request) {
            return id != null ? ("#" + id + " > p { color: green }") : null;
        }
    };

    InlineStyleWorker worker2 = new InlineStyleWorker() {
        @Override
        public String getKey() {
            return "worker2";
        }

        @Override
        public @Nullable String getDeclaration(Breakpoint breakpoint,SlingHttpServletRequest request) {
            if (breakpoint.mediaQuery() == null) {
                return "font-size: " +  RequestUtil.getPolicy(request).get("fontsize", String.class);
            } else {
                return getFromRequest("min-width", breakpoint.key(), "minwidth", request);
            }
        }

        @Override
        public @Nullable String getRule(@Nullable Breakpoint breakpoint, @Nullable String id,
                                        SlingHttpServletRequest request) {
            return null;
        }
    };
    InlineStyleServiceImpl service;

    @BeforeEach
    void setup() {
        List<Breakpoint> breakpoints = ResponsivePropertiesImplTest.initResponsiveConfiguration(context);
        String someComp = CONTENT_ROOT + "/comp";
        final String[] array = new String[] {"worker1", "worker2"};
        context.build().resource("/apps/foo/bar", "styleWorkers", array);
        context.build().resource(someComp, "sling:resourceType", "foo/bar",
            "color", "blue",
            "minheightTablet", "200px",
            "fontsize", "13px",
            "minwidthDesktop", "90%");
        context.currentResource(someComp);
        ((SlingBindings)context.request().getAttribute(SlingBindings.class.getName())).put(DxBindingsValueProvider.POLICY_KEY, context.currentResource().getValueMap());
        RequestUtil.getBindings(context.request()).put(DxBindingsValueProvider.BP_KEY,
            breakpoints);
        RequestUtil.getBindings(context.request()).put(DxBindingsValueProvider.RESP_PROPS_KEY,
            new ResponsivePropertiesImpl(breakpoints, context.currentResource().getValueMap()));
        service = new InlineStyleServiceImpl();
    }

    @Test
    void getInlineStyleNoWorker() {
        assertTrue(StringUtils.isBlank(service.getInlineStyle(null, context.request())));
    }

    @Test
    void getInlineStyle2WorkersNoID() {
        service.bindWorker(worker1);
        service.bindWorker(worker2);
        assertEquals("font-size: 13px\n"
            + "@media screen and (min-width: 600px) {\n"
            + "min-height: 200px\n"
            + "}\n"
            + "@media screen and (min-width: 1200px) {\n"
            + "min-width: 90%\n"
            + "}", service.getInlineStyle(null, context.request()));
    }

    @Test
    @DisplayName("inline style from 2 then 1 worker, with an id")
    void getInlineStyle2Then1WorkersAndID() {
        service.bindWorker(worker1);
        service.bindWorker(worker2);
        assertEquals("#this-is-my-block {font-size: 13px}\n"
            + "#this-is-my-block > p { color: green }\n"
            + "@media screen and (min-width: 600px) {\n"
            + "#this-is-my-block {min-height: 200px}\n"
            + "#this-is-my-block > p { color: green }\n"
            + "}\n"
            + "@media screen and (min-width: 1200px) {\n"
            + "#this-is-my-block {min-width: 90%}\n"
            + "#this-is-my-block > p { color: green }\n"
            + "}", service.getInlineStyle("this-is-my-block", context.request()));
        service.unbindWorker(worker2);
        assertEquals("@media screen and (min-width: 600px) {\n"
            + "min-height: 200px\n"
            + "}", service.getInlineStyle(null, context.request()));
    }

    @Test
    void getWorkerKeysFullPath() {
        final String[] array = new String[] {"workers1", "workers2"};
        context.build().resource("/apps/foo/bar", "styleWorkers", array);
        context.build().resource(CONTENT_ROOT, "sling:resourceType", "foo/bar");
        assertArrayEquals(array, new InlineStyleServiceImpl().getWorkerKeys(context.currentResource(CONTENT_ROOT)));
    }


    @Test
    void getWorkerKeysNothing() {
        context.build().resource("/apps/check/this", "blah", "blah");
        context.build().resource(CONTENT_ROOT, "sling:resourceType", "check/this");
        assertEquals(0, new InlineStyleServiceImpl().getWorkerKeys(context.currentResource(CONTENT_ROOT)).length);
    }
}