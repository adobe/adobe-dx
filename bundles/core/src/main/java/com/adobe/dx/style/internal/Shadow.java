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

import static com.adobe.dx.style.Constants.PX_SPACE;
import static com.adobe.dx.utils.RequestUtil.getPolicy;

import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.style.StyleWorker;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class Shadow implements StyleWorker {

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
        if (breakpoint == null) {
            //we only do border for all
            ValueMap dxPolicy = getPolicy(request);
            String color = dxPolicy.get(PN_COLOR, String.class);
            if (color != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(RULE)
                    .append(dxPolicy.get(PN_OFFSETX, 0L)).append(PX_SPACE)
                    .append(dxPolicy.get(PN_OFFSETY, 0L)).append(PX_SPACE)
                    .append(dxPolicy.get(PN_BLUR, 0L)).append(PX_SPACE)
                    .append(dxPolicy.get(PN_SPREAD, 0L)).append(PX_SPACE)
                    .append(color);
                if (dxPolicy.containsKey(PN_INSET)) {
                    sb.append(INSET_SUFFIX);
                }
                return sb.toString();
            }
        }
        return null;
    }
}
