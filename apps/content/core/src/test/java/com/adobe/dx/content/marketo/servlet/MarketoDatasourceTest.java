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

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adobe.dx.content.marketo.mocks.service.MockMarketoFormData;
import com.adobe.dx.content.marketo.service.MarketoFormData;
import com.adobe.dx.content.marketo.service.MarketoService;
import com.adobe.dx.testing.AbstractTest;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.servlethelpers.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MarketoDatasourceTest extends AbstractTest {

    private static final String MARKETO_COMPONENT = "/apps/dx/components/marketo";

    private MarketoDatasource marketoDatasource = new MarketoDatasource();

    private MockSlingHttpServletResponse response;

    private MockSlingHttpServletRequest request;

    @BeforeEach
    private void setup() {
        context.load().json("/mocks/marketo/simplepage.json", MARKETO_COMPONENT);
        context.currentResource( MARKETO_COMPONENT);
        context.registerService(MarketoService.class, new MockMarketoService());
        request = context.request();
        response = context.response();
        context.registerInjectActivateService(marketoDatasource);
    }

    @Test
    void testWhenNoRequestSuffixFallBack() {
        updateRequestAndActivate(EMPTY);
        SimpleDataSource ds = (SimpleDataSource) request.getAttribute(DataSource.class.getName());
        validateDatasource(ds, Collections.singletonList("globalForm en_US"), Collections.singletonList("0"));
    }

    @Test
    void testWhenServiceReturnsNullFormData() {
        updateRequestAndActivate("non-existing");
        assertNull(request.getAttribute(DataSource.class.getName()));
    }

    @Test
    void testWhenServiceReturnsEmptyFormData() {
        updateRequestAndActivate("/some-random-path");
        SimpleDataSource ds = (SimpleDataSource) request.getAttribute(DataSource.class.getName());
        assertFalse(ds.iterator().hasNext());
    }

    @Test
    void testWithCorrectFormData() {
        updateRequestAndActivate("/content/dx/us/en/goodPage");
        SimpleDataSource ds = (SimpleDataSource) request.getAttribute(DataSource.class.getName());
        validateDatasource(ds, Arrays.asList("form1 de_DE", "form2 en_US", "form3 en_GB"),
            Arrays.asList("1", "2", "3"));
    }

    private void validateDatasource(SimpleDataSource ds, List<String> expectedTexts, List<String> expectedValues) {
        Iterator<Resource> dsIterator = ds.iterator();
        int index = 0;
        for (String expectedText : expectedTexts) {
            assertTrue(dsIterator.hasNext());
            Resource elem = dsIterator.next();
            assertEquals(expectedText, elem.getValueMap().get("text", String.class));
            assertEquals(expectedValues.get(index), elem.getValueMap().get("value", String.class));
            index++;
        }
        assertFalse(dsIterator.hasNext());
    }


    private void updateRequestAndActivate(String suffix) {
        RequestPathInfo requestPathInfo = request.getRequestPathInfo();
        if (requestPathInfo instanceof MockRequestPathInfo && StringUtils.isNotEmpty(suffix)) {
            ((MockRequestPathInfo) requestPathInfo).setSuffix(suffix);
        }
        marketoDatasource.doGet(request, response);
    }

    private static class MockMarketoService implements MarketoService {

        @Override
        @SuppressWarnings("squid:S1168")
        public List<MarketoFormData> getMarketoForms(@NotNull String resourcePath) {
            if (StringUtils.equals(resourcePath, "/content")) {
                return Collections.singletonList(new MockMarketoFormData("globalForm", 0, "en_US"));
            }
            if (StringUtils.equals(resourcePath, "non-existing")) {
                return null;
            }
            if (StringUtils.equals(resourcePath, "/content/dx/us/en/goodPage")) {
                return Arrays.asList(new MockMarketoFormData("form1", 1, "de_DE"),
                    new MockMarketoFormData("form2", 2, "en_US"),
                    new MockMarketoFormData("form3", 3, "en_GB"));
            }
            return Collections.emptyList();
        }
    }

}
