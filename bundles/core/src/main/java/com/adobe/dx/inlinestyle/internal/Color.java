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

import static com.adobe.dx.utils.RequestUtil.getFromRespProps;

import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.inlinestyle.InlineStyleWorker;
import com.adobe.dx.styleguide.StyleGuideUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class Color implements InlineStyleWorker {
    private static final String KEY = "color";

    private static final String PN_COLOR = "foregroundColor";
    private static final String FORMAT = "color: %s";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public @Nullable String getDeclaration(@Nullable Breakpoint breakpoint, SlingHttpServletRequest request) {
        String colorKey = getFromRespProps(request, breakpoint, PN_COLOR);
        String color = StyleGuideUtil.getColor(request, colorKey);
        if (StringUtils.isNotBlank(color)) {
            return String.format(FORMAT, color);
        }
        return null;
    }

    @Override
    public @Nullable String getRule(@Nullable Breakpoint breakpoint, @Nullable String id,
                                    SlingHttpServletRequest request) {
        return null;
    }
}
