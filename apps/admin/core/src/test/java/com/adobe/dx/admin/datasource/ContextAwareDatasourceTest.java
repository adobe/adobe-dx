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

package com.adobe.dx.admin.datasource;

import com.adobe.dx.admin.datasource.internal.ContextAwareDatasource;
import com.adobe.dx.testing.AbstractTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adobe.granite.ui.components.ds.SimpleDataSource;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wcm.testing.mock.aem.junit5.AemContext;

public class ContextAwareDatasourceTest extends AbstractTest {

    private static final String DATASOURCE_CLASS = "com.adobe.granite.ui.components.ds.DataSource";

    @BeforeEach
    public void setUp() {
        context.load().json("/mocks/datasource/ca-content.json", "/content");
        context.load().json("/mocks/datasource/ca-conf.json", "/conf");
        context.load().json("/mocks/datasource/ca-app.json", "/apps");
        context.addModelsForClasses(ContextAwareDatasource.class);

        ResourceResolver resolver = context.resourceResolver();

        Resource component = resolver.getResource("/apps/component/style");
        context.request().setResource(component);
    }

    @Test
    public void getCaDsFromDialog() {
        context.requestPathInfo().setSuffix("/content/dexter/jcr:content");
        SimpleDataSource ds = getDataSource(context);
        assertTrue(ds instanceof SimpleDataSource);

        Iterator<Resource> dsResources = ds.iterator();
        while(dsResources.hasNext()) {
            Resource style = dsResources.next();
            String text = style.getValueMap().get("text", String.class);
            assertEquals("Red", text);
        }
    }

    @Test
    public void getCaDsFromPage() {
        context.request().setParameterMap(new HashMap<String, Object>(){
            private static final long serialVersionUID = 1L;
            { put("item", "/content/dexter"); }
        });
        SimpleDataSource ds = getDataSource(context);
        assertTrue(ds instanceof SimpleDataSource);
        Iterator<Resource> dsResources = ds.iterator();
        while(dsResources.hasNext()) {
            Resource style = dsResources.next();
            String text = style.getValueMap().get("text", String.class);
            assertEquals("Red", text);
        }
    }

    @Test
    public void getCaDsFromDialogUsingBadPath() {
        context.requestPathInfo().setSuffix("/content/dexter/jcr:content/foo");
        SimpleDataSource ds = getDataSource(context);
        assertTrue(ds instanceof SimpleDataSource);
        Iterator<Resource> dsResources = ds.iterator();
        while(dsResources.hasNext()) {
            Resource style = dsResources.next();
            String text = style.getValueMap().get("text", String.class);
            assertEquals("Red", text);
        }
    }

    @Test
    public void getCaDsFromPolicy() {
        context.requestPathInfo().setSuffix("/conf/dexter/settings/wcm/policies/wcm/foundation/components/fake-policy");
        SimpleDataSource ds = getDataSource(context);
        assertTrue(ds instanceof SimpleDataSource);
        Iterator<Resource> dsResources = ds.iterator();
        while(dsResources.hasNext()) {
            Resource style = dsResources.next();
            String text = style.getValueMap().get("text", String.class);
            assertEquals("Red", text);
        }
    }

    private SimpleDataSource getDataSource(AemContext context) {
        SlingHttpServletRequest request = context.request();
        request.adaptTo(ContextAwareDatasource.class);
        return (SimpleDataSource) context.request().getAttribute(DATASOURCE_CLASS);
    }
}
