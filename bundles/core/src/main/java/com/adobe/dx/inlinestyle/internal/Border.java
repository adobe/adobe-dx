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

import static com.adobe.dx.inlinestyle.Constants.DECLARATION;
import static com.adobe.dx.inlinestyle.Constants.DEL_SPACE;
import static com.adobe.dx.inlinestyle.Constants.PX;
import static com.adobe.dx.inlinestyle.Constants.PX_SPACE;
import static com.adobe.dx.inlinestyle.Constants.SPACE;
import static com.adobe.dx.utils.RequestUtil.getPolicy;

import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.inlinestyle.InlineStyleWorker;
import com.adobe.dx.styleguide.StyleGuideUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
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
    private static final String TOP = "Top";
    private static final String RIGHT = "Right";
    private static final String LEFT = "Left";
    private static final String BOTTOM = "Bottom";
    private static final String DECL_RADIUS = "border-radius: ";
    private static final String DECL_TOP = DECL_PREFIX + "-top";
    private static final String DECL_BOTTOM = DECL_PREFIX + "-bottom";
    private static final String DECL_RIGHT = DECL_PREFIX + "-right";
    private static final String DECL_LEFT = DECL_PREFIX + "-left";
    private static final String ALL = "all";
    private static final String EACH = "each";
    private static final String ALL_CAP = "All";
    private static final String PN_BORDERRADIUS = PREFIX + RADIUS;
    private static final String PN_ALLRADIUS = PREFIX + ALL_CAP + RADIUS;
    private static final String PN_RADIUS_TOPLEFT = PREFIX + RADIUS + TOP + LEFT;
    private static final String PN_RADIUS_TOPRIGHT = PREFIX + RADIUS + TOP + RIGHT;
    private static final String PN_RADIUS_BOTTOMLEFT = PREFIX + RADIUS + BOTTOM + LEFT;
    private static final String PN_RADIUS_BOTTOMRIGHT = PREFIX + RADIUS + BOTTOM + RIGHT;
    private static final String PN_SIDES = PREFIX + "Sides";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public @Nullable String getDeclaration(Breakpoint breakpoint, SlingHttpServletRequest request) {
        if (StringUtils.isBlank(breakpoint.mediaQuery())) {
            //we only do border for all
            ValueMap dxPolicy = getPolicy(request);
            String border = buildBorder(request, dxPolicy);
            String radius = buildRadius(dxPolicy);
            if (border != null) {
                if (radius != null) {
                    return border + DEL_SPACE + radius;
                } else {
                    return border;
                }
            } else if (radius != null) {
                return radius;
            }
        }
        return null;
    }

    @Override
    public @Nullable String getRule(@Nullable Breakpoint breakpoint, @Nullable String id,
                                    SlingHttpServletRequest request) {
        return null;
    }

    private String buildBorder(SlingHttpServletRequest request, ValueMap policy) {
        String borderSides = policy.get(PN_SIDES, String.class);
        if (StringUtils.equals(ALL, borderSides)) {
            return getAllBorders(request, policy);
        } else if (StringUtils.equals(EACH, borderSides)) {
            return getEachBorder(request, policy);
        }
        return null;
    }

    private String getAllBorders(SlingHttpServletRequest request, ValueMap policy) {
        return getBorderStyle(request, policy, ALL_CAP, DECL_PREFIX);
    }

    private String getEachBorder(SlingHttpServletRequest request, ValueMap policy) {
        List<String> borders = new ArrayList<>();
        final String topBorder = getBorderStyle(request, policy, TOP, DECL_TOP);
        final String rightBorder = getBorderStyle(request, policy, RIGHT, DECL_RIGHT);
        final String bottomBorder = getBorderStyle(request, policy, BOTTOM, DECL_BOTTOM);
        final String leftBorder = getBorderStyle(request, policy, LEFT, DECL_LEFT);
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

    private String getBorderStyle(SlingHttpServletRequest request, ValueMap policy, String side, String propertyName) {
        String borderStyle = policy.get(PREFIX + side + STYLE_SUFFIX, String.class);
        long borderThickness = policy.get(PREFIX + side + WIDTH_SUFFIX, 0L);
        String borderColorKey = policy.get(PREFIX + side + COLOR_SUFFIX, String.class);
        String borderColor = StyleGuideUtil.getColor(request, borderColorKey);
        if (borderStyle != null && borderThickness > 0 && borderColor != null) {
            return propertyName + DECLARATION + borderStyle + SPACE + borderThickness + PX_SPACE + borderColor;
        }
        return null;
    }

    private String buildRadius(ValueMap policy) {
        String borderRadius = policy.get(PN_BORDERRADIUS, String.class);
        if (StringUtils.equals(ALL, borderRadius)) {
            return getAllBorderRadius(policy);
        } else if (StringUtils.equals(EACH, borderRadius)) {
            return getEachBorderRadius(policy);
        }
        return null;
    }

    private String getAllBorderRadius(ValueMap policy) {
        long borderRadius = policy.get(PN_ALLRADIUS, 0L);
        if (borderRadius > 0) {
            return DECL_RADIUS + borderRadius + PX;
        }
        return null;
    }

    private String getEachBorderRadius(ValueMap policy) {
        long radiusTopLeft = policy.get(PN_RADIUS_TOPLEFT, 0L);
        long radiusTopRight = policy.get(PN_RADIUS_TOPRIGHT, 0L);
        long radiusBottomRight = policy.get(PN_RADIUS_BOTTOMRIGHT, 0L);
        long radiusBottomLeft = policy.get(PN_RADIUS_BOTTOMLEFT, 0L);

        if (radiusTopLeft > 0 || radiusTopRight > 0 || radiusBottomLeft > 0 || radiusBottomRight > 0) {
            return DECL_RADIUS
                + radiusTopLeft + PX_SPACE + radiusTopRight + PX_SPACE
                + radiusBottomRight + PX_SPACE + radiusBottomLeft + PX;
        }
        return null;
    }
}
