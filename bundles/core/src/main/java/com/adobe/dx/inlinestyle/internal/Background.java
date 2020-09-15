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

import static com.adobe.dx.inlinestyle.Constants.DEL_SPACE;
import static com.adobe.dx.utils.RequestUtil.getFromRespProps;

import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.inlinestyle.InlineStyleWorker;
import com.adobe.dx.styleguide.StyleGuideUtil;
import com.day.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class Background implements InlineStyleWorker {
    private static final String KEY = "background";
    private static final String PN_BACKGROUNDCOLOR = "backgroundColor";
    private static final String PN_GRADIENT = "gradient";
    private static final String PN_IMAGE = "fileReference";
    private static final String PN_FOCUSX = "focusX";
    private static final String PN_FOCUSY = "focusY";
    private static final String COLOR_FORMAT = "background-color: %s";
    private static final String IMAGE_FORMAT = "background-image: %s";
    private static final String BG_SIZE = "background-size: cover";
    private static final String POSITION_PREFIX = "background-position: ";
    private static final String POSITION_UNIT = "% ";
    private static final String IMAGE_DECLARATION = "url(%s)";
    private static final String IMG_DECLARATION_DELIMITER = ",";

    @Override
    public String getKey() {
        return KEY;
    }


    private String generateColorDeclaration(String bgColor) {
        if (StringUtils.isNotBlank(bgColor)) {
            return String.format(COLOR_FORMAT, bgColor);
        }
        return null;
    }

    private String generateImageDeclaration(String gradient, String image) {
        if (StringUtils.isNotBlank(gradient) || StringUtils.isNotBlank(image)) {
            String imageDeclaration = null;
            if (StringUtils.isNotBlank(image)) {
                imageDeclaration = String.format(IMAGE_DECLARATION, Text.escape(image));
            }
            return String.format(IMAGE_FORMAT, Arrays.asList(gradient, imageDeclaration).stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(IMG_DECLARATION_DELIMITER)));
        }
        return null;
    }

    private String generateSize(String image) {
        if (StringUtils.isNotBlank(image)) {
            return BG_SIZE;
        }
        return null;
    }

    private String generatePosition(String image, @Nullable Breakpoint breakpoint, SlingHttpServletRequest request) {
        if (StringUtils.isNotBlank(image)) {
            Long focusX = getFromRespProps(request, breakpoint, PN_FOCUSX, Long.class);
            Long focusY = getFromRespProps(request, breakpoint, PN_FOCUSY, Long.class);
            if (focusX != null && focusY != null) {
                return POSITION_PREFIX + focusX + POSITION_UNIT + focusY + POSITION_UNIT;
            }
        }
        return null;
    }

    @Override
    public @Nullable String getDeclaration(@Nullable Breakpoint breakpoint, SlingHttpServletRequest request) {
        String bgColorKey = getFromRespProps(request, breakpoint, PN_BACKGROUNDCOLOR, String.class);
        String bgColor = StyleGuideUtil.getColor(request, bgColorKey);
        String gradientKey = getFromRespProps(request, breakpoint, PN_GRADIENT, String.class);
        String gradient = StyleGuideUtil.getGradient(request, gradientKey);
        String image = getFromRespProps(request, breakpoint, PN_IMAGE, String.class);
        if (bgColor != null || gradient != null || image != null) {
            List<String> declarations = new ArrayList<>();
            declarations.add(generateColorDeclaration(bgColor));
            declarations.add(generateImageDeclaration(gradient, image));
            declarations.add(generateSize(image));
            declarations.add(generatePosition(image, breakpoint, request));
            return declarations.stream()
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(DEL_SPACE));
        }
        return null;
    }

    @Override
    public @Nullable String getRule(@Nullable Breakpoint breakpoint, @Nullable String id,
                                    SlingHttpServletRequest request) {
        return null;
    }
}
