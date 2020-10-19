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

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class TypeFilter implements Function<Resource, Boolean> {

    Collection<Pattern> acceptedTypes;

    public TypeFilter(String [] typePatterns) {
        acceptedTypes = new ArrayList<>();
        for (String regexp : typePatterns) {
            acceptedTypes.add(Pattern.compile(regexp));
        }
    }

    @Override
    public Boolean apply(Resource resource) {
        if (StringUtils.isNotBlank(resource.getResourceType())) {
            for (Pattern pattern: acceptedTypes) {
                if (pattern.matcher(resource.getResourceType()).matches()) {
                    return true;
                }
            }
        }
        return false;
    }
}
