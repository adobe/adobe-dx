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

import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.responsive.ResponsiveConfiguration;
import com.adobe.dx.testing.AbstractTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.testing.mock.caconfig.MockContextAwareConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wcm.testing.mock.aem.junit5.AemContext;

public class ResponsivePropertiesTest extends AbstractTest {

    ResponsiveConfiguration configuration;

    public static ResponsiveConfiguration initResponsiveConfiguration(AemContext context) {
        context.build().resource(CONF_ROOT + "/sling:configs/" + ResponsiveConfiguration.class.getName() + "/breakpoints")
            .siblingsMode()
            .resource("1","propertySuffix", "Mobile", "key", "mobile")
            .resource("2", "propertySuffix", "Tablet", "key", "tablet")
            .resource("3", "propertySuffix", "Desktop", "key", "desktop");
        MockContextAwareConfig.registerAnnotationClasses(context, ResponsiveConfiguration.class);
        MockContextAwareConfig.registerAnnotationClasses(context, Breakpoint.class);
        context.create().resource(CONTENT_ROOT, "sling:configRef", CONF_ROOT);
        ResponsiveConfiguration configuration =  context.resourceResolver()
            .getResource(CONTENT_ROOT)
            .adaptTo(ConfigurationBuilder.class)
            .as(ResponsiveConfiguration.class);
        assertEquals( 3, configuration.breakpoints().length, "we should have 3 breakpoints configured");
        return configuration;
    }

    @BeforeEach
    void setup() {
        configuration = initResponsiveConfiguration(context);
    }

    LinkedHashMap<String,String> getProperty(final ResponsiveConfiguration rConfig, String key, Object...entries) {
        String path = "/content/" + StringUtils.join(Arrays.asList(entries), "/");
        context.build().resource(path, entries).commit();
        Resource resource = context.resourceResolver().getResource(path);
        ResponsiveProperties responsiveProperties = new ResponsiveProperties(rConfig, resource.getValueMap());
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
            getProperty(configuration, "width", "widthTablet", 34,"widthMobile", 12, "widthDesktop", 43),
            "mobile", "12", "tablet", "34", "desktop", "43");
        assertLinkedHashMapEqual("no mobile",
            getProperty(configuration, "width", "widthTablet", 34, "widthDesktop", 43),
            "mobile", null, "tablet", "34", "desktop", "43");
        assertLinkedHashMapEqual("only desktop",
            getProperty(configuration, "width", "widthDesktop", 43),
            "mobile", null, "tablet", null, "desktop", "43");
        assertLinkedHashMapEqual("should work with boolean too...",
            getProperty(configuration, "inherit", "inheritMobile", true, "inheritDesktop", false),
            "mobile", "true", "tablet", null, "desktop", "false");
        assertNull(getProperty(configuration, "height", "blah", 34,"foo", 12),
            "no value should be null");
        assertNull(getProperty(configuration, "height", "blah", 34,"foo", 12,"heightDesktop", ""),
            "blank value should be null");
        assertNull(new ResponsiveProperties(configuration, ValueMap.EMPTY).get(null));
    }

    @Test
    void unsupported() {
        ResponsiveProperties props = new ResponsiveProperties(configuration, ValueMap.EMPTY);
        final Collection<Callable> unsupportedOperations = Arrays.asList(
            () -> {props.clear(); return 0;},
            () -> {props.putAll(ValueMap.EMPTY); return 0;},
            () -> props.put("foo", "bar"),
            () -> props.containsKey("foo"),
            () -> props.containsValue("bar"),
            () -> props.size(),
            () -> props.values(),
            () -> props.keySet(),
            () -> props.entrySet(),
            () -> props.isEmpty(),
            () -> props.remove("foo"));
        for (Callable callable : unsupportedOperations) {
            assertThrows(UnsupportedOperationException.class, () -> callable.call());
        }

    }
}