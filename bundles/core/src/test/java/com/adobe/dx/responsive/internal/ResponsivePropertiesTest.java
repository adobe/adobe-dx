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

import static org.junit.jupiter.api.Assertions.*;

import com.adobe.dx.testing.AbstractTest;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Test;

class ResponsivePropertiesTest extends AbstractTest {

    String[] BREAKPOINTS = new String[] {"Mobile", "Tablet", "Desktop"};

    LinkedHashMap<String,String> getProperty(final String[] breakpoints, String key, Object...entries) {
        String path = "/content/" + StringUtils.join(Arrays.asList(entries), "/");
        context.build().resource(path, entries).commit();
        Resource resource = context.resourceResolver().getResource(path);
        ResponsiveProperties responsiveProperties = new ResponsiveProperties(breakpoints, resource.getValueMap());
        return (LinkedHashMap<String,String>)responsiveProperties.get(key);
    }

    void assertLinkedHashMapEqual(String message, LinkedHashMap<String,String> result, String... properties) {
        LinkedHashMap<String,String> expected = new LinkedHashMap<>();
        for (int i = 0; i < properties.length; i += 2) {
            expected.put(properties[i], properties[i+1]);
        }
        assertEquals(expected, result, message);
    }

    @Test
    void get() {
        assertLinkedHashMapEqual("three widths are provided",
            getProperty(BREAKPOINTS, "width", "widthTablet", 34,"widthMobile", 12, "widthDesktop", 43),
            "mobile", "12", "tablet", "34", "desktop", "43");
        assertLinkedHashMapEqual("no mobile",
            getProperty(BREAKPOINTS, "width", "widthTablet", 34, "widthDesktop", 43),
            "mobile", null, "tablet", "34", "desktop", "43");
        assertLinkedHashMapEqual("only desktop",
            getProperty(BREAKPOINTS, "width", "widthDesktop", 43),
            "mobile", null, "tablet", null, "desktop", "43");
        assertLinkedHashMapEqual("should work with boolean too...",
            getProperty(BREAKPOINTS, "inherit", "inheritMobile", true, "inheritDesktop", false),
            "mobile", "true", "tablet", null, "desktop", "false");
        assertNull(getProperty(BREAKPOINTS, "height", "blah", 34,"foo", 12),
            "no value should be null");
        assertNull(getProperty(BREAKPOINTS, "height", "blah", 34,"foo", 12,"heightDesktop", ""),
            "blank value should be null");
        assertNull(new ResponsiveProperties(BREAKPOINTS, ValueMap.EMPTY).get(null));
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