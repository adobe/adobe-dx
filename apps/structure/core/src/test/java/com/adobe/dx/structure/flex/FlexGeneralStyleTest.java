package com.adobe.dx.structure.flex;

import static org.junit.jupiter.api.Assertions.*;

import com.adobe.dx.inlinestyle.InlineStyleWorker;
import com.adobe.dx.testing.AbstractInlineStyleWorkerTest;

import org.junit.jupiter.api.Test;

class FlexGeneralStyleTest extends AbstractInlineStyleWorkerTest {

    @Test
    public void testRule() {
        context.build().resource(CONTENT_ROOT, "minHeightValue",
            20L, "minHeightType", "px",
            "gap", 30L);
        assertEquals("#this-is-my-flex > .dx-flex-items {\n"
            + "min-height: 20px; margin: -15px\n"
            + "}\n"
            + "#this-is-my-flex > .dx-flex-items > * {\n"
            + "border: 0 solid transparent; border-width: 15px\n"
            + "}", getRule("mobile", "this-is-my-flex"));
    }

    @Test
    public void testRuleNoGap() {
        context.build().resource(CONTENT_ROOT, "minHeightValue",
            30L, "minHeightType", "%");
        assertEquals("#this-is-my-flex > .dx-flex-items {\n"
            + "min-height: 30%\n"
            + "}", getRule("mobile", "this-is-my-flex"));
    }

    @Test
    public void testNothing() {
        context.build().resource(CONTENT_ROOT, "minHeightValue",
            20L, "minHeightType", "px",
            "gap", 30L);
        assertNull(getRule("tablet", "this-is-my-flex"));
    }

    @Override
    protected InlineStyleWorker getWorker() {
        return new FlexGeneralStyle();
    }
}