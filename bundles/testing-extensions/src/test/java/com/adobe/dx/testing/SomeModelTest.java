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

package com.adobe.dx.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.adobe.dx.domtagging.AttributeService;
import com.adobe.dx.domtagging.IDTagger;
import com.adobe.dx.inlinestyle.InlineStyleService;
import com.adobe.dx.testing.extensions.WCMModeDisabledContext;
import com.adobe.dx.testing.extensions.WCMModeEditContext;
import com.day.cq.wcm.api.WCMMode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

public class SomeModelTest extends AbstractRequestModelTest {

    @Test
    public void testId() {
        context.build().resource(CONTENT_ROOT, "foo", "bar");
        SomeModel model = getModel(SomeModel.class, CONTENT_ROOT);
        assertNotNull(model.getId());
    }

    @ExtendWith(WCMModeDisabledContext.class)
    @Test
    public void testWCMModeDisabled() {
        assertEquals(WCMMode.DISABLED, WCMMode.fromRequest(context.request()));
    }

    @ExtendWith(WCMModeEditContext.class)
    @Test
    public void testWCMModeEdit() {
        assertEquals(WCMMode.EDIT, WCMMode.fromRequest(context.request()));
    }
}
