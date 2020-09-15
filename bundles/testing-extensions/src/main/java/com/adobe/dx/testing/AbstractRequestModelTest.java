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
package com.adobe.dx.testing;

import com.adobe.dx.testing.extensions.InlineStyleContext;
import com.adobe.dx.testing.extensions.ResponsiveContext;
import com.adobe.dx.testing.extensions.StyleGuideContext;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(ResponsiveContext.class)
@ExtendWith(StyleGuideContext.class)
@ExtendWith(InlineStyleContext.class)
public class AbstractRequestModelTest extends AbstractTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRequestModelTest.class);

    protected <T> T getModel(final Class<T> type) {
        try {
            Resource resource = context.currentResource();
            if (resource != null) {
                context.addModelsForClasses(type);
                return context.getService(ModelFactory.class).createModel(context.request(), type);
            }
            return type.getDeclaredConstructor().newInstance();
        } catch(ReflectiveOperationException e) {
            LOGGER.error("unable to fetch model", e);
        }
        return null;
    }

    protected <T> T getModel(final Class<T> type, String path) {
        context.currentResource(path);
        return getModel(type);
    }

}