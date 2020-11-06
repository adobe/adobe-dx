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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.apache.sling.testing.mock.caconfig.ContextPlugins.CACONFIG;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class AbstractTest {

    public static final String CONTENT_ROOT = "/content/foo";
    public static final String CONF_ROOT = "/conf/foo";
    public static final String CONFIG_ROOTS = CONF_ROOT + "/sling:configs";

    public AemContext context = buildContext(getType());

    protected ResourceResolverType getType() {
        return ResourceResolverType.RESOURCERESOLVER_MOCK;
    }

    protected static AemContext buildContext(ResourceResolverType type) {
        return new AemContextBuilder(type)
            .plugin(CACONFIG)
            .build();
    }

    public static void initContentRoots(AemContext context) {
        if (context.resourceResolver().getResource(CONFIG_ROOTS) == null) {
            context.create().resource(CONF_ROOT + "/sling:configs");
        }
        if (context.resourceResolver().getResource(CONTENT_ROOT) == null) {
            context.create().resource(CONTENT_ROOT);
        }
        context.build().resource(CONTENT_ROOT, "sling:configRef", CONF_ROOT);
    }

    public static ValueMap getVM(AemContext context, String path) {
        Resource resource = context.resourceResolver().getResource(path);
        if (resource != null) {
            return resource.getValueMap();
        }
        return null;
    }

    protected ValueMap getVM(String path) {
        return getVM(context, path);
    }
}