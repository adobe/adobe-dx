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
package com.adobe.dx.responsive.internal;

import static com.adobe.dx.bindings.internal.DxBindingsValueProvider.BREAKPOINTS;
import static org.junit.jupiter.api.Assertions.*;

import com.adobe.dx.testing.AbstractTest;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Test;

class ResponsivePropertiesTest extends AbstractTest {

    Object getProperty(final String[] breakpoints, String key, Object...entries) {
        String path = "/content/" + StringUtils.join(Arrays.asList(entries), "/");
        context.build().resource(path, entries).commit();
        Resource resource = context.resourceResolver().getResource(path);
        ResponsiveProperties responsiveProperties = new ResponsiveProperties(breakpoints, resource.getValueMap());
        return responsiveProperties.get(key);
    }

    @Test
    void get() {
        assertEquals(12,
            getProperty(BREAKPOINTS, "width", "widthTablet", 34,"widthMobile", 12, "widthDesktop", 43),
            "width should be mobile's");
        assertEquals(34,
            getProperty(BREAKPOINTS, "width", "widthTablet", 34, "widthDesktop", 43),
            "width should be tablet's");
        assertEquals(43,
            getProperty(BREAKPOINTS, "width", "widthDesktop", 43),
            "width should be desktop's");
        assertEquals(true,
            getProperty(BREAKPOINTS, "inherit", "inheritMobile", true, "inheritDesktop", false),
            "should work with boolean too...");
        assertEquals("bar",
            getProperty(BREAKPOINTS, "foo", "fooTablet", "bar", "fooDesktop", "blah"),
            "...and strings");
        assertNull(getProperty(BREAKPOINTS, "height", "blah", 34,"foo", 12),
            "no value should be null");
    }

    @Test
    void unsupported() {
        ResponsiveProperties props = new ResponsiveProperties(BREAKPOINTS, ValueMap.EMPTY);
        assertThrows(UnsupportedOperationException.class, () -> props.clear());
        assertThrows(UnsupportedOperationException.class, () -> props.putAll(ValueMap.EMPTY));
        assertThrows(UnsupportedOperationException.class, () -> props.put("foo", "bar"));
        assertThrows(UnsupportedOperationException.class, () -> props.containsKey("foo"));
        assertThrows(UnsupportedOperationException.class, () -> props.containsValue("bar"));
        assertThrows(UnsupportedOperationException.class, () -> props.size());
        assertThrows(UnsupportedOperationException.class, () -> props.values());
        assertThrows(UnsupportedOperationException.class, () -> props.keySet());
        assertThrows(UnsupportedOperationException.class, () -> props.entrySet());
        assertThrows(UnsupportedOperationException.class, () -> props.isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> props.remove("foo"));
    }
}