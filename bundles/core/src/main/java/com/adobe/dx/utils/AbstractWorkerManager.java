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

package com.adobe.dx.utils;

import static com.day.cq.wcm.commons.Constants.EMPTY_STRING_ARRAY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Get a list of workers depending on the resource type, and the bound workers
 * @param <T>
 */
public abstract class AbstractWorkerManager <T extends Worker> {

    protected abstract List<T> getWorkers();
    protected abstract String getProperty();

    private static final String SLASH = "/";
    private static final String TYPE_PREFIX = "apps/";


    protected Map<String, T> workersMap = MapUtils.EMPTY_MAP;

    /**
     * returns ordered list of workers for that given resource (or null)
     */
    protected @NotNull String[] getWorkerKeys(Resource resource) {
        String type = resource.getResourceType();
        String typePath = (type.startsWith(SLASH) ? type.substring(1) : TYPE_PREFIX + type);
        ValueMap props = resource.adaptTo(ConfigurationBuilder.class).name(typePath).asValueMap();
        String[] keys = props.get(getProperty(), String[].class);
        if (keys != null) {
            return keys;
        }
        return EMPTY_STRING_ARRAY;
    }

    protected void refreshWorkers() {
        Map<String, T> map = new HashMap<>();
        for (T worker : getWorkers()) {
            map.put(worker.getKey(), worker);
        }
        workersMap = map;
    }

    protected void bindWorker(T worker) {
        getWorkers().add(worker);
        refreshWorkers();
    }

    protected void unbindWorker(T worker) {
        getWorkers().remove(worker);
        refreshWorkers();
    }
}
