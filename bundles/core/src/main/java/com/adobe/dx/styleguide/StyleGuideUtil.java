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

package com.adobe.dx.styleguide;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.caconfig.ConfigurationBuilder;

public class StyleGuideUtil {
    private StyleGuideUtil() {
    }

    private static StyleGuideItem get(StyleGuideItem[] items, String key) {
        if (items != null && StringUtils.isNotBlank(key)) {
            for (StyleGuideItem item : items) {
                if (key.equals(item.key())) {
                    return item;
                }
            }
        }
        return null;
    }

    private static String getValue(StyleGuideItem[] items, String key) {
        StyleGuideItem item = get(items, key);
        if (item != null) {
            return item.value();
        }
        return null;
    }

    private static StyleGuide getGuide(SlingHttpServletRequest request) {
        return request.getResource().adaptTo(ConfigurationBuilder.class).as(StyleGuide.class);
    }

    public static String getColor(SlingHttpServletRequest request, String key) {
        return getValue(getGuide(request).colors(), key);
    }

    public static String getGradient(SlingHttpServletRequest request,  String key) {
        return getValue(getGuide(request).gradients(), key);
    }
}
