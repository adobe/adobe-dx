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

import static com.adobe.dx.testing.AbstractTest.CONF_ROOT;
import static com.adobe.dx.testing.AbstractTest.CONTENT_ROOT;
import static com.adobe.dx.testing.extensions.ExtensionsUtil.getContext;

import com.adobe.dx.bindings.internal.DxBindingsValueProvider;
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.responsive.ResponsiveConfiguration;
import com.adobe.dx.responsive.internal.ResponsivePropertiesImpl;
import com.adobe.dx.testing.AbstractTest;

import java.util.Arrays;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.testing.mock.caconfig.MockContextAwareConfig;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import io.wcm.testing.mock.aem.junit5.AemContext;

public class ResponsiveContext implements BeforeEachCallback {
    private static final String PROPERTY_SUFFIX = "propertySuffix";

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        AemContext context = getContext(extensionContext);
        context.build().resource(CONF_ROOT + "/sling:configs/" + ResponsiveConfiguration.class.getName() + "/breakpoints")
            .siblingsMode()
            .resource("1", PROPERTY_SUFFIX, "Mobile", "key", "mobile")
            .resource("2", PROPERTY_SUFFIX, "Tablet",
                "key", "tablet",
                "mediaQuery", "@media screen and (min-width: 600px)",
                "inheritBehaviourProp", "inheritTablet")
            .resource("3", PROPERTY_SUFFIX, "Desktop",
                "key", "desktop",
                "mediaQuery", "@media screen and (min-width: 1200px)",
                "inheritBehaviourProp", "inheritDesktop");
        MockContextAwareConfig.registerAnnotationClasses(context, ResponsiveConfiguration.class);
        MockContextAwareConfig.registerAnnotationClasses(context, Breakpoint.class);
        if (context.resourceResolver().getResource(CONTENT_ROOT) == null) {
            context.create().resource(CONTENT_ROOT);
        }
        context.build().resource(CONTENT_ROOT, "sling:configRef", CONF_ROOT);
        Resource resource = context.resourceResolver().getResource(CONTENT_ROOT);
        if (resource != null) {
            ConfigurationBuilder builder = resource.adaptTo(ConfigurationBuilder.class);
            if (builder != null) {
                ResponsiveConfiguration configuration = builder.as(ResponsiveConfiguration.class);
                SlingBindings bindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
                List<Breakpoint> breakpoints = Arrays.asList(configuration.breakpoints());
                ResponsivePropertiesImpl responsiveProperties = new ResponsivePropertiesImpl(breakpoints, AbstractTest.getVM(context, CONTENT_ROOT));
                bindings.put(DxBindingsValueProvider.RESP_PROPS_KEY, responsiveProperties);
                bindings.put(DxBindingsValueProvider.BP_KEY, breakpoints);
            }
        }
    }
}
