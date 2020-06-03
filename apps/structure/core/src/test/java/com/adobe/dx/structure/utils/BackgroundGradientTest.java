package com.adobe.dx.structure.utils;


import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import com.adobe.dx.structure.utils.BackgroundGradient;
import com.adobe.dx.testing.AbstractRequestModelTest;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.apache.sling.caconfig.resource.impl.ConfigurationResourceResolverImpl;
import org.apache.sling.caconfig.resource.impl.def.DefaultConfigurationResourceResolvingStrategy;
import org.apache.sling.caconfig.resource.impl.def.DefaultContextPathStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BackgroundGradientTest extends AbstractRequestModelTest {

    private BackgroundGradient backgroundGradient;
    private ConfigurationResourceResolver confResourceResolver;

    @BeforeEach
    private void setup() {
        context.load().json("/mocks/gradients/ca-gradients.json", "/conf");
        context.load().json("/mocks/gradients/gradients-app.json", "/apps");
        context.addModelsForClasses(BackgroundGradient.class);

        ResourceResolver resolver = context.resourceResolver();

        Resource resource = resolver.getResource("/apps/component/style");
        context.request().setResource(resource);

        backgroundGradient = resource.adaptTo(BackgroundGradient.class);

        try {
            confResourceResolver = registerConfigurationResourceResolver();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getGradients() {
        String fadeToBlackGradient = backgroundGradient.getGradient("FadeToBlack");
        assertTrue(fadeToBlackGradient.equals("linear-gradient(180deg, rgba(0, 0, 0, 0.5) 50.0%,rgba(0, 0, 0, 1) 95.0%)"));

        String adobeRedGradient = backgroundGradient.getGradient("AdobeRed");
        assertTrue(adobeRedGradient.equals("linear-gradient(180deg, rgba(0, 0, 0, 0.5) 36.8%,rgba(255, 0, 0, 0.78) 95.0%)"));

        String rainbowBarsGradient = backgroundGradient.getGradient("RainbowBars");
        assertTrue(rainbowBarsGradient.equals("linear-gradient(90deg, rgba(255, 0, 0, 0.41) 20.0%,rgba(255, 165, 0, 0.41) 20.0%,rgba(255, 165, 0, 0.41) 40.0%,rgba(255, 255, 0, 0.41) 40.0%,rgba(255, 255, 0, 0.41) 60.0%,rgba(0, 128, 0, 0.41) 60.0%,rgba(0, 128, 0, 0.41) 80.0%,rgba(0, 0, 255, 0.4) 80.0%)"));
    }

    @Test
    public void getInvalidGradient() {
        String notAGradient = backgroundGradient.getGradient("notAGradient");
        assertNull(notAGradient);
    }


    private ConfigurationResourceResolver registerConfigurationResourceResolver() throws IOException {
        context.registerInjectActivateService(new DefaultContextPathStrategy());
        context.registerInjectActivateService(new DefaultConfigurationResourceResolvingStrategy());
        return context.registerInjectActivateService(new ConfigurationResourceResolverImpl());
    }
}
