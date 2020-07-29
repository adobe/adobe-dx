package com.adobe.dx.structure.utils;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import com.adobe.dx.testing.AbstractRequestModelTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BackgroundGradientTest extends AbstractRequestModelTest {

    private BackgroundGradient backgroundGradient;

    @BeforeEach
    private void setup() throws ReflectiveOperationException {
        context.load().json("/mocks/gradients/ca-gradients.json", "/conf");
        context.load().json("/mocks/gradients/gradients-app.json", "/apps");
        context.addModelsForClasses(BackgroundGradient.class);

        backgroundGradient = getModel(BackgroundGradient.class, "/apps/component/style");
    }

    @Test
    public void getGradients() {
        assertNull(backgroundGradient.getGradient());

        assertEquals("linear-gradient(180deg, rgba(0, 0, 0, 0.5) 50.0%,rgba(0, 0, 0, 1) 95.0%)",
            backgroundGradient.getGradient("FadeToBlack"));

        assertEquals("linear-gradient(180deg, rgba(0, 0, 0, 0.5) 36.8%,rgba(255, 0, 0, 0.78) 95.0%)",
            backgroundGradient.getGradient("AdobeRed"));

        assertEquals("linear-gradient(90deg, rgba(255, 0, 0, 0.41) 20.0%,rgba(255, 165, 0, 0.41) 20.0%,rgba(255, 165, 0, 0.41) 40.0%,rgba(255, 255, 0, 0.41) 40.0%,rgba(255, 255, 0, 0.41) 60.0%,rgba(0, 128, 0, 0.41) 60.0%,rgba(0, 128, 0, 0.41) 80.0%,rgba(0, 0, 255, 0.4) 80.0%)",
            backgroundGradient.getGradient("RainbowBars"));
    }

    @Test
    public void getInvalidGradient() {
        String notAGradient = backgroundGradient.getGradient("notAGradient");
        assertNull(notAGradient);
    }
}
