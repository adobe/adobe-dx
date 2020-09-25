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

import static com.adobe.dx.utils.CSSConstants.BOTTOM;
import static com.adobe.dx.utils.CSSConstants.LEFT;
import static com.adobe.dx.utils.CSSConstants.PN_BOTTOM;
import static com.adobe.dx.utils.CSSConstants.DECLARATION;
import static com.adobe.dx.utils.CSSConstants.DEL_SPACE;
import static com.adobe.dx.utils.CSSConstants.PN_LEFT;
import static com.adobe.dx.utils.CSSConstants.PX;
import static com.adobe.dx.utils.CSSConstants.PX_SPACE;
import static com.adobe.dx.utils.CSSConstants.PN_RIGHT;
import static com.adobe.dx.utils.CSSConstants.RIGHT;
import static com.adobe.dx.utils.CSSConstants.SPACE;
import static com.adobe.dx.utils.CSSConstants.PN_TOP;
import static com.adobe.dx.utils.CSSConstants.TOP;
import static com.adobe.dx.utils.RequestUtil.getFromRespProps;

import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.inlinestyle.InlineStyleWorker;
import com.adobe.dx.styleguide.StyleGuideUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class Border implements InlineStyleWorker {
    private static final String DECL_PREFIX = "border";
    private static final String KEY = DECL_PREFIX;
    private static final String PREFIX = KEY;
    private static final String COLOR_SUFFIX = "Color";
    private static final String STYLE_SUFFIX = "Style";
    private static final String WIDTH_SUFFIX = "Width";
    private static final String RADIUS = "Radius";
    private static final String DECL_RADIUS = "border-radius: ";
    private static final String DECL_TOP = DECL_PREFIX + TOP;
    private static final String DECL_BOTTOM = DECL_PREFIX + BOTTOM;
    private static final String DECL_RIGHT = DECL_PREFIX + RIGHT;
    private static final String DECL_LEFT = DECL_PREFIX + LEFT;
    private static final String ALL = "all";
    private static final String EACH = "each";
    private static final String ALL_CAP = "All";
    private static final String PN_BORDERRADIUS = PREFIX + RADIUS;
    private static final String PN_ALLRADIUS = PREFIX + ALL_CAP + RADIUS;
    private static final String PN_RADIUS_TOPLEFT = PREFIX + RADIUS + PN_TOP + PN_LEFT;
    private static final String PN_RADIUS_TOPRIGHT = PREFIX + RADIUS + PN_TOP + PN_RIGHT;
    private static final String PN_RADIUS_BOTTOMLEFT = PREFIX + RADIUS + PN_BOTTOM + PN_LEFT;
    private static final String PN_RADIUS_BOTTOMRIGHT = PREFIX + RADIUS + PN_BOTTOM + PN_RIGHT;
    private static final String PN_SIDES = PREFIX + "Sides";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public @Nullable String getDeclaration(Breakpoint breakpoint, SlingHttpServletRequest request) {
        String border = buildBorder(breakpoint, request);
        String radius = buildRadius(breakpoint, request);
        if (border != null) {
            if (radius != null) {
                return border + DEL_SPACE + radius;
            } else {
                return border;
            }
        } else if (radius != null) {
            return radius;
        }
        return null;
    }

    @Override
    public @Nullable String getRule(@Nullable Breakpoint breakpoint, @Nullable String id,
                                    SlingHttpServletRequest request) {
        return null;
    }

    private String buildBorder(Breakpoint breakpoint, SlingHttpServletRequest request) {
        String borderSides = getFromRespProps(request, breakpoint,PN_SIDES);
        if (StringUtils.equals(ALL, borderSides)) {
            return getAllBorders(breakpoint, request);
        } else if (StringUtils.equals(EACH, borderSides)) {
            return getEachBorder(breakpoint, request);
        }
        return null;
    }

    private String getAllBorders(Breakpoint breakpoint, SlingHttpServletRequest request) {
        return getBorderStyle(breakpoint, request, ALL_CAP, DECL_PREFIX);
    }

    private String getEachBorder(Breakpoint breakpoint, SlingHttpServletRequest request) {
        List<String> borders = new ArrayList<>();
        final String topBorder = getBorderStyle(breakpoint, request, PN_TOP, DECL_TOP);
        final String rightBorder = getBorderStyle(breakpoint, request, PN_RIGHT, DECL_RIGHT);
        final String bottomBorder = getBorderStyle(breakpoint, request, PN_BOTTOM, DECL_BOTTOM);
        final String leftBorder = getBorderStyle(breakpoint, request, PN_LEFT, DECL_LEFT);
        if (topBorder != null) {
            borders.add(topBorder);
        }
        if (rightBorder != null) {
            borders.add(rightBorder);
        }
        if (bottomBorder != null) {
            borders.add(bottomBorder);
        }
        if (leftBorder != null) {
            borders.add(leftBorder);
        }
        return borders.isEmpty() ? null : String.join(DEL_SPACE, borders);
    }

    private String getBorderStyle(Breakpoint breakpoint, SlingHttpServletRequest request, String side, String propertyName) {
        String borderStyle = getFromRespProps(request, breakpoint, PREFIX + side + STYLE_SUFFIX);
        long borderThickness = getFromRespProps(request, breakpoint,PREFIX + side + WIDTH_SUFFIX, 0L);
        String borderColorKey = getFromRespProps(request, breakpoint,PREFIX + side + COLOR_SUFFIX);
        String borderColor = StyleGuideUtil.getColor(request, borderColorKey);
        if (borderStyle != null && borderThickness > 0 && borderColor != null) {
            return propertyName + DECLARATION + borderStyle + SPACE + borderThickness + PX_SPACE + borderColor;
        }
        return null;
    }

    private String buildRadius(Breakpoint breakpoint, SlingHttpServletRequest request) {
        String borderRadius = getFromRespProps(request, breakpoint, PN_BORDERRADIUS);
        if (StringUtils.equals(ALL, borderRadius)) {
            return getAllBorderRadius(breakpoint, request);
        } else if (StringUtils.equals(EACH, borderRadius)) {
            return getEachBorderRadius(breakpoint, request);
        }
        return null;
    }

    private String getAllBorderRadius(Breakpoint breakpoint, SlingHttpServletRequest request) {
        long borderRadius = getFromRespProps(request, breakpoint, PN_ALLRADIUS, 0L);
        if (borderRadius > 0) {
            return DECL_RADIUS + borderRadius + PX;
        }
        return null;
    }

    private String getEachBorderRadius(Breakpoint breakpoint, SlingHttpServletRequest request) {
        long radiusTopLeft = getFromRespProps(request, breakpoint, PN_RADIUS_TOPLEFT, 0L);
        long radiusTopRight = getFromRespProps(request, breakpoint, PN_RADIUS_TOPRIGHT, 0L);
        long radiusBottomRight = getFromRespProps(request, breakpoint, PN_RADIUS_BOTTOMRIGHT, 0L);
        long radiusBottomLeft = getFromRespProps(request, breakpoint, PN_RADIUS_BOTTOMLEFT, 0L);

        if (radiusTopLeft > 0 || radiusTopRight > 0 || radiusBottomLeft > 0 || radiusBottomRight > 0) {
            return DECL_RADIUS
                + radiusTopLeft + PX_SPACE + radiusTopRight + PX_SPACE
                + radiusBottomRight + PX_SPACE + radiusBottomLeft + PX;
        }
        return null;
    }
}
