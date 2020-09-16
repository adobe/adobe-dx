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

import static com.adobe.dx.inlinestyle.Constants.DEL_SPACE;
import static com.adobe.dx.inlinestyle.Constants.PERCENT;
import static com.adobe.dx.inlinestyle.Constants.RULE_DELIMITER;
import static com.adobe.dx.structure.flex.FlexModel.PN_MINHEIGHT;
import static com.adobe.dx.structure.flex.FlexModel.PN_MINHEIGHT_TYPE;
import static com.adobe.dx.utils.RequestUtil.getInheritedMap;

import com.adobe.dx.inlinestyle.InlineStyleWorker;
import com.adobe.dx.responsive.Breakpoint;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * Sets up style for each item of the flex container
 */
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class FlexItemsDefinitions implements InlineStyleWorker {
    private static final String FLEX_DEFINITIONS = "flex-definitions";

    private static final String NN_DEFINITIONS = "definitions";
    private static final String PN_WIDTH = "width";
    private static final String PN_WIDTH_CUSTOMVALUE = PN_WIDTH + "CustomValue";
    private static final String PN_WIDTH_CUSTOMTYPE = PN_WIDTH + "CustomType";
    private static final String PN_MINHEIGHT_VALUE = PN_MINHEIGHT + "Value";
    private static final String PN_ORDER = "order";
    private static final String PN_JUSTIFICATION = "justification";
    private static final String PV_AUTO = "auto";
    private static final String PV_CUSTOM = "custom";
    private static final String PV_JUSTIFY_STRETCH = "JustifyStretch";

    private static final String CSS_WIDTH = "width: ";
    private static final String CSS_ORDER = "order: ";
    private static final String CSS_STRETCH_AUTO_WIDTH = "flex: 1 1 1%; max-width: 100%";
    private static final String CSS_AUTO_WIDTH = "flex: 0 0 auto; max-width: 100%; width: auto";
    private static final String CSS_MIN_HEIGHT = "min-height: ";
    private static final String CSS_AUTO = "auto";

    private static final String FORMAT_WIDTH = CSS_WIDTH + "%s; max-width: %s";
    private static final String FORMAT_COLUMNWIDTH = FORMAT_WIDTH + "; flex: 1 1 auto";
    private static final String FORMAT_RULE_DEFINITIONS = "#%s > .dx-flex-items > *:nth-child(%s) {\n%s\n}";

    @Override
    public String getKey() {
        return FLEX_DEFINITIONS;
    }

    @Override
    public @Nullable String getDeclaration(@Nullable Breakpoint breakpoint, SlingHttpServletRequest request) {
        return null;
    }

    @Override
    public @Nullable String getRule(Breakpoint breakpoint, @Nullable String id,
                                    SlingHttpServletRequest request) {
        Resource parent = getParent(breakpoint, request);
        if (parent != null) {
            List<String> definitions = null;
            int declarationIndex = 1;
            for (Iterator<Resource> children = parent.listChildren(); children.hasNext();) {
                String declaration = getDefinitionDeclaration(breakpoint, request, children.next());
                if (StringUtils.isNotBlank(declaration)) {
                    if (definitions == null) {
                        definitions = new ArrayList<>();
                    }
                    definitions.add(String.format(FORMAT_RULE_DEFINITIONS, id, declarationIndex++, declaration));
                }
            }
            if (definitions != null) {
                return String.join(RULE_DELIMITER, definitions);
            }
        }
        return null;
    }

    /**
     * looks for a definition parent for the given breakpoint
     * @param breakpoint
     * @param request
     * @return
     */
    private Resource getParent(Breakpoint breakpoint, SlingHttpServletRequest request) {
        String resourceName = NN_DEFINITIONS + breakpoint.propertySuffix();
        Resource parent = request.getResource().getChild(resourceName);
        if (parent != null) {
            return parent;
        }
        ResourceResolver resolver = request.getResourceResolver();
        ContentPolicyManager policyManager = resolver.adaptTo(ContentPolicyManager.class);
        if (policyManager != null) {
            ContentPolicy contentPolicy = policyManager.getPolicy(request.getResource());
            if (contentPolicy != null) {
                String path = contentPolicy.getPath();
                return resolver.getResource(path + "/" + resourceName);
            }
        }
        return null;
    }

    /**
     * Computes definition String
     * @param breakpoint
     * @param request
     * @param resource
     * @return
     */
    private String getDefinitionDeclaration(Breakpoint breakpoint, SlingHttpServletRequest request, Resource resource) {
        String declaration = Arrays.asList(buildWidth(breakpoint, request, resource.getValueMap()),
            buildCustomMinHeight(resource.getValueMap()),
            buildOrder(resource.getValueMap()))
            .stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(DEL_SPACE));
        if (StringUtils.isNotBlank(declaration)) {
            return declaration;
        }
        return null;
    }

    private String buildWidth(Breakpoint breakpoint, SlingHttpServletRequest request, ValueMap properties) {
        String width = properties.get(PN_WIDTH, String.class);
        // Check for custom width
        String widthValue = buildCustomWidth(properties, width);

        // Check for auto width
        if (StringUtils.equals(width, "auto")) {
            String justification = getInheritedMap(request).getInheritedValue(PN_JUSTIFICATION, breakpoint, PV_JUSTIFY_STRETCH);
            if (StringUtils.equals(justification, PV_JUSTIFY_STRETCH)) {
                widthValue = CSS_STRETCH_AUTO_WIDTH;
            } else {
                widthValue = CSS_AUTO_WIDTH;
            }
        }

        // Attempt Column Width
        if (StringUtils.isBlank(widthValue)) {
            widthValue = buildColumnWidth(width);
        }
        return widthValue;
    }

    private String buildCustomWidth(ValueMap properties, String width) {
        if (StringUtils.equals(width, PV_CUSTOM)) {
            long value = properties.get(PN_WIDTH_CUSTOMVALUE, 0L);
            String type = properties.get(PN_WIDTH_CUSTOMTYPE, String.class);
            if (value > 0 && StringUtils.isNotEmpty(type)) {
                String widthString = value + type;
                return String.format(FORMAT_WIDTH , widthString, widthString);
            }
        }
        return StringUtils.EMPTY;
    }


    private String buildColumnWidth(String width) {
        if (StringUtils.contains(width, PERCENT)) {
            return String.format(FORMAT_COLUMNWIDTH, width, width);
        }
        return StringUtils.EMPTY;
    }

    private String buildCustomMinHeight(ValueMap properties) {
        String minHeight = properties.get(PN_MINHEIGHT, String.class);
        if (StringUtils.equals(minHeight, PV_CUSTOM)) {
            long value = properties.get(PN_MINHEIGHT_VALUE, 0L);
            String type = properties.get(PN_MINHEIGHT_TYPE, String.class);
            if (value > 0 && StringUtils.isNotEmpty(type)) {
                return CSS_MIN_HEIGHT + value + type;
            }
        } else if (PV_AUTO.equals(minHeight)) {
            return CSS_MIN_HEIGHT + CSS_AUTO;
        }
        return StringUtils.EMPTY;
    }

    private String buildOrder(ValueMap properties) {
        Long order = properties.get(PN_ORDER, Long.class);
        if (order != null) {
            return CSS_ORDER + order;
        }
        return StringUtils.EMPTY;
    }

}
