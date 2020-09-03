/*******************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2019 Adobe
 *  All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains
 * the property of Adobe and its suppliers, if any. The intellectual
 * and technical concepts contained herein are proprietary to Adobe
 * and its suppliers and are protected by all applicable intellectual
 * property laws, including trade secret and copyright laws.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe.
 ******************************************************************************/

package com.adobe.dx.structure;

import com.adobe.dx.bindings.internal.DxBindingsValueProvider;
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.responsive.ResponsiveConfiguration;
import com.adobe.dx.testing.AbstractRequestModelTest;

import java.util.Map;

import javax.script.Bindings;
import javax.script.SimpleBindings;

import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.caconfig.MockContextAwareConfig;
import org.junit.jupiter.api.BeforeEach;

import io.wcm.testing.mock.aem.junit5.AemContext;

public class AbstractStructureModelTest extends AbstractRequestModelTest {

    protected SlingBindings getBindings(AemContext context) {
        return (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
    }

    protected static final String MODEL_PATH = CONTENT_ROOT + "/model";

    @BeforeEach
    public void beforeEach() {
        context.build().resource(CONF_ROOT + "/sling:configs/" + ResponsiveConfiguration.class.getName() + "/breakpoints")
            .siblingsMode()
            .resource("1", "propertySuffix", "Mobile", "key", "mobile")
            .resource("2", "propertySuffix", "Tablet", "key", "tablet")
            .resource("3", "propertySuffix", "Desktop", "key", "desktop");
        MockContextAwareConfig.registerAnnotationClasses(context, ResponsiveConfiguration.class);
        MockContextAwareConfig.registerAnnotationClasses(context, Breakpoint.class);
        context.create().resource(CONTENT_ROOT, "sling:configRef", CONF_ROOT);
    }

    @Override
    protected <T> T getModel(Class<T> type) {
        DxBindingsValueProvider provider = new DxBindingsValueProvider();
        Bindings bindings = new SimpleBindings();
        for (Map.Entry<String, Object> entry : getBindings(context).entrySet()) {
            bindings.put(entry.getKey(), entry.getValue());
        }
        bindings.put(SlingBindings.RESOURCE, context.currentResource());
        provider.addBindings(bindings);
        SlingBindings slingBindings = getBindings(context);
        slingBindings.put(DxBindingsValueProvider.POLICY_KEY, bindings.get(DxBindingsValueProvider.POLICY_KEY));
        slingBindings.put(DxBindingsValueProvider.BP_KEY, bindings.get(DxBindingsValueProvider.BP_KEY));
        slingBindings.put(DxBindingsValueProvider.RESP_PROPS_KEY, bindings.get(DxBindingsValueProvider.RESP_PROPS_KEY));
        return super.getModel(type);
    }

    @Override
    protected <T> T getModel(Class<T> type, String path) {
        context.currentResource(path);
        return getModel(type);
    }
}
