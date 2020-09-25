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
package com.adobe.dx.structure.flex;

import com.adobe.dx.domtagging.AttributeService;
import com.adobe.dx.domtagging.IDTagger;
import com.adobe.dx.inlinestyle.InlineStyleService;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FlexModel {

    public static final String PN_MINHEIGHT = "minHeight";
    public static final String PN_MINHEIGHT_VALUE = PN_MINHEIGHT + "Value";
    public static final String PN_MINHEIGHT_TYPE = PN_MINHEIGHT + "Type";

    @SlingObject
    protected SlingHttpServletRequest request;

    @OSGiService
    IDTagger idTagger;

    @OSGiService
    InlineStyleService styleService;

    @OSGiService
    AttributeService attributeService;

    String id;

    String style;

    String additionalClasses;

    Map<String, String> attributes;

    @PostConstruct
    void init() {
        if (idTagger != null) {
            id = idTagger.computeComponentId(request, null);
        }
        if (styleService != null) {
            style = styleService.getInlineStyle(getId(), request);
        }
        if (attributeService != null) {
            attributes = attributeService.getAttributes(request);
            additionalClasses = attributeService.getClassesString(request);
        }
    }

    public String getId() {
        return id;
    }

    public String getStyle() {
        return style;
    }

    public String getAdditionalClasses() {
        return additionalClasses;
    }

    public Map<String, String> getAttributes() { return attributes; }
}
