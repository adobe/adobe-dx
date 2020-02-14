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
package com.adobe.dx.admin.config.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.adobe.dx.admin.config.manager.ColumnViewDataSource;
import com.adobe.dx.admin.config.manager.ColumnViewItem;
import static com.adobe.dx.admin.config.manager.Constants.BAD_LIST;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

@Model(adaptables = { SlingHttpServletRequest.class }, 
       adapters = { ColumnViewDataSource.class },
       resourceType = "dx/config-manager/common/components/content")
@Exporter(name = "jackson", extensions = "json")
public class ColumnViewDataSourceImpl implements ColumnViewDataSource {

    @SlingObject(injectionStrategy = InjectionStrategy.REQUIRED)
    private ResourceResolver resourceResolver;

    @Override
    public List<ColumnViewItem> getItems() {
        List<ColumnViewItem> children = new ArrayList<>();
        Resource conf = resourceResolver.getResource("/conf");
        if (conf != null) {
            Iterable<Resource> childrenIter = conf.getChildren();
            childrenIter.forEach(child -> {
                boolean badListed = Arrays.stream(BAD_LIST).anyMatch(child.getName()::equals);
                if (!badListed) {
                    ColumnViewItem item = child.adaptTo(ColumnViewItem.class);
                    children.add(item);
                }
            });
            return children;
        }
        return null;
    }

}