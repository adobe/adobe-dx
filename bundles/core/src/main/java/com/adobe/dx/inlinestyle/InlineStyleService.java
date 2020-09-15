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
package com.adobe.dx.inlinestyle;

import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.Nullable;

public interface InlineStyleService {

    /**
     * Computes a list of CSS declarations relatives of the given request. If an id is provided,
     * encapsulate those declarations in that id rule (for nested usage).
     *
     * @param id optional ID to encapsulate declarations with
     * @param request current request
     * @return declaration set, or local rule
     */
    String getInlineStyle(@Nullable String id, SlingHttpServletRequest request);
}
