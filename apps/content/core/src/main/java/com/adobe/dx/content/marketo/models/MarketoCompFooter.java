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

package com.adobe.dx.content.marketo.models;

import com.adobe.dx.content.marketo.models.internal.MarketoConfBasicInfo;
import com.adobe.dx.content.marketo.service.CloudConfigReader;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = SlingHttpServletRequest.class, cache = true)
public class MarketoCompFooter {

    @OSGiService
    private CloudConfigReader cloudConfigReader = null;

    @Self
    private SlingHttpServletRequest request = null;

    private static final String CONFIG_NAME = "marketo-config";

    private MarketoConfBasicInfo marketoConfBasicInfo;

    // TODO Fetch Authored Marketo Components on page and add those list of ids
    // For now, just a test sample. Also, note that the component type may have to be configurable - and not hardcoded
    private List<String> marketoFormIds = Arrays.asList("1005", "1001");

    @PostConstruct
    private void init() {
        marketoConfBasicInfo = cloudConfigReader.getContextAwareCloudConfigRes(
            request.getRequestPathInfo().getResourcePath(), CONFIG_NAME, MarketoConfBasicInfo.class);
        this.request = null;
    }

    public List<String> getMarketoFormIds() {
        return marketoFormIds;
    }

    public MarketoConfBasicInfo getMarketoConfBasicInfo() {
        return marketoConfBasicInfo;
    }

}
