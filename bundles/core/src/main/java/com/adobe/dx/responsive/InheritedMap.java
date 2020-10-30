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

package com.adobe.dx.responsive;

public interface InheritedMap {

    /**
     * Get value from current breakpoint, or inherited (if set)
     *
     * @param propertyName name of the property fetched
     * @param breakpoint breakpoint for which we do want the value
     * @param defaultValue default value
     * @param <T> generic type
     * @return value (in given type), or default value
     */
    <T> T getInheritedValue(String propertyName, Breakpoint breakpoint, T defaultValue);

    /**
     * Get value from current breakpoint, or inherited (if set)
     *
     * @param propertyName name of the property fetched
     * @param inheritPropertyName name of the property to look at for inheritance
     * @param breakpoint breakpoint for which we do want the value
     * @param defaultValue default value
     * @param <T> generic type
     * @return value (in given type), or default value
     */
    <T> T getInheritedValue(String propertyName, String inheritPropertyName, Breakpoint breakpoint, T defaultValue);
}
