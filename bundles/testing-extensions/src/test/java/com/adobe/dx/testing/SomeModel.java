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

import com.adobe.dx.domtagging.AttributeService;
import com.adobe.dx.domtagging.IDTagger;
import com.adobe.dx.inlinestyle.InlineStyleService;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

@Model(adaptables = SlingHttpServletRequest.class)
public class SomeModel {

    @SlingObject
    SlingHttpServletRequest request;

    @OSGiService
    IDTagger idTagger;

    @OSGiService
    AttributeService attributeService;

    @OSGiService
    InlineStyleService inlineStyleService;

    String id;

    String classes;

    String style;

    @PostConstruct
    void init() {
        id = idTagger.computeComponentId(request, "idProperty");
        classes = attributeService.getClassesString(request);
        style = inlineStyleService.getInlineStyle(id, request);
    }

    String getId() {
        return id;
    }
}
