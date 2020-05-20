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
package com.adobe.dx.admin.config.manager.internal;

import java.util.Collections;
import java.util.List;

import com.adobe.dx.admin.config.manager.ColumnViewDataSource;
import com.adobe.dx.admin.config.manager.ColumnViewItem;
import static com.adobe.dx.admin.config.manager.Constants.CONF_ROOT;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

@Model(adaptables = { SlingHttpServletRequest.class }, 
       adapters = { ColumnViewDataSource.class },
       resourceType = "dx/config-manager/components/content")
@Exporter(name = "jackson", extensions = "json")
public class ColumnViewDataSourceImpl implements ColumnViewDataSource {

    @SlingObject(injectionStrategy = InjectionStrategy.REQUIRED)
    private ResourceResolver resourceResolver;

    @Override
    public List<ColumnViewItem> getItems() {
        Resource conf = resourceResolver.getResource(CONF_ROOT);
        if (conf != null) {
            ColumnViewItem item = conf.adaptTo(ColumnViewItem.class);
            if (item != null) {
                return item.getChildren();
            }
        }
        return Collections.emptyList();
    }

}