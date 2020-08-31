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
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.responsive.ResponsiveConfiguration;
import com.adobe.dx.testing.AbstractRequestModelTest;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

import java.util.List;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.CompositeValueMap;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.testing.mock.caconfig.MockContextAwareConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FlexModelTest extends AbstractRequestModelTest {

    FlexModel model;

    @BeforeEach
    public void setup()  throws ReflectiveOperationException {
        context.build().resource(CONF_ROOT + "/sling:configs/" + ResponsiveConfiguration.class.getName() + "/breakpoints")
            .siblingsMode()
            .resource("1","propertySuffix", "Mobile", "key", "mobile")
            .resource("2", "propertySuffix", "Tablet", "key", "tablet")
            .resource("3", "propertySuffix", "Desktop", "key", "desktop");
        MockContextAwareConfig.registerAnnotationClasses(context, ResponsiveConfiguration.class);
        MockContextAwareConfig.registerAnnotationClasses(context, Breakpoint.class);
        context.create().resource(CONTENT_ROOT, "sling:configRef", CONF_ROOT);
        ResponsiveConfiguration configuration =  context.resourceResolver()
            .getResource(CONTENT_ROOT)
            .adaptTo(ConfigurationBuilder.class)
            .as(ResponsiveConfiguration.class);
        context.build().resource(CONTENT_ROOT,
            "sling:resourceType", "dx/structure/components/flex",
            "title", "dx flex component")
            .resource("definitionsMobile")
            .siblingsMode()
            .resource("1", "minHeight", "custom")
            .resource("2", "minHeight", "custom");
        context.currentResource(CONTENT_ROOT);
        context.contentPolicyMapping("dx/structure/components/flex", "blah", "blah");
        ContentPolicy policy = context.resourceResolver()
            .adaptTo(ContentPolicyManager.class).getPolicy(context.currentResource());
        context.build().resource(policy.getPath() + "/definitionsTablet/items0",
            "minHeight", "custom");
        model = getModel(FlexModel.class, CONTENT_ROOT);
        model.breakpoints = configuration.breakpoints();
        model.init();
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