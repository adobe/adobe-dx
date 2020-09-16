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

import com.adobe.dx.bindings.internal.DxBindingsValueProvider;
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.responsive.InheritedMap;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for fetching utility objects from request
 */
public class RequestUtil {
    private RequestUtil() {
    }

    /**
     * @param request current request
     * @return sling bindings
     */
    public static final SlingBindings getBindings(SlingHttpServletRequest request) {
        return (SlingBindings)request.getAttribute(SlingBindings.class.getName());
    }

    /**
     * @param request current request
     * @return current DX policy
     */
    public static final ValueMap getPolicy(SlingHttpServletRequest request) {
        return (ValueMap)getBindings(request).get(DxBindingsValueProvider.POLICY_KEY);
    }

    /**
     * @param request current request
     * @return current list of breakpoints
     */
    public static final @NotNull List<Breakpoint> getBreakpoints(SlingHttpServletRequest request) {
         List<Breakpoint> breakpoints = (List<Breakpoint>) getBindings(request).get(DxBindingsValueProvider.BP_KEY);
         if (breakpoints == null) {
             breakpoints = Collections.emptyList();
         }
         return breakpoints;
    }

    /**
     * @param request current request
     * @return current set of responsive properties
     */
    public static final Map<String, LinkedHashMap<String, Object>> getResponsiveProperties(SlingHttpServletRequest request) {
        return (Map<String, LinkedHashMap<String, Object>>)getBindings(request).get(DxBindingsValueProvider.RESP_PROPS_KEY);
    }

    /**
     * @param request current request
     * @return current set of responsive properties
     */
    public static final InheritedMap getInheritedMap(SlingHttpServletRequest request) {
        return (InheritedMap) getBindings(request).get(DxBindingsValueProvider.RESP_PROPS_KEY);
    }

    /**
     * Get value or null of a given property for a given breakpoint
     *
     * @param request current request
     * @param breakpoint current breakpoint
     * @param propertyName required property name
     * @param <T> type of the value required
     * @return value or null if not found
     */
    public static final <T> T getFromRespProps(SlingHttpServletRequest request, Breakpoint breakpoint, String propertyName) {
        Map<String, LinkedHashMap<String, Object>> resprops = RequestUtil.getResponsiveProperties(request);
        if (resprops != null) {
            LinkedHashMap<String, Object> values = resprops.get(propertyName);
            if (values != null && values.get(breakpoint.key()) != null) {
                return (T) values.get(breakpoint.key());
            }
        }
        return null;
    }

}
