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
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.adobe.dx.bindings.internal.DxBindingsValueProvider;
import com.adobe.dx.style.StyleWorker;
import com.adobe.dx.style.StyleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
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
    private static final String SLASH = "/";
    private static final String TYPE_PREFIX = "/apps/";
    private static final String STYLEWORKERS_SUFFIX = "/styleWorkers";

    @Reference(service= StyleWorker.class,
        cardinality= ReferenceCardinality.MULTIPLE,
        policy= ReferencePolicy.DYNAMIC, policyOption= ReferencePolicyOption.GREEDY,
        bind = "bindWorker", unbind = "unbindWorker")
    volatile List<StyleWorker> workers = new ArrayList<>();

    Map<String, StyleWorker> workerMap = MapUtils.EMPTY_MAP;

    @Override
    public String getLocalStyle(String id, SlingHttpServletRequest request) {
        List<String> declarations = null;
        Resource resource = request.getResource();
        String[] keys = getWorkerKeys(resource);
        if (keys != null) {
            SlingBindings bindings = (SlingBindings)request.getAttribute(SlingBindings.class.getName());
            ValueMap dxPolicy = bindings != null ? (ValueMap)bindings.get(DxBindingsValueProvider.POLICY_KEY) : ValueMap.EMPTY;
            for (String workerKey : keys) {
                StyleWorker worker = workerMap.get(workerKey);
                if (worker != null) {
                    String declaration = worker.getDeclaration(resource, dxPolicy);
                    if (StringUtils.isNotBlank(declaration)) {
                        logger.debug("generated {} from {}", declaration, request);
                        if (declarations == null) {
                            declarations = new ArrayList<>();
                        }
                        declarations.add(declaration);
                    }
                } else {
                    logger.debug("{} was required resource type {}, but no associated worker is registered", workerKey,
                        resource.getResourceType());
                }
            }
            if (declarations != null && !declarations.isEmpty()) {
                String concat = String.join(DECLARATION_DELIMITER, declarations);
                return StringUtils.isNotBlank(id) ? String.format(FORMAT_ID, id, concat) : concat;
            }
        }
        return EMPTY;
    }

    /**
     * returns ordered list of workers for that given resource (or null)
     */
    String[] getWorkerKeys(Resource resource) {
        String type = resource.getResourceType();
        String typePath = (type.startsWith(SLASH) ? type : TYPE_PREFIX + type) + STYLEWORKERS_SUFFIX;
        Resource keys = resource.getResourceResolver().getResource(typePath);
        if (keys != null) {
            return keys.adaptTo(String[].class);
        }
        return null;
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
