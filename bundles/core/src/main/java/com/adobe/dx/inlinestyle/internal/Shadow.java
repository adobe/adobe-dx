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
package com.adobe.dx.inlinestyle.internal;

import static com.adobe.dx.utils.CSSConstants.PX_SPACE;
import static com.adobe.dx.utils.RequestUtil.getPolicy;

import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.inlinestyle.InlineStyleWorker;
import com.adobe.dx.styleguide.StyleGuideUtil;
import com.adobe.dx.utils.RequestUtil;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class Shadow implements InlineStyleWorker {

    private static final String KEY = "shadow";
    private static final String RULE = "box-shadow: ";
    private static final String INSET_SUFFIX = " inset";
    private static final String PREFIX = KEY;
    private static final String PN_COLOR = PREFIX + "Color";
    private static final String PN_OFFSETX = PREFIX + "OffsetX";
    private static final String PN_OFFSETY = PREFIX + "OffsetY";
    private static final String PN_BLUR = PREFIX + "Blur";
    private static final String PN_SPREAD = PREFIX + "Spread";
    private static final String PN_INSET = PREFIX + "Inset";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public @Nullable String getDeclaration(Breakpoint breakpoint, SlingHttpServletRequest request) {
        String colorKey = RequestUtil.getFromRespProps(request, breakpoint, PN_COLOR);
        String color = StyleGuideUtil.getColor(request, colorKey);
        if (color != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(RULE)
                .append(RequestUtil.getFromRespProps(request, breakpoint, PN_OFFSETX, 0L)).append(PX_SPACE)
                .append(RequestUtil.getFromRespProps(request, breakpoint, PN_OFFSETY, 0L)).append(PX_SPACE)
                .append(RequestUtil.getFromRespProps(request, breakpoint, PN_BLUR, 0L)).append(PX_SPACE)
                .append(RequestUtil.getFromRespProps(request, breakpoint, PN_SPREAD, 0L)).append(PX_SPACE)
                .append(color);
            Object inset = RequestUtil.getFromRespProps(request, breakpoint, PN_INSET);
            if (inset != null && inset.toString().length() > 0) {
                sb.append(INSET_SUFFIX);
            }
            return sb.toString();
        }
        return null;
    }

    @Override
    public @Nullable String getRule(@Nullable Breakpoint breakpoint, @Nullable String id,
                                    SlingHttpServletRequest request) {
        return null;
    }
}
