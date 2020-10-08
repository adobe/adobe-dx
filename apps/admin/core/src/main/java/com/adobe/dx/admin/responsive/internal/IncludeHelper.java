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
package com.adobe.dx.admin.responsive.internal;

import com.adobe.granite.ui.components.htl.ComponentHelper;

import java.io.IOException;

import javax.script.Bindings;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;

public class IncludeHelper extends ComponentHelper {

    SlingHttpServletRequest request;
    Resource targetResource;

    IncludeHelper (SlingHttpServletRequest request, Resource targetResource) {
        this.request = request;
        this.targetResource = targetResource;
        init((Bindings)request.getAttribute(SlingBindings.class.getName()));
    }

    @Override
    protected void activate() {
    }

    String include() throws ServletException, IOException {
        return super.include(targetResource, targetResource.getResourceType(), StringUtils.EMPTY, getOptions());
    }
}