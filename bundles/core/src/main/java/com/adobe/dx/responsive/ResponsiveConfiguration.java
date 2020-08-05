package com.adobe.dx.responsive;

import org.apache.sling.caconfig.annotation.Configuration;

@Configuration(label="Responsive Configuration", description = "sets the responsive behaviour")
public @interface ResponsiveConfiguration {

    String[] breakpoints() default {"Mobile", "Tablet", "Desktop"};
}
