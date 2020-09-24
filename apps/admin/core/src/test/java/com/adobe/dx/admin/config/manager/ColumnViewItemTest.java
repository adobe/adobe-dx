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
package com.adobe.dx.admin.config.manager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.adobe.dx.testing.AbstractTest;
import com.adobe.dx.testing.extensions.ResponsiveContext;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.caconfig.spi.metadata.ConfigurationMetadata;
import org.apache.sling.caconfig.spi.metadata.PropertyMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ResponsiveContext.class)
class ColumnViewItemTest extends AbstractTest {

    @Test
    void getMetadataChildren() {
        PropertyMetadata bp = new PropertyMetadata("breakpoints", String.class);
        List<PropertyMetadata<?>> list = new ArrayList<>();
        list.add(bp);
        ColumnViewItem item = new ColumnViewItem();
        item.resource = context.currentResource(CONTENT_ROOT);
        item.metadata = new ConfigurationMetadata(ResponsiveContext.class.getName(), list, false);
        List<ColumnViewItem> children = item.getChildren();
        assertNotNull(children);
    }
}