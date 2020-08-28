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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.adobe.dx.inlinestyle.InlineStyleWorker;
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.testing.AbstractInlineStyleWorkerTest;

import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

public class ResponsiveContextTest extends AbstractInlineStyleWorkerTest {

    @Override
    protected InlineStyleWorker getWorker() {
        return WORKER_TO_TEST;
    }

    @Test
    protected void testBasics() {
        assertEquals("checkThis", getWorker().getKey());
        assertEquals("FOO", getDeclaration("mobile"));
        assertEquals("BAR", getRule("BAR", "id"));
    }

    final static InlineStyleWorker WORKER_TO_TEST = new InlineStyleWorker() {
        @Override
        public String getKey() {
            return "checkThis";
        }

        @Override
        public @Nullable String getDeclaration(Breakpoint breakpoint, SlingHttpServletRequest request) {
            return "FOO";
        }

        @Override
        public @Nullable String getRule(@Nullable Breakpoint breakpoint, @Nullable String id,
                                        SlingHttpServletRequest request) {
            return "BAR";
        }
    };
}
