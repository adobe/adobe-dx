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
package com.adobe.dx.policy.internal;

import static com.day.cq.wcm.scripting.WCMBindingsConstants.NAME_CURRENT_CONTENT_POLICY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.adobe.dx.testing.AbstractTest;
import com.day.cq.wcm.api.policies.ContentPolicy;

import javax.script.Bindings;
import javax.script.SimpleBindings;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.junit.jupiter.api.Test;

class DxContentPolicyManagerTest extends AbstractTest {

    ContentPolicy mockContentPolicy(String path) {
        ContentPolicy policy = mock(ContentPolicy.class);
        when(policy.getProperties())
            .thenReturn(context.resourceResolver().getResource(path).getValueMap());
        return policy;
    }

    @Test
    void addBindings() {
        Bindings bindings = new SimpleBindings();
        final String POLICY_PATH = "/blah";
        context.build().resource(POLICY_PATH, "k1", "v12", "k2", "v22");
        context.build().resource(CONTENT_ROOT, "k1", "v11").commit();
        bindings.put(SlingBindings.RESOURCE,  context.resourceResolver().getResource(CONTENT_ROOT));
        bindings.put(NAME_CURRENT_CONTENT_POLICY, mockContentPolicy(POLICY_PATH));
        DxContentPolicyManager mgr = new DxContentPolicyManager();
        mgr.addBindings(bindings);
        ValueMap vm = (ValueMap)bindings.get("dxPolicy");
        assertNotNull(vm);
        assertEquals(2, vm.size(), "there should be 2 items in that VM");
        assertEquals("v11", vm.get("k1", String.class), "first one comes from comp resource");
        assertEquals("v22", vm.get("k2", String.class), "second one comes from policy resource");
    }
}