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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.adobe.dx.domtagging.IDTagger;
import com.adobe.dx.structure.AbstractStructureModelTest;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

import java.util.List;

import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FlexModelTest extends AbstractStructureModelTest {

    FlexModel model;

    @BeforeEach
    public void setup()  throws ReflectiveOperationException {
        context.build().resource(MODEL_PATH,
            "sling:resourceType", "dx/structure/components/flex",
            "title", "dx flex component")
            .resource("definitionsMobile")
            .siblingsMode()
            .resource("1", "minHeight", "custom")
            .resource("2", "minHeight", "custom");
        context.currentResource(MODEL_PATH);
        context.contentPolicyMapping("dx/structure/components/flex", "blah", "blah");
        ContentPolicy policy = context.resourceResolver()
            .adaptTo(ContentPolicyManager.class).getPolicy(context.currentResource());
        context.build().resource(policy.getPath() + "/definitionsTablet/items0",
            "minHeight", "custom");
        model = getModel(FlexModel.class, MODEL_PATH);
    }

    @Test
    public void testWorkingId() {
        model.idTagger = mock(IDTagger.class);
        when(model.idTagger.computeComponentId(any(), any())).thenReturn("blah");
        assertEquals("blah", model.getId());
    }

    @Test
    public void testNonWorkingId() {
        assertNull(model.getId());
    }

    @Test
    public void testIsNeeded() {
        assertTrue(model.isStyleNeeded());
    }

    @Test
    public void testGetDefinitions() {
        List<ValueMap> mobile = model.getDefinitions("mobile");
        List<ValueMap> tablet = model.getDefinitions("tablet");
        assertNotNull(mobile);
        assertNotNull(tablet);
        assertNull(model.getDefinitions("desktop"));
        assertEquals(2, mobile.size());
        assertEquals(1, tablet.size());
        assertEquals("custom", mobile.get(0).get("minHeight"));
    }
}