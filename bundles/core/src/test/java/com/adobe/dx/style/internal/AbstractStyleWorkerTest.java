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

package com.adobe.dx.style.internal;

import com.adobe.dx.bindings.internal.DxBindingsValueProvider;
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.style.StyleWorker;
import com.adobe.dx.testing.AbstractTest;

import org.apache.sling.api.scripting.SlingBindings;

public abstract class AbstractStyleWorkerTest extends AbstractTest {

    abstract StyleWorker getWorker();

    String getDeclaration() {
        return getDeclaration(null);
    }

    String getDeclaration(Breakpoint breakpoint) {

        SlingBindings bindings = (SlingBindings)context.request().getAttribute(SlingBindings.class.getName());
        bindings.put(DxBindingsValueProvider.POLICY_KEY, getVM(CONTENT_ROOT));
        return getWorker().getDeclaration(breakpoint, context.request());
    }
}
