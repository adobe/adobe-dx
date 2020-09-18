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
package com.adobe.dx.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.adobe.dx.inlinestyle.internal.InlineStyleServiceImpl;
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.responsive.internal.ResponsivePropertiesImplTest;
import com.adobe.dx.testing.AbstractTest;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractWorkerManagerTest extends AbstractTest {

    final static String[] ARRAY = new String[] {"worker1", "worker2"};
    final static String PATH = CONTENT_ROOT + "/comp";
    Resource resource;

    @BeforeEach
    public void setup() {
        AbstractTest.initContentRoots(context);
        context.build().resource(CONFIG_ROOTS  + "/apps/foo/bar",  "styleWorkers", ARRAY);
        context.build().resource(PATH, "sling:resourceType", "foo/bar");
        resource = context.currentResource(PATH);
    }

    @Test
    void getWorkerKeys() {
        assertArrayEquals(ARRAY, new InlineStyleServiceImpl().getWorkerKeys(resource));
    }

    @Test
    void getWorkerKeysFullPath() {
        context.build().resource(CONTENT_ROOT, "sling:resourceType", "/apps/foo/bar");
        assertArrayEquals(ARRAY, new InlineStyleServiceImpl().getWorkerKeys(resource));
    }

    @Test
    void getWorkerKeysNothing() {
        context.build().resource(CONTENT_ROOT, "sling:resourceType", "check/this");
        resource = context.resourceResolver().getResource(CONTENT_ROOT);
        assertEquals(0, new InlineStyleServiceImpl().getWorkerKeys(resource).length);
    }
}