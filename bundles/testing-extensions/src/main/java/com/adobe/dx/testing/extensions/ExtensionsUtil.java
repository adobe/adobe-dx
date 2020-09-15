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
package com.adobe.dx.testing.extensions;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.extension.ExtensionContext;

import io.wcm.testing.mock.aem.junit5.AemContext;

public class ExtensionsUtil {
    private static void putAllFields(Map<String, Field> fields, Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            fields.put(field.getName(), field);
        }
        if (type.getSuperclass() != null) {
            putAllFields(fields, type.getSuperclass());
        }
    }

    /**
     * @param extensionContext current extension context
     * @return AemContext instance, given the fact current test has or inherits from a class that has
     * a AEMContext instance as a <code>context</code> public field
     * @throws IllegalAccessException
     */
    static AemContext getContext(ExtensionContext extensionContext) throws IllegalAccessException {
        Map<String, Field> map = new HashMap<>();
        putAllFields(map, extensionContext.getTestClass().get());
        return (AemContext)map.get("context").get(extensionContext.getTestInstance().get());
    }

}
