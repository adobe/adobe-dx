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

import com.adobe.dx.responsive.Breakpoint;

import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.Nullable;

/**
 * Single CSS declaration or rule generator,
 * for very specific usage in a style tag or attribute,
 * fed in the component context
 */
public interface InlineStyleWorker {

    /**
     * @return key with which the worker can be identified
     */
    String getKey();

    /**
     * Generates a declaration specific to that generator
     *
     * @param breakpoint breakpoint corresponding to that declaration
     * @param request current request
     * @return single or several declarations split by ';', or null if not necessary or able to generate some
     */
    @Nullable String getDeclaration(Breakpoint breakpoint, SlingHttpServletRequest request);

    /**
     * Generates a rule specific to that generator
     *
     * @param breakpoint if rule is specific to it, null otherwise
     * @param id rules can reuse in format <code>${id}</code> current
     * @param request current request
     * @return one or several rules
     */
    @Nullable String getRule(@Nullable Breakpoint breakpoint, @Nullable String id, SlingHttpServletRequest request);
}
