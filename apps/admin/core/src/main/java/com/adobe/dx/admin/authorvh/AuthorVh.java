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
package com.adobe.dx.admin.authorvh;

import static com.day.cq.wcm.api.WCMMode.DISABLED;

import com.adobe.dx.domtagging.AttributeWorker;
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.utils.RequestUtil;
import com.day.cq.wcm.api.WCMMode;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Fetches pattern of CSS vh usage, and outputs them as attributes
 */
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = AuthorVh.Configuration.class)
public class AuthorVh implements AttributeWorker {

    private static final String CONFIG_SEPARATOR = ":";

    public static final String KEY = "authorvh";

    private static final String ATTRIBUTE_PREFIX = "data-author-vh-";

    private static final String VH_SEPARATOR = ",";

    private static final String ATTRIBUTE_PREFIX_ITEM = ATTRIBUTE_PREFIX + "item-";

    Configuration configuration;

    Map<String, String> typeMatch;

    Map<String, String> valueProperty;

    List<String> listParentNodeNames;

    @Activate
    @Modified
    public void activate(Configuration configuration) {
        this.configuration = configuration;
        Map<String, String> types = new HashMap<>();
        Map<String, String> values = new HashMap<>();
        for (String pattern : configuration.propertyMatching()) {
            String[] patterns = pattern.split(CONFIG_SEPARATOR);
            if (patterns.length != 3) {
                throw new IllegalArgumentException("there should be 3 parameters split by " + CONFIG_SEPARATOR);
            }
            types.put(patterns[0], patterns[1]);
            values.put(patterns[0], patterns[2]);
        }
        typeMatch = types;
        valueProperty = values;
        listParentNodeNames = Arrays.asList(configuration.itemParents());
    }

    /**
     * @param resource
     * @param request
     * @param breakpoint
     * @return first match or null
     */
    private String extractVH(Resource resource, SlingHttpServletRequest request, Breakpoint breakpoint) {
        for (Map.Entry<String, String> e : typeMatch.entrySet()) {
            String typeValue = breakpoint != null ? RequestUtil.getFromRespProps(request, breakpoint, e.getKey())
                : resource.getValueMap().get(e.getKey(), String.class);
            if (StringUtils.isNotBlank(typeValue) && (typeValue.equals(e.getValue()))) {
                String property = valueProperty.get(e.getKey());
                Long value = breakpoint != null ? RequestUtil.getFromRespProps(request, breakpoint, property)
                    : resource.getValueMap().get(property, Long.class);
                if (value != null) {
                    return value.toString();
                }
            }
        }
        return null;
    }

    private Map<String, String> addToMap(Map<String, String> existing, String key, String value) {
        if (existing == null) {
            existing = new HashMap<>();
        }
        existing.put(key, value);
        return existing;
    }

    private String generateItemAttributes(Resource resource, SlingHttpServletRequest request) {
        if (resource != null) {
            return (String) IteratorUtils.toList(resource.listChildren()).stream()
                .map(r -> extractVH((Resource) r, request, null))
                .map(s -> s != null ? s : StringUtils.EMPTY)
                .collect(Collectors.joining(VH_SEPARATOR));
        }
        return null;
    }

    @Override
    public Map<String, String> getAttributes(SlingHttpServletRequest request) {
        if (DISABLED.equals(WCMMode.fromRequest(request))) {
            return null;
        }

        Map<String, String> attributes = null;
        for (Breakpoint breakpoint : RequestUtil.getBreakpoints(request)) {
            if (breakpoint == null) {
                continue;
            }
            String vh = extractVH(request.getResource(), request, breakpoint);
            if (vh != null) {
                attributes = addToMap(attributes, ATTRIBUTE_PREFIX + breakpoint.key(), vh);
            }
            for (String parent : listParentNodeNames) {
                Resource parentResource = request.getResource().getChild(parent + breakpoint.propertySuffix());
                String value = generateItemAttributes(parentResource, request);
                if (value != null) {
                    attributes = addToMap(attributes, ATTRIBUTE_PREFIX_ITEM + breakpoint.key(), value);
                }
            }
        }
        return attributes;
    }

    @Override
    public Collection<String> getClasses(SlingHttpServletRequest request) {
        return null;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @ObjectClassDefinition(name = "Adobe Dx - Author VH Attribute worker")
    public @interface Configuration {

        @AttributeDefinition(
            name = "Property Matching scheme",
            description = "<typeProperty>:<typeValuePattern>:<valueProperty><br>"
                + "<ul><li>type property is the property name we look for a type value match,</li>"
                + "<li>typeValuePattern is a regexp we look at to check if current type property is a match.</li>"
                + "<li>Value property is the property we look at in case of a match</li>"
        )
        String[] propertyMatching();

        @AttributeDefinition(
            name = "Items parent node names",
            description = "itemParents"
        )
        String[] itemParents();
    }
}
