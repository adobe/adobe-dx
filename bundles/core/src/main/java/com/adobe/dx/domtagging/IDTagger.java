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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.Nullable;

public interface IDTagger {

    /**
     * Get an ID for the given component request, depending on:
     * <ol>
     *  <li>property that has been manually set on the component resource,</li>
     *  <li>if nothing is set, eventual presence of the tag set on the component resource by that service,</li>
     *  <li>if still nothing generation of an id. In that case next request will generate another id</li>
     * </ol>
     * The service also prepend reference components at the page root level with the reference component id, to
     * avoid duplicate IDs as much as we can
     *
     * @param request current component request
     * @param property property that could override any id value, can be null
     * @return
     */
    String computeComponentId(final SlingHttpServletRequest request, @Nullable String property);

    /**
     * Tags, if needed and configured, a resource with page & component id
     *
     * @param resource resource to tag
     */
    void tagResource(Resource resource);

}
