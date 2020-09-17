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

import static com.adobe.dx.inlinestyle.Constants.DECLARATION_DELIMITER;
import static com.adobe.dx.inlinestyle.Constants.RULE_DELIMITER;
import static com.day.cq.wcm.commons.Constants.EMPTY_STRING_ARRAY;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.inlinestyle.InlineStyleWorker;
import com.adobe.dx.inlinestyle.InlineStyleService;
import com.adobe.dx.utils.RequestUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class InlineStyleServiceImpl implements InlineStyleService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String FORMAT_ID = "#%s {%s}";
    private static final String FORMAT_BP = "\n%s {\n%s\n}";
    private static final String SLASH = "/";
    private static final String TYPE_PREFIX = "apps/";
    private static final String PN_STYLEWORKERS = "styleWorkers";

    @Reference(service= InlineStyleWorker.class,
        cardinality= ReferenceCardinality.MULTIPLE,
        policy= ReferencePolicy.DYNAMIC, policyOption= ReferencePolicyOption.GREEDY,
        bind = "bindWorker", unbind = "unbindWorker")
    final List<InlineStyleWorker> workers = new ArrayList<>();

    Map<String, InlineStyleWorker> workerMap = MapUtils.EMPTY_MAP;

    @FunctionalInterface
    private interface StyleFunction<T1, T2, T3> {
        T3 apply(T1 t1, T2 t2, T3 t3);
    }

    List<String> generateAndAppend(List<String> existing,
                                   Breakpoint breakpoint, SlingHttpServletRequest request, String id,
                                   StyleFunction<SlingHttpServletRequest, Breakpoint, String> function) {
        String styleString = function.apply(request, breakpoint, id);
        if (StringUtils.isNotBlank(styleString)) {
            logger.debug("generated {}", styleString);
            if (existing == null) {
                existing = new ArrayList<>();
            }
            existing.add(styleString);
        }
        return existing;
    }

    String getStylePerBreakpoint(String id, Breakpoint breakpoint, String[] keys, SlingHttpServletRequest request) {
        String returnValue = EMPTY;
        List<String> declarations = null;
        List<String> rules = null;
        for (String workerKey : keys) {
            InlineStyleWorker worker = workerMap.get(workerKey);
            if (worker != null) {
                logger.debug("found {}", worker);
                declarations = generateAndAppend(declarations, breakpoint, request, id,
                    (r, b, i) -> worker.getDeclaration(b, r));
                rules = generateAndAppend(rules, breakpoint, request, id,
                    (r, b, i) -> worker.getRule(b, i, r));
            }
        }
        if (declarations != null && !declarations.isEmpty()) {
            String concat = String.join(DECLARATION_DELIMITER, declarations);
            returnValue = StringUtils.isNotBlank(id) ? String.format(FORMAT_ID, id, concat) : concat;
        }
        if (rules != null && !rules.isEmpty()) {
            String concat = String.join(RULE_DELIMITER, rules);
            returnValue = (returnValue.isEmpty() ? EMPTY : returnValue + RULE_DELIMITER) + concat;
        }
        return returnValue;
    }

    @Override
    public String getInlineStyle(String id, SlingHttpServletRequest request) {
        Resource resource = request.getResource();
        String[] keys = getWorkerKeys(resource);
        if (keys.length > 0) {
            StringBuilder style = new StringBuilder();
            for (Breakpoint breakpoint :  RequestUtil.getBreakpoints(request)) {
                String bpStyle = getStylePerBreakpoint(id, breakpoint, keys, request);
                if (StringUtils.isNotBlank(bpStyle)) {
                    style.append(StringUtils.isNotBlank(breakpoint.mediaQuery()) ?
                        String.format(FORMAT_BP, breakpoint.mediaQuery(), bpStyle):
                        bpStyle);
                }
            }
            if (style.length() > 0) {
                return style.toString().trim();
            }
        }
        return EMPTY;
    }

    /**
     * returns ordered list of workers for that given resource (or null)
     */
    @NotNull String[] getWorkerKeys(Resource resource) {
        String type = resource.getResourceType();
        String typePath = (type.startsWith(SLASH) ? type : TYPE_PREFIX + type);
        ValueMap props = resource.adaptTo(ConfigurationBuilder.class).name(typePath).asValueMap();
        String[] keys = props.get(PN_STYLEWORKERS, String[].class);
        if (keys != null) {
            return keys;
        }
        return EMPTY_STRING_ARRAY;
    }

    void refreshWorkers() {
        Map<String, InlineStyleWorker> map = new HashMap<>();
        for (InlineStyleWorker worker : workers) {
            map.put(worker.getKey(), worker);
        }
        workerMap = map;
    }

    void bindWorker(InlineStyleWorker worker) {
        workers.add(worker);
        refreshWorkers();
    }

    void unbindWorker(InlineStyleWorker worker) {
        workers.remove(worker);
        refreshWorkers();
    }
}
