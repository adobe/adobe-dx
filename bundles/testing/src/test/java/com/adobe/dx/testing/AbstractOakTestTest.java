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

package com.adobe.dx.testing;

import static com.adobe.dx.testing.AbstractTest.buildContext;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class AbstractOakTestTest {
    AemContext context = buildContext(ResourceResolverType.JCR_OAK);
    AbstractOakTest test;

    @BeforeEach
    public void setup() {
        test = new AbstractOakTest();
        test.context = context;
    }

    @Test
    public void test() {
        context.build().resource(AbstractTest.CONTENT_ROOT, "foo", "bar", "blah", 2).commit();
        ValueMap properties = test.getVM(AbstractTest.CONTENT_ROOT);
        assertEquals("bar", properties.get("foo"));
        assertEquals(2L, properties.get("blah"));
    }
}
