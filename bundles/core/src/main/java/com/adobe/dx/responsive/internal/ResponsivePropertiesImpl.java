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

import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.responsive.InheritedMap;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * simply check for get operation the required property with and ordered list of
 * suffix. Allowing several properties
 */
public class ResponsivePropertiesImpl implements Map<String, LinkedHashMap<String, Object>>, InheritedMap {

    private final List<Breakpoint> breakpoints;
    private ValueMap properties;
    private static final String PV_INHERIT = "inherit";

    public ResponsivePropertiesImpl(final List<Breakpoint> breakpoints, ValueMap properties) {
        this.breakpoints = breakpoints;
        this.properties = properties;
    }

    private String computeResponsiveResourceName(String name, Breakpoint breakpoint) {
        return name + breakpoint.propertySuffix();
    }

    @Override
    public LinkedHashMap<String, Object> get(Object key) {
        if (key != null) {
            boolean empty = true;
            LinkedHashMap<String, Object> breakpointValues = new LinkedHashMap<>();
            for (Breakpoint breakpoint : breakpoints) {
                String respKey = computeResponsiveResourceName(key.toString(), breakpoint);
                Object value = properties.get(respKey);
                empty &= value == null || StringUtils.isBlank(value.toString());
                breakpointValues.put(breakpoint.key(), value);
            }
            if (!empty) {
                return breakpointValues;
            }
        }
        return null;
    }

    private @Nullable Breakpoint getPreviousBreakpoint(Breakpoint breakpoint) {
        Breakpoint previous = null;
        for (Breakpoint candidate : breakpoints) {
            if (breakpoint.key().equals(candidate.key())) {
                break;
            }
            previous = candidate;
        }
        return previous;
    }

    /**
     * @param breakpoint current breakpoint
     * @param properties current values
     * @param inheritPropertyName property to look for specific inheritance
     * @return can we access "previous" breakpoint or not, based on general or specific inherit property,
     * that is <inheritProperty>PropertyName (with capital initial), e.g. inheritDesktopMargin
     */
    boolean shouldInherit(Breakpoint breakpoint, ValueMap properties, String inheritPropertyName) {
        if (StringUtils.isNotBlank(breakpoint.inherit())) {
            String specificInherit = breakpoint.inherit() + StringUtils.capitalize(inheritPropertyName);
            String defaultBehaviour = properties.get(breakpoint.inherit(), PV_INHERIT);
            String behaviour = properties.get(specificInherit, defaultBehaviour);
            return PV_INHERIT.equals(behaviour);
        }
        return false;
    }

    @Override
    public <T> T getInheritedValue(String propertyName, Breakpoint breakpoint, T defaultValue) {
        return getInheritedValue(propertyName, propertyName, breakpoint, defaultValue);
    }

    @Override
    public <T> T getInheritedValue(String propertyName, String inheritPropertyName, Breakpoint breakpoint,
                                   T defaultValue) {
        LinkedHashMap<String, Object> values = get(propertyName);
        if (values != null) {
            if (values.get(breakpoint.key()) != null) {
                return (T) values.get(breakpoint.key());
            }
            if (shouldInherit(breakpoint, properties, inheritPropertyName)) {
                Breakpoint previous = getPreviousBreakpoint(breakpoint);
                if (previous != null) {
                    return getInheritedValue(propertyName, previous, defaultValue);
                }
            }
        }
        return defaultValue;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public LinkedHashMap<String, Object> put(String key, LinkedHashMap<String, Object> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LinkedHashMap<String, Object> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends LinkedHashMap<String, Object>> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Collection<LinkedHashMap<String, Object>> values() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Set<Entry<String, LinkedHashMap<String, Object>>> entrySet() {
        throw new UnsupportedOperationException();
    }

}
