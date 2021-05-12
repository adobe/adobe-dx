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
package com.adobe.dx.admin.rendercondition.internal;

import static org.apache.jackrabbit.JcrConstants.JCR_CONTENT;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;

import com.adobe.dx.testing.AbstractTest;
import com.adobe.dx.utils.service.CloudConfigReader;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlethelpers.MockRequestPathInfo;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CloudConfigPropertyRenderConditionTest extends AbstractTest {

    private static final @NotNull String CLOUD_CONFIG_PATH = CONF_ROOT + "/settings/cloudconfigs/ccp-rc-test";
    
    @InjectMocks
    private CloudConfigPropertyRenderCondition condition = new CloudConfigPropertyRenderCondition();
    
    @Mock
    CloudConfigReader cloudConfigReader;

    @BeforeEach
    public void setup() {
        context.load().json("/mocks/admin.configmanager/configuration-tree-ccp-rc-test.json", CLOUD_CONFIG_PATH);
        context.load().json("/mocks/rendercondition/content-tree-ccp-rc-test.json", CONTENT_ROOT);
        
        MockitoAnnotations.initMocks(this);
        when(cloudConfigReader.getContextAwareCloudConfigRes(anyString(), anyString()))
        .thenReturn(context.resourceResolver().getResource(CLOUD_CONFIG_PATH + "/" + JCR_CONTENT).adaptTo(ValueMap.class));
        
        String pagePath = CONTENT_ROOT + "/page";
        ((MockRequestPathInfo)context.request().getRequestPathInfo()).setSuffix(pagePath);
    }

    @Test
    @DisplayName("condition should be checked for a text propety with a value")
    void computeRenderConditionText() throws RepositoryException, ServletException, IOException {
        context.currentResource(CONTENT_ROOT + "/dialog1/granite:rendercondition");
        RenderCondition result = condition.computeRenderCondition(context.request());
        assertNotNull(result);
        assertTrue(result.check());
    }

    @Test
    @DisplayName("condition should not be checked for an empty value")
    void computeRenderConditionEmptyText() throws RepositoryException, ServletException, IOException {
        context.currentResource(CONTENT_ROOT + "/dialog2/granite:rendercondition");
        RenderCondition result = condition.computeRenderCondition(context.request());
        assertNotNull(result);
        assertFalse(result.check());
    }

    @Test
    @DisplayName("condition should be checked for a true value")
    void computeRenderConditionEmptyBooleanTrue() throws RepositoryException, ServletException, IOException {
        context.currentResource(CONTENT_ROOT + "/dialog3/granite:rendercondition");
        RenderCondition result = condition.computeRenderCondition(context.request());
        assertNotNull(result);
        assertTrue(result.check());
    }
    
    @Test
    @DisplayName("condition should not be checked for a false value")
    void computeRenderConditionEmptyBooleanFalse() throws RepositoryException, ServletException, IOException {
        context.currentResource(CONTENT_ROOT + "/dialog4/granite:rendercondition");
        RenderCondition result = condition.computeRenderCondition(context.request());
        assertNotNull(result);
        assertFalse(result.check());
    }

    @Test
    @DisplayName("condition should not be checked if render condition is not configured")
    void computeRenderConditionMissingConfig() throws RepositoryException, ServletException, IOException {
        context.currentResource(CONTENT_ROOT + "/dialog5/granite:rendercondition");
        RenderCondition result = condition.computeRenderCondition(context.request());
        assertNotNull(result);
        assertFalse(result.check());
    }
}
