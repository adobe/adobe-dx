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

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(collection = true)
public @interface Breakpoint {
    @Property(label = "name")
    String getLabel();

    @Property(label = "property suffix", description = "suffix to append to a property to get the responsive version of it")
    String propertySuffix();

    @Property(label = "map key", description = "key from where a value will be available")
    String key();

    @Property(label = "media Query", description = "media query that defines this breakpoint")
    String mediaQuery();

    @Property(label = "inheritance behaviour property", description = "name of the property that defines wether "
        + "current breakpoint should 'override' or 'inherit' previous breakpoint")
    String inheritBehaviourProp();

    @Property(label = "start", description = "screen width from which this breakpoint is set")
    int start();

    @Property(label = "end", description = "screen width to which this breakpoint is set")
    int end();
}
