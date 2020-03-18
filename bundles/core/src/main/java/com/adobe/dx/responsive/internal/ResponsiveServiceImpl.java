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
package com.adobe.dx.responsive.internal;

import com.adobe.dx.responsive.ResponsiveService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(
    immediate = true,
    configurationPolicy = ConfigurationPolicy.REQUIRE
)
@Designate(
    ocd= ResponsiveServiceImpl.Configuration.class
)
public class ResponsiveServiceImpl implements ResponsiveService {

    Configuration configuration;

    @Override
    public String[] getBreakpoints() {
        return configuration.breakpoints();
    }

    @Activate
    @Modified
    public void activate(Configuration config) {
        configuration = config;
    }

    @ObjectClassDefinition(name = "Adobe DX Responsive Settings")
    public @interface Configuration {

        /**
         * Site Locale Key.
         *
         * @return unique key that identifies this config.
         */
        @AttributeDefinition(
            name = "Site Locale Key",
            description = "A unique key that identifies this config. The key is also used as a "
                + "placeholder key."
        )
        @SuppressWarnings("squid:S00100")
        String[] breakpoints() default {"Mobile", "Tablet", "Desktop"};
    }
}
