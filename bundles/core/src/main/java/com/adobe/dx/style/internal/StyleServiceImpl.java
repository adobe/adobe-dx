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

import static com.adobe.dx.style.Constants.DECLARATION_DELIMITER;
import static com.day.cq.wcm.commons.Constants.EMPTY_STRING_ARRAY;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.adobe.dx.bindings.internal.DxBindingsValueProvider;
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.style.StyleWorker;
import com.adobe.dx.style.StyleService;
import com.adobe.dx.utils.RequestUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.record.PageBreakRecord;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
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
public class StyleServiceImpl implements StyleService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String FORMAT_ID = "#%s {%s}";
    private static final String FORMAT_BP = "\n%s {\n%s\n}";
    private static final String SLASH = "/";
    private static final String TYPE_PREFIX = "/apps/";
    private static final String STYLEWORKERS_SUFFIX = "/styleWorkers";

    @Reference(service= StyleWorker.class,
        cardinality= ReferenceCardinality.MULTIPLE,
        policy= ReferencePolicy.DYNAMIC, policyOption= ReferencePolicyOption.GREEDY,
        bind = "bindWorker", unbind = "unbindWorker")
    volatile List<StyleWorker> workers = new ArrayList<>();

    Map<String, StyleWorker> workerMap = MapUtils.EMPTY_MAP;

    String getStylePerBreakpoint(String id, Breakpoint breakpoint, String[] keys, SlingHttpServletRequest request) {
        List<String> declarations = null;
        for (String workerKey : keys) {
            if (workerMap.containsKey(workerKey)) {
                logger.debug("found {} worker", workerKey);
                String declaration = workerMap.get(workerKey).getDeclaration(breakpoint, request);
                if (StringUtils.isNotBlank(declaration)) {
                    logger.debug("generated {}", declaration);
                    if (declarations == null) {
                        declarations = new ArrayList<>();
                    }
                    declarations.add(declaration);
                }
            }
        }
        if (declarations != null && !declarations.isEmpty()) {
            String concat = String.join(DECLARATION_DELIMITER, declarations);
            return StringUtils.isNotBlank(id) ? String.format(FORMAT_ID, id, concat) : concat;
        }
        return EMPTY;
    }

    @Override
    public String getLocalStyle(String id, SlingHttpServletRequest request) {
        Resource resource = request.getResource();
        String[] keys = getWorkerKeys(resource);
        if (keys.length > 0) {
            StringBuilder style = new StringBuilder();
            String defaultStyle = getStylePerBreakpoint(id, null, keys, request);
            if (StringUtils.isNotBlank(defaultStyle)) {
                style.append(defaultStyle);
            }
            Breakpoint[] breakpoints = RequestUtil.getBreakpoints(request);
            if (breakpoints != null) {
                for (Breakpoint breakpoint : breakpoints) {
                    String bpStyle = getStylePerBreakpoint(id, breakpoint, keys, request);
                    if (StringUtils.isNotBlank(bpStyle)) {
                        style.append(String.format(FORMAT_BP, breakpoint.mediaQuery(), bpStyle));
                    }
                }
            }
            if (style.length() > 0) {
                return style.toString();
            }
        }
        return EMPTY;
    }

    /**
     * returns ordered list of workers for that given resource (or null)
     */
    @NotNull String[] getWorkerKeys(Resource resource) {
        String type = resource.getResourceType();
        String typePath = (type.startsWith(SLASH) ? type : TYPE_PREFIX + type) + STYLEWORKERS_SUFFIX;
        Resource keys = resource.getResourceResolver().getResource(typePath);
        if (keys != null) {
            return keys.adaptTo(String[].class);
        }
        return EMPTY_STRING_ARRAY;
    }

    void refreshWorkers() {
        Map<String, StyleWorker> map = new HashMap<>();
        for (StyleWorker worker : workers) {
            map.put(worker.getKey(), worker);
        }
        workerMap = map;
    }

    void bindWorker(StyleWorker worker) {
        workers.add(worker);
        refreshWorkers();
    }

    void unbindWorker(StyleWorker worker) {
        workers.remove(worker);
        refreshWorkers();
    }
}
