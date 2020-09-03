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
package com.adobe.dx.style.internal;

import static org.junit.jupiter.api.Assertions.*;

import com.adobe.dx.bindings.internal.DxBindingsValueProvider;
import com.adobe.dx.style.StyleWorker;
import com.adobe.dx.testing.AbstractTest;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

class StyleServiceImplTest extends AbstractTest {

    @Test
    void getLocalStyle() {
        final String[] array = new String[] {"worker1", "worker2"};
        context.build().resource("/apps/foo/bar", "styleWorkers", array);
        context.build().resource(CONTENT_ROOT, "sling:resourceType", "foo/bar", "color", "blue", "fontsize", "13px");
        context.currentResource(CONTENT_ROOT);
        ((SlingBindings)context.request().getAttribute(SlingBindings.class.getName())).put(DxBindingsValueProvider.POLICY_KEY, context.currentResource().getValueMap());
        StyleServiceImpl service = new StyleServiceImpl();
        assertTrue(StringUtils.isBlank(service.getLocalStyle(null, context.request())));
        service.bindWorker(new StyleWorker() {
            @Override
            public String getKey() {
                return "worker1";
            }

            @Override
            public @Nullable String getDeclaration(Resource resource, ValueMap dxPolicy) {
                return "color: " + dxPolicy.get("color", String.class);
            }
        });
        StyleWorker worker2 = new StyleWorker() {
            @Override
            public String getKey() {
                return "worker2";
            }

            @Override
            public @Nullable String getDeclaration(Resource resource, ValueMap dxPolicy) {
                return "font-size: " + dxPolicy.get("fontsize", String.class);
            }
        };
        service.bindWorker(worker2);
        assertEquals("color: blue;font-size: 13px", service.getLocalStyle(null, context.request()));
        assertEquals("#this-is-my-block {color: blue;font-size: 13px}", service.getLocalStyle("this-is-my-block", context.request()));
        service.unbindWorker(worker2);
        assertEquals("color: blue", service.getLocalStyle(null, context.request()));
    }

    @Test
    void getWorkerKeysFullPath() {
        final String[] array = new String[] {"workers1", "workers2"};
        context.build().resource("/apps/foo/bar", "styleWorkers", array);
        context.build().resource(CONTENT_ROOT, "sling:resourceType", "foo/bar");
        assertArrayEquals(array, new StyleServiceImpl().getWorkerKeys(context.currentResource(CONTENT_ROOT)));
    }


    @Test
    void getWorkerKeysNothing() {
        context.build().resource("/apps/foo/bar", "blah", "blah");
        context.build().resource(CONTENT_ROOT, "sling:resourceType", "foo/bar");
        assertNull(new StyleServiceImpl().getWorkerKeys(context.currentResource(CONTENT_ROOT)));
    }
}