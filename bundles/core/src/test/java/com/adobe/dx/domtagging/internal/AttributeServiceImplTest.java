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
package com.adobe.dx.domtagging.internal;

import static org.junit.jupiter.api.Assertions.*;

import com.adobe.dx.bindings.internal.DxBindingsValueProvider;
import com.adobe.dx.domtagging.AttributeWorker;
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.responsive.internal.ResponsivePropertiesImpl;
import com.adobe.dx.responsive.internal.ResponsivePropertiesImplTest;
import com.adobe.dx.testing.AbstractTest;
import com.adobe.dx.utils.RequestUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingBindings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttributeServiceImplTest extends AbstractTest {
    AttributeWorker worker1 = new AttributeWorker() {
        @Override
        public Map<String, String> getAttributes(SlingHttpServletRequest request) {
            Map<String, String> map = new HashMap<>();
            map.put("11", "11");
            map.put("12", "12");
            return map;
        }

        @Override
        public String getKey() {
            return "worker1";
        }
    };

    AttributeWorker worker2 = new AttributeWorker() {
        @Override
        public Map<String, String> getAttributes(SlingHttpServletRequest request) {
            Map<String, String> map = new HashMap<>();
            map.put("21", "21");
            map.put("22", "22");
            return map;
        }

        @Override
        public String getKey() {
            return "worker2";
        }
    };

    AttributeServiceImpl service;

    @BeforeEach
    void setup() {
        List<Breakpoint> breakpoints = ResponsivePropertiesImplTest.initResponsiveConfiguration(context);
        String someComp = CONTENT_ROOT + "/comp";
        final String[] array = new String[] {"worker1", "worker2"};
        context.build().resource(CONFIG_ROOTS  + "/apps/foo/bar",  "attributeWorkers", array);
        context.build().resource(someComp, "sling:resourceType", "foo/bar");
        context.currentResource(someComp);
        ((SlingBindings)context.request().getAttribute(SlingBindings.class.getName())).put(DxBindingsValueProvider.POLICY_KEY, context.currentResource().getValueMap());
        RequestUtil.getBindings(context.request()).put(DxBindingsValueProvider.BP_KEY,
            breakpoints);
        RequestUtil.getBindings(context.request()).put(DxBindingsValueProvider.RESP_PROPS_KEY,
            new ResponsivePropertiesImpl(breakpoints, context.currentResource().getValueMap()));
        service = new AttributeServiceImpl();
    }

    @Test
    public void testNoWorker() {
        assertNull(service.getAttributes(context.request()));
    }

    @Test
    public void testOneWorker() {
        service.bindWorker(worker1);
        assertIterableEquals(Arrays.asList("11", "12"), service.getAttributes(context.request()).keySet());
    }

    @Test
    public void testTwoWorker() {
        service.bindWorker(worker1);
        service.bindWorker(worker2);
        assertTrue(CollectionUtils.isEqualCollection(Arrays.asList("11", "12", "21", "22"), service.getAttributes(context.request()).keySet()));
    }
}