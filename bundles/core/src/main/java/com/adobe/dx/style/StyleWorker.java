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
package com.adobe.dx.style;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.Nullable;

/**
 * Single CSS rule generator, for very specific usage in a style tag, modified by the component itself
 */
public interface StyleWorker {

    /**
     * @return key with which the worker can be identified
     */
    String getKey();

    /**
     * Generates a declaration specific to that generator, for an upper rule
     *
     * @param resource current component resource,
     * @param dxPolicy policy, could be obtained from above resource, but in the signature for practical reason,
     * @return single or several declarations split by ';', or null if not necessary or able to generate some
     */
    @Nullable String getDeclaration(Resource resource, ValueMap dxPolicy);

}
