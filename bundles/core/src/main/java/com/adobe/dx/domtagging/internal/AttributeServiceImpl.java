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

package com.adobe.dx.domtagging.internal;

import static com.adobe.dx.utils.CSSConstants.SPACE;

import com.adobe.dx.domtagging.AttributeService;
import com.adobe.dx.domtagging.AttributeWorker;
import com.adobe.dx.utils.AbstractWorkerManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class AttributeServiceImpl extends AbstractWorkerManager<AttributeWorker> implements AttributeService {
    Logger logger = LoggerFactory.getLogger(AttributeServiceImpl.class);

    private static final String PN_ATTRIBUTEWORKERS = "attributeWorkers";

    @Reference(service= AttributeWorker.class,
        cardinality= ReferenceCardinality.MULTIPLE,
        policy= ReferencePolicy.DYNAMIC, policyOption= ReferencePolicyOption.GREEDY,
        bind = "bindWorker", unbind = "unbindWorker")
    final List<AttributeWorker> workers = new ArrayList<>();

    @Override
    public @Nullable Map<String, String> getAttributes(SlingHttpServletRequest request) {
        Map<String, String> attributes = null;
        for (String key : getWorkerKeys(request.getResource())) {
            AttributeWorker worker = workersMap.get(key);
            if (worker != null) {
                logger.debug("get attributes from worker {}", worker.getKey());
                Map<String, String> workerMap = worker.getAttributes(request);
                if (attributes == null) {
                    attributes = workerMap;
                } else {
                    attributes.putAll(workerMap);
                }
            }
        }
        return attributes;
    }

    @Override
    public @Nullable String getClassesString(SlingHttpServletRequest request) {
        Collection<String> classes = null;
        for (String key : getWorkerKeys(request.getResource())) {
            AttributeWorker worker = workersMap.get(key);
            if (worker != null) {
                logger.debug("get classes from worker {}", worker.getKey());
                Collection<String> workerClasses = worker.getClasses(request);
                if (classes == null) {
                    classes = new ArrayList<>();
                }
                classes.addAll(workerClasses);
            }
        }
        return classes != null ? String.join(SPACE, classes) : null;
    }

    @Override
    protected List<AttributeWorker> getWorkers() {
        return workers;
    }

    @Override
    protected String getProperty() {
        return PN_ATTRIBUTEWORKERS;
    }

    @Override
    public void bindWorker(AttributeWorker worker) {
        super.bindWorker(worker);
    }

    @Override
    public void unbindWorker(AttributeWorker worker) {
        super.unbindWorker(worker);
    }
}
