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
package com.adobe.dx.admin.authorvh;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.adobe.dx.testing.AbstractTest;
import com.adobe.dx.testing.extensions.ResponsiveContext;
import com.adobe.dx.testing.extensions.WCMModeDisabledContext;
import com.adobe.dx.testing.extensions.WCMModeEditContext;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ResponsiveContext.class)
class AuthorVhTest extends AbstractTest {

    AuthorVh authorvh;

    @BeforeEach
    public void setup() {
        authorvh = new AuthorVh();
        context.registerInjectActivateService(authorvh,
            "propertyMatching", new String[] { "minHeightType:vh:minHeightValue"},
            "itemParents", new String[] { "definitions" });
        context.build().resource(CONTENT_ROOT, "minHeightTypeTablet","vh","minHeightValueTablet", 51L, "sling:resourceType", "dx/some/component");
        context.build().resource(CONTENT_ROOT +"/definitionsMobile")
            .siblingsMode()
            .resource("item0", "minHeight", "custom", "minHeightType", "vh", "minHeightValue", 12L, "width", "custom", "widthCustomValue", 200L, "widthCustomType", "px")
            .resource("item1", "minHeight", "custom", "minHeightValue", 92L, "minHeightType", "px", "width", "auto")
            .resource("item2", "minHeight", "auto", "minHeightType", "px", "width", "100%");
        context.build().resource(CONTENT_ROOT +"/definitionsDesktop")
            .siblingsMode()
            .resource("item0", "minHeight", "auto", "minHeightType", "px", "width", "auto", "widthCustomType", "px", "order", 1L)
            .resource("item1", "minHeight", "custom", "minHeightType", "vh","minHeightValue", 15L, "width", "33.33%", "widthCustomType", "px")
            .resource("item2", "minHeight", "auto", "minHeightType", "px", "width", "33.33%", "widthCustomType", "px");
        context.currentResource(CONTENT_ROOT);
    }

    @Test
    public void lockKey() {
        assertEquals("authorvh", authorvh.getKey());
    }

    @ExtendWith(WCMModeEditContext.class)
    @Test
    public void testGetAttributes() {
        Map<String, String> attributes = authorvh.getAttributes(context.request());
        assertNotNull(attributes);
        assertArrayEquals(new String[]{"data-author-vh-tablet", "data-author-vh-item-mobile", "data-author-vh-item-desktop"},
            attributes.keySet().toArray());
        assertEquals("51", attributes.get("data-author-vh-tablet"));
        assertEquals("12,,", attributes.get("data-author-vh-item-mobile"));
        assertEquals(",15,", attributes.get("data-author-vh-item-desktop"));
    }

    @ExtendWith(WCMModeDisabledContext.class)
    @Test
    public void testGetAttributesOnPublish() {
        assertNull(authorvh.getAttributes(context.request()));
    }
}