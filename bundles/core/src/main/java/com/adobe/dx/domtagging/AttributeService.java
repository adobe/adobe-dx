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

package com.adobe.dx.domtagging;

import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.Nullable;

/**
 * Dynamically binds to the <code>AttributeWorker</code> instances in place, to provide contextual
 * set of attributes for that component resource
 */
public interface AttributeService {

    /**
     * @param request current component request context
     * @return map of attributes to be used for a given tag, corresponding to current request context
     */
    @Nullable Map<String, String> getAttributes(SlingHttpServletRequest request);

}
