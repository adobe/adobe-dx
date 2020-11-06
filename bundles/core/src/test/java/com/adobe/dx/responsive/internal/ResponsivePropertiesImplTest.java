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

import com.adobe.dx.bindings.internal.DxBindingsValueProvider;
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.responsive.InheritedMap;
import com.adobe.dx.responsive.ResponsiveConfiguration;
import com.adobe.dx.testing.AbstractTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.testing.mock.caconfig.MockContextAwareConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wcm.testing.mock.aem.junit5.AemContext;

public class ResponsivePropertiesImplTest extends AbstractTest {

    List<Breakpoint> breakpoints;

    public static List<Breakpoint> initResponsiveConfiguration(AemContext context) {
        AbstractTest.initContentRoots(context);
        context.build().resource(CONF_ROOT + "/sling:configs/" + ResponsiveConfiguration.class.getName() + "/breakpoints")
            .siblingsMode()
            .resource("1","propertySuffix", "", "key", "mobile")
            .resource("2", "propertySuffix", "Tablet", "key", "tablet", "mediaQuery", "@media screen and (min-width: 600px)",
                "inherit", "inheritTablet")
            .resource("3", "propertySuffix", "Desktop", "key", "desktop", "mediaQuery", "@media screen and (min-width: 1200px)",
                "inherit", "inheritDesktop");
        MockContextAwareConfig.registerAnnotationClasses(context, ResponsiveConfiguration.class);
        MockContextAwareConfig.registerAnnotationClasses(context, Breakpoint.class);
        ResponsiveConfiguration configuration =  context.resourceResolver()
            .getResource(CONTENT_ROOT)
            .adaptTo(ConfigurationBuilder.class)
            .as(ResponsiveConfiguration.class);
        assertEquals( 3, configuration.breakpoints().length, "we should have 3 breakpoints configured");
        return Arrays.asList(configuration.breakpoints());
    }

    public static void setBreakpoints(AemContext context) {
        SlingBindings bindings = (SlingBindings)context.request().getAttribute(SlingBindings.class.getName());
        List<Breakpoint> breakpointList = ResponsivePropertiesImplTest.initResponsiveConfiguration(context);
        ResponsivePropertiesImpl responsiveProperties = new ResponsivePropertiesImpl(breakpointList, AbstractTest.getVM(context, CONTENT_ROOT));
        bindings.put(DxBindingsValueProvider.RESP_PROPS_KEY, responsiveProperties);
        bindings.put(DxBindingsValueProvider.BP_KEY, breakpointList);
    }

    @BeforeEach
    void setup() {
        breakpoints = initResponsiveConfiguration(context);
    }

    LinkedHashMap<String, Object> getProperty(String key, Object...entries) {
        String path = "/content/" + StringUtils.join(Arrays.asList(entries), "/");
        context.build().resource(path, entries).commit();
        Resource resource = context.resourceResolver().getResource(path);
        ResponsivePropertiesImpl responsiveProperties = new ResponsivePropertiesImpl(breakpoints, resource.getValueMap());
        return responsiveProperties.get(key);
    }

    InheritedMap getInheritedMap(Object...entries) {
        String path = "/content/" + StringUtils.join(Arrays.asList(entries), "/");
        context.build().resource(path, entries).commit();
        Resource resource = context.resourceResolver().getResource(path);
        return new ResponsivePropertiesImpl(breakpoints, resource.getValueMap());
    }

    void assertLinkedHashMapEqual(String message, LinkedHashMap<String, Object> result, Object... properties) {
        LinkedHashMap<String, Object> expected = new LinkedHashMap<>();
        for (int i = 0; i < properties.length; i += 2) {
            expected.put((String)properties[i], properties[i+1]);
        }
        assertEquals(expected, result, message);
    }

    @Test
    void get() {
        assertLinkedHashMapEqual("three widths are provided",
            getProperty( "width", "widthTablet", 34,"width", 12, "widthDesktop", 43),
            "mobile", 12, "tablet", 34, "desktop", 43);
        assertLinkedHashMapEqual("no mobile",
            getProperty( "width", "widthTablet", 34, "widthDesktop", 43),
            "mobile", null, "tablet", 34, "desktop", 43);
        assertLinkedHashMapEqual("only desktop",
            getProperty( "width", "widthDesktop", 43),
            "mobile", null, "tablet", null, "desktop", 43);
        assertLinkedHashMapEqual("should work with boolean too...",
            getProperty( "flag", "flag", true, "flagDesktop", false),
            "mobile", true, "tablet", null, "desktop", false);
        assertNull(new ResponsivePropertiesImpl(breakpoints, ValueMap.EMPTY).get(null));
    }

    @Test
    void getNoValue() {
        assertNull(getProperty( "height", "blah", 34,"foo", 12),
            "no value should be null");
    }

    @Test
    void getBlankValue() {
        assertNull(getProperty( "height", "blah", 34, "foo", 12, "heightDesktop", ""),
            "blank value should be null");
    }

    @Test
    void getRawInheritedMap () {
        InheritedMap map = getInheritedMap( "test", "exist");
        assertEquals("exist", map.getInheritedValue("test", breakpoints.get(0), ""));
        map = getInheritedMap( "does", "notexist");
        assertEquals("", map.getInheritedValue("test", breakpoints.get(0), ""));
    }

    @Test
    void getInheritedValue () {
        Breakpoint tablet = breakpoints.get(1);
        Breakpoint desktop = breakpoints.get(2);
        //direct accesses or default behaviour should be inheritance
        assertEquals("exist", getInheritedMap( "test", "exist")
            .getInheritedValue("test", tablet, ""));
        assertEquals("exist", getInheritedMap( "test", "exist")
            .getInheritedValue("test", tablet, ""));
        assertEquals("exist", getInheritedMap( "test", "exist")
            .getInheritedValue("test", desktop, ""));
        //explicit inheritance should work too
        assertEquals("exist", getInheritedMap( "test", "exist", "inheritTablet", "inherit")
            .getInheritedValue("test", tablet, ""));
        assertEquals("exist", getInheritedMap( "test", "exist", "inheritTablet",  "inherit", "inheritDesktop",  "inherit")
            .getInheritedValue("test", desktop, ""));
        //override (or anything else) should break inheritance
        assertEquals("", getInheritedMap( "test", "exist", "inheritTablet", "override", "inheritDesktop", "inherit")
            .getInheritedValue("test", desktop, ""));
        //inheritance on a given property should work
        assertEquals("exist", getInheritedMap( "test", "exist", "inheritTablet", "override", "inheritTabletTest", "inherit")
            .getInheritedValue("test", desktop, ""));
    }

    @Test
    void unsupported() {
        ResponsivePropertiesImpl props = new ResponsivePropertiesImpl(new ArrayList<>(), ValueMap.EMPTY);
        final Collection<Callable> unsupportedOperations = Arrays.asList(
            () -> {props.clear(); return 0;},
            () -> {props.putAll(new LinkedHashMap<>()); return 0;},
            () -> props.put("foo", new LinkedHashMap<>()),
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