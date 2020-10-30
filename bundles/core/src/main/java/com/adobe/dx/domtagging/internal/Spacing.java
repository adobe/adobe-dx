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

import static com.adobe.dx.utils.CSSConstants.CLASS_DELIMITER;
import static com.adobe.dx.utils.CSSConstants.PN_BOTTOM;
import static com.adobe.dx.utils.CSSConstants.PN_LEFT;
import static com.adobe.dx.utils.CSSConstants.PN_RIGHT;
import static com.adobe.dx.utils.CSSConstants.PN_TOP;

import com.adobe.dx.domtagging.AttributeWorker;
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.responsive.InheritedMap;
import com.adobe.dx.utils.RequestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class Spacing implements AttributeWorker {

    private static final String KEY = "spacing";

    private static final List<String> PREFIXES = Arrays.asList("margin", "padding");
    private static final List<String> SIDES = Arrays.asList(PN_TOP, PN_RIGHT, PN_BOTTOM, PN_LEFT);

    @Override
    public Map<String, String> getAttributes(SlingHttpServletRequest request) {
        return null;
    }

    Long getValue(Breakpoint breakpoint, String prefix, String side, InheritedMap map) {
        return map.getInheritedValue(prefix + side, prefix, breakpoint,null);
    }

    @Override
    public Collection<String> getClasses(SlingHttpServletRequest request) {
        Collection<String> classes = null;
        InheritedMap map = RequestUtil.getInheritedMap(request);
        for (Breakpoint breakpoint : RequestUtil.getBreakpoints(request)) {
            for (String prefix : PREFIXES) {
                for (String side : SIDES) {
                    Long value = getValue(breakpoint, prefix, side, map);
                    if (value != null) {
                        if (classes == null) {
                            classes = new ArrayList<>();
                        }
                        classes.add(String.join(CLASS_DELIMITER, Arrays.asList(breakpoint.key(),
                            prefix,
                            side.toLowerCase(),
                            value.toString())));
                    }
                }
            }
        }
        return classes;
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
