/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.adobe.dx.content.marketo.servlet;

import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;
import static org.apache.sling.api.servlets.HttpConstants.METHOD_GET;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES;

import com.adobe.dx.content.marketo.service.MarketoFormData;
import com.adobe.dx.content.marketo.service.MarketoService;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = Servlet.class,
    property = {
        SLING_SERVLET_RESOURCE_TYPES + "=dx/components/author/datasource/marketoDataSource",
        SLING_SERVLET_METHODS + "=" + METHOD_GET})
public class MarketoDatasource extends SlingSafeMethodsServlet {

    @SuppressWarnings("squid:S1075")
    private static final String CONTENT_ROOT_PATH = "/content";

    @Reference
    private transient MarketoService marketoService = null;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) {
        List<MarketoFormData> forms = marketoService
            .getMarketoForms(getResourcePath(request.getRequestPathInfo().getSuffix()));
        if (null != forms) {
            ResourceResolver resolver = request.getResourceResolver();
            Iterator<Resource> datasourceIterator = forms.stream().map(form -> generateDsValueMap(form, resolver))
                .iterator();
            request.setAttribute(DataSource.class.getName(), new SimpleDataSource(datasourceIterator));
        }
    }

    private Resource generateDsValueMap(MarketoFormData form, ResourceResolver resolver) {
        ValueMap vm = new ValueMapDecorator(new HashMap<>());
        if (null != form) {
            vm.put("text", form.getName() + " " + form.getLocale());
            vm.put("value", form.getId());
        }
        return new ValueMapResource(resolver, new ResourceMetadata(),
            NT_UNSTRUCTURED, vm);
    }

    private String getResourcePath(String resourceSuffix) {
        return StringUtils.isNotBlank(resourceSuffix) ? resourceSuffix : CONTENT_ROOT_PATH;
    }
}   
