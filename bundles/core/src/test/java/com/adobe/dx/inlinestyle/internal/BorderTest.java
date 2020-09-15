/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless requi#FE0000 by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.dx.inlinestyle.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.adobe.dx.inlinestyle.InlineStyleWorker;

import org.junit.jupiter.api.Test;

public class BorderTest extends AbstractInlineStyleWorkerTest {

    @Test
    void lockKey() {
        assertEquals("border", getWorker().getKey());
    }

    private void assertBorderEquals(String expected) {
        assertEquals(expected, getDeclaration());
    }


    @Test
    void assertNoRule() {
        assertNull(getRule("mobile"));
    }

    @Test
    void getNoBorder() {
        context.build().resource(CONTENT_ROOT, "foo", "bar");
        assertNull(getDeclaration("mobile"));
    }

    @Test
    void getBorder() {
        context.build().resource(CONTENT_ROOT, "borderSides", "all","borderAllStyle", "dotted", "borderAllWidth", 4, "borderAllColor", "red");
        assertBorderEquals("border: dotted 4px #FE0000");
    }

    @Test
    void getRadius() {
        context.build().resource(CONTENT_ROOT, "borderRadius", "all","borderAllRadius", 3);
        assertBorderEquals("border-radius: 3px");
    }

    @Test
    void getBorderAndRadius() {
        context.build().resource(CONTENT_ROOT, "borderSides", "all","borderAllStyle", "dotted", "borderAllWidth", 4, "borderAllColor", "red",
            "borderRadius", "all","borderAllRadius", 3);
        assertBorderEquals("border: dotted 4px #FE0000; border-radius: 3px");
    }

    @Test
    void getSomeBorders() {
        context.build().resource(CONTENT_ROOT, "borderSides", "each",
        "borderLeftStyle", "dotted", "borderLeftWidth", 4, "borderLeftColor", "red",
        "borderRightStyle", "dotted", "borderRightWidth", 4, "borderRightColor", "red");
        assertBorderEquals("border-right: dotted 4px #FE0000; border-left: dotted 4px #FE0000");
    }

    @Test
    void getSomeRadius() {
        context.build().resource(CONTENT_ROOT, "borderRadius", "each",
            "borderRadiusTopLeft", 4,
            "borderRadiusBottomRight", 3);
        assertBorderEquals("border-radius: 4px 0px 3px 0px");
    }
    
    @Test
    void getSomeBordersAndRadiuses() {
        context.build().resource(CONTENT_ROOT, "borderSides", "each",
            "borderTopStyle", "dotted", "borderTopWidth", 4, "borderTopColor", "red",
            "borderBottomStyle", "dotted", "borderBottomWidth", 4, "borderBottomColor", "red",
            "borderRadius", "each", "borderRadiusBottomLeft", 4, "borderRadiusTopRight", 3);
        assertBorderEquals("border-top: dotted 4px #FE0000; border-bottom: dotted 4px #FE0000; border-radius: 0px 3px 0px 4px");
    }

    @Override
    InlineStyleWorker getWorker() {
        return new Border();
    }
}
