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
package com.adobe.dx.structure.flex;

import static org.junit.jupiter.api.Assertions.*;

import com.adobe.dx.inlinestyle.InlineStyleWorker;
import com.adobe.dx.testing.AbstractInlineStyleWorkerTest;

import org.junit.jupiter.api.Test;

class FlexItemsDefinitionsTest  extends AbstractInlineStyleWorkerTest {

    @Override
    protected InlineStyleWorker getWorker() {
        return new FlexItemsDefinitions();
    }

    @Test
    public void lockKey() {
        assertEquals("flex-definitions", getWorker().getKey());
    }

    @Test
    protected void getDefinition() {
        assertNull(getDeclaration("mobile"));
    }

    @Test
    protected void getRule(){
        context.build().resource(CONTENT_ROOT, "justification", "someJustification", "inheritDesktop", "override", "sling:resourceType", "dx/components/structure/flex");
        context.build().resource(CONTENT_ROOT +"/definitions")
                .siblingsMode()
                    .resource("item0", "minHeight", "auto", "minHeightType", "px", "width", "custom",
                        "widthCustomValue", 200L, "widthCustomType", "px")
                    .resource("item1", "minHeight", "custom", "minHeightValue", 92L, "minHeightType", "px", "width", "auto")
                    .resource("item2", "minHeight", "auto", "minHeightType", "px", "width", "100%");
        context.contentPolicyMapping("dx/components/structure/flex","definitionsTablet/item0/minHeight", "auto");
        context.build().resource(CONTENT_ROOT +"/definitionsDesktop")
                .siblingsMode()
                    .resource("item0", "minHeight", "auto", "minHeightType", "px", "width", "auto", "widthCustomType", "px", "order", 1L)
                    .resource("item1", "minHeight", "auto", "minHeightType", "px", "width", "33.33%", "widthCustomType", "px")
                    .resource("item2", "minHeight", "auto", "minHeightType", "px", "width", "33.33%", "widthCustomType", "px");
        context.currentResource(CONTENT_ROOT);
        assertNull(getRule("tablet", "a-flex"));
        assertEquals("#a-flex > .dx-Flex-items > *:nth-child(1) {\n"
            + "width: 200px; max-width: 200px; min-height: auto\n"
            + "}\n"
            + "#a-flex > .dx-Flex-items > *:nth-child(2) {\n"
            + "flex: 0 0 auto; max-width: 100%; width: auto; min-height: 92px\n"
            + "}\n"
            + "#a-flex > .dx-Flex-items > *:nth-child(3) {\n"
            + "width: 100%; max-width: 100%; flex: 1 1 auto; min-height: auto\n"
            + "}", getRule("mobile","a-flex"));
        assertEquals("#a-flex > .dx-Flex-items > *:nth-child(1) {\n"
            + "flex: 1 1 1%; max-width: 100%; min-height: auto; order: 1\n"
            + "}\n"
            + "#a-flex > .dx-Flex-items > *:nth-child(2) {\n"
            + "width: 33.33%; max-width: 33.33%; flex: 1 1 auto; min-height: auto\n"
            + "}\n"
            + "#a-flex > .dx-Flex-items > *:nth-child(3) {\n"
            + "width: 33.33%; max-width: 33.33%; flex: 1 1 auto; min-height: auto\n"
            + "}", getRule("desktop","a-flex"));
    }
}