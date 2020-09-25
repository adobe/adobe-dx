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
package com.adobe.dx.structure.flex;

import static com.adobe.dx.utils.CSSConstants.DEL_SPACE;
import static com.adobe.dx.utils.CSSConstants.RULE_DELIMITER;
import static com.adobe.dx.structure.flex.FlexModel.PN_MINHEIGHT_TYPE;
import static com.adobe.dx.structure.flex.FlexModel.PN_MINHEIGHT_VALUE;

import com.adobe.dx.inlinestyle.InlineStyleWorker;
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.utils.RequestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * Sets up general inline style for items container (min height, and gap)
 */
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class FlexGeneralStyle implements InlineStyleWorker {
    private static final String FLEX_GENERAL = "flex-general";
    private static final String RULE_CONTAINER = "#%s > .dx-flex-items {\n%s\n}";
    private static final String RULE_ITEM = "#%s > .dx-flex-items > * {\n%s\n}";
    private static final String PN_GAP = "gap";
    private static final String MIN_HEIGHT_PREFIX  = "min-height: ";
    private static final String GAP_CONTAINER = "margin: -%spx";
    private static final String GAP_ITEM_DECLARATION = "border: 0 solid transparent; border-width: %spx";

    @Override
    public String getKey() {
        return FLEX_GENERAL;
    }

    @Override
    public @Nullable String getDeclaration(@Nullable Breakpoint breakpoint, SlingHttpServletRequest request) {
        return null;
    }

    String computeMinHeight(Breakpoint breakpoint, SlingHttpServletRequest request) {
        Long minHeight = RequestUtil.getFromRespProps(request, breakpoint, PN_MINHEIGHT_VALUE);
        if ( minHeight != null) {
            String minHeightType = RequestUtil.getFromRespProps(request, breakpoint, PN_MINHEIGHT_TYPE);
            if (minHeightType != null) {
                return MIN_HEIGHT_PREFIX + minHeight.toString() + minHeightType;
            }
        }
        return null;
    }

    String computeGapContainer(Long gap) {
        if (gap != null) {
            return String.format(GAP_CONTAINER, gap.toString());
        }
        return null;
    }

    @Override
    public @Nullable String getRule(Breakpoint breakpoint, @Nullable String id,
                                    SlingHttpServletRequest request) {
        List<String> rules = null;
        String minHeight = computeMinHeight(breakpoint, request);
        Long gap = RequestUtil.getFromRespProps(request, breakpoint, PN_GAP);
        gap = gap != null ? gap / 2 : null;
        String gapContainer = computeGapContainer(gap);
        if (StringUtils.isNotBlank(minHeight) || StringUtils.isNotBlank(gapContainer)) {
            rules = new ArrayList<>();
            rules.add(String.format(RULE_CONTAINER, id, Arrays.asList(minHeight, gapContainer).stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(DEL_SPACE))));
        }
        if (gap != null) {
            rules = rules == null ? new ArrayList<>() : rules;
            rules.add(String.format(RULE_ITEM, id, String.format(GAP_ITEM_DECLARATION, gap.toString())));
        }
        return rules != null ? String.join(RULE_DELIMITER, rules) : null;
    }

}
