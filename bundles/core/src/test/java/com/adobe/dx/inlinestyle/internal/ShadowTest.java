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
package com.adobe.dx.inlinestyle.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.adobe.dx.inlinestyle.InlineStyleWorker;

import org.junit.jupiter.api.Test;

class ShadowTest extends AbstractInlineStyleWorkerTest {

    @Override
    InlineStyleWorker getWorker() {
        return new Shadow();
    }

    @Test
    void lockKey() {
        assertEquals("shadow", new Shadow().getKey());
    }

    @Test
    void assertNoRule() {
        assertNull(getRule("mobile"));
    }

    @Test
    void getShadow() {
        context.build().resource(CONTENT_ROOT, "shadowColor", "blue",
            "shadowOffsetX", 9L,
            "shadowOffsetY", 10L,
            "shadowBlur", 11L,
            "shadowSpread", 12L);
        assertEquals("box-shadow: 9px 10px 11px 12px #0000FE", getDeclaration());
    }

    @Test
    void testInset() {
        context.build().resource(CONTENT_ROOT, "shadowColor", "red",
            "shadowInset", true);
        assertEquals("box-shadow: 0px 0px 0px 0px #FE0000 inset", getDeclaration());
    }
}