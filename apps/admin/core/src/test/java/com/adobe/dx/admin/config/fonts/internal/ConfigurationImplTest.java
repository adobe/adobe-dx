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
package com.adobe.dx.admin.config.fonts.internal;

import static com.adobe.dx.admin.config.fonts.internal.ConfigurationImpl.DEFAULT_QUICK_ACTIONS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adobe.dx.admin.config.fonts.Configuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.adobe.dx.testing.AbstractRequestModelTest;

public class ConfigurationImplTest extends AbstractRequestModelTest {

    private static final String CLOUD_CONF =  "settings/cloudconfigs/some";
    private static final String CLOUD_CONF_NOTPAGE = "settings/cloudconfigs/some/notPage";
    private static final String WHATEVER_PAGE = "whatever/page";
    private static final String EXPECTED_DATE_VALUE = "20180214";
    private static final String SDF = "yyyyMMdd";

    Configuration defaultConfiguration;
    Configuration notPageConfiguration;

    private Configuration getConfiguration(String path) throws Exception {
        context.addModelsForPackage(ConfigurationImpl.class.getPackage().getName());
        return getModel(Configuration.class, CONF_ROOT + "/" + path);
    }

    @BeforeEach
    private void setup() throws Exception {
        context.load().json("/mocks/admin.adobefonts/configuration-tree.json", CONF_ROOT);
        defaultConfiguration = getConfiguration(CLOUD_CONF);
        notPageConfiguration = getConfiguration(CLOUD_CONF_NOTPAGE);
    }

    @Test
    void testLastModified() throws Exception {
        assertEquals("admin", defaultConfiguration.getLastModifiedBy());
        assertNotNull(defaultConfiguration.getLastModifiedDate());
        assertEquals(EXPECTED_DATE_VALUE, getDateString(defaultConfiguration.getLastModifiedDate()));
        assertNull(notPageConfiguration.getLastModifiedDate());
    }

    @Disabled(value="@todo remove when https://wcm-io.atlassian.net/browse/WTES-51 is fixed and consumed")
    @Test
    void testLastPublished() {
        assertNotNull(defaultConfiguration.getLastPublishedDate());
        assertEquals(EXPECTED_DATE_VALUE, getDateString(defaultConfiguration.getLastPublishedDate()));
    }

    String getDateString(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat(SDF);
        Date date = new Date();
        date.setTime(cal.getTimeInMillis());
        return sdf.format(date);
    }

    @Test
    void testQuickActions() throws Exception {
        assertEquals(DEFAULT_QUICK_ACTIONS, defaultConfiguration.getQuickactionsRels());
        assertTrue(getConfiguration(WHATEVER_PAGE).getQuickactionsRels().isEmpty());
    }

    @Test
    void testHasChildren() throws Exception {
        assertTrue(getConfiguration(".").hasChildren());
        assertFalse(notPageConfiguration.hasChildren());
    }

    @Test
    void testGetTitle() throws Exception{
        assertEquals("Cloud Configuration",
            defaultConfiguration.getTitle(),
            "title should be Cloud Configuration");
        assertEquals("notPage", notPageConfiguration.getTitle(),
            "title should be page node name in case no title is there");
    }

}