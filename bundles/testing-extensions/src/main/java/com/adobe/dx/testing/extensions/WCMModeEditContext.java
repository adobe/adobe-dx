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

package com.adobe.dx.testing.extensions;

import static com.adobe.dx.testing.extensions.ExtensionsUtil.getContext;
import static com.day.cq.wcm.api.WCMMode.REQUEST_ATTRIBUTE_NAME;

import com.day.cq.wcm.api.WCMMode;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import io.wcm.testing.mock.aem.junit5.AemContext;

public class WCMModeEditContext implements BeforeTestExecutionCallback {
    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        AemContext context = getContext(extensionContext);
        context.request().setAttribute(REQUEST_ATTRIBUTE_NAME, WCMMode.EDIT);
    }
}