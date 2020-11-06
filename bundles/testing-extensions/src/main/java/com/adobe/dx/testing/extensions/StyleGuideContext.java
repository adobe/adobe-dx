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

import static com.adobe.dx.testing.AbstractTest.CONFIG_ROOTS;
import static com.adobe.dx.testing.AbstractTest.CONF_ROOT;

import com.adobe.dx.styleguide.StyleGuide;
import com.adobe.dx.styleguide.StyleGuideItem;

import org.apache.sling.testing.mock.caconfig.MockContextAwareConfig;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import io.wcm.testing.mock.aem.junit5.AemContext;

public class StyleGuideContext implements BeforeEachCallback {
    private static final String K = "key";
    private static final String V = "value";
    private static final String L = "label";
    
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        String confRoot = CONFIG_ROOTS + "/" + StyleGuide.class.getName() + "/";
        AemContext context = ExtensionsUtil.getContext(extensionContext);
        context.build().resource(confRoot + "colors")
            .siblingsMode()
            .resource("1",K, "red", V, "#FE0000")
            .resource("2", K, "white", V, "#FEFEFE")
            .resource("3", K, "blue", V, "#0000FE");
        context.build().resource(confRoot + "gradients")
            .siblingsMode()
            .resource("1",L, "Adobe Red", K, "red", V, "linear-gradient(180deg, rgba(0, 0, 0, 0.5) 36.8%,rgba(255, 0, 0, 0.78) 95.0%)")
            .resource("2", L, "Fade to black", K, "wb", V, "linear-gradient(180deg, rgba(0, 0, 0, 0.5) 50.0%,rgba(0, 0, 0, 1) 95.0%)")
            .resource("3", L, "Rainbow bars", K, "rainbow", V, "linear-gradient(90deg, rgba(255, 0, 0, 0.41) 20.0%,rgba(255, 165, 0, 0.41) 20.0%,rgba(255, 165, 0, 0.41) 40.0%,rgba(255, 255, 0, 0.41) 40.0%,rgba(255, 255, 0, 0.41) 60.0%,rgba(0, 128, 0, 0.41) 60.0%,rgba(0, 128, 0, 0.41) 80.0%,rgba(0, 0, 255, 0.4) 80.0%)");
        MockContextAwareConfig.registerAnnotationClasses(context, StyleGuide.class);
        MockContextAwareConfig.registerAnnotationClasses(context, StyleGuideItem.class);
    }
}
