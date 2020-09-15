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

package com.adobe.dx.inlinestyle.internal;

import com.adobe.dx.bindings.internal.DxBindingsValueProvider;
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.inlinestyle.InlineStyleWorker;
import com.adobe.dx.responsive.internal.ResponsivePropertiesImplTest;
import com.adobe.dx.styleguide.StyleGuide;
import com.adobe.dx.styleguide.StyleGuideItem;
import com.adobe.dx.testing.AbstractTest;
import com.adobe.dx.utils.RequestUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.record.PageBreakRecord;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.caconfig.MockContextAwareConfig;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractInlineStyleWorkerTest extends AbstractTest {

    abstract InlineStyleWorker getWorker();

    String getDeclaration() {
        return getDeclaration("mobile");
    }

    @BeforeEach
    void setup(){
        setStyleGuide();
        ResponsivePropertiesImplTest.setBreakpoints(context);
        context.currentResource(CONTENT_ROOT);
    }


    void setStyleGuide() {
        String confRoot = CONF_ROOT + "/sling:configs/" + StyleGuide.class.getName() + "/";
        context.build().resource(confRoot + "colors")
            .siblingsMode()
            .resource("1","key", "red", "value", "#FE0000")
            .resource("2", "key", "white", "value", "#FEFEFE")
            .resource("3", "key", "blue", "value", "#0000FE");
        context.build().resource(confRoot + "gradients")
            .siblingsMode()
            .resource("1","label", "Adobe Red", "key", "red", "value", "linear-gradient(180deg, rgba(0, 0, 0, 0.5) 36.8%,rgba(255, 0, 0, 0.78) 95.0%)")
            .resource("2", "label", "Fade to black", "key", "wb", "value", "linear-gradient(180deg, rgba(0, 0, 0, 0.5) 50.0%,rgba(0, 0, 0, 1) 95.0%)")
            .resource("3", "label", "Rainbow bars", "key", "rainbow", "value", "linear-gradient(90deg, rgba(255, 0, 0, 0.41) 20.0%,rgba(255, 165, 0, 0.41) 20.0%,rgba(255, 165, 0, 0.41) 40.0%,rgba(255, 255, 0, 0.41) 40.0%,rgba(255, 255, 0, 0.41) 60.0%,rgba(0, 128, 0, 0.41) 60.0%,rgba(0, 128, 0, 0.41) 80.0%,rgba(0, 0, 255, 0.4) 80.0%)");
        MockContextAwareConfig.registerAnnotationClasses(context, StyleGuide.class);
        MockContextAwareConfig.registerAnnotationClasses(context, StyleGuideItem.class);
    }

    Breakpoint getBreakpoint(String key) {
        SlingBindings bindings = (SlingBindings)context.request().getAttribute(SlingBindings.class.getName());
        bindings.put(DxBindingsValueProvider.POLICY_KEY, getVM(CONTENT_ROOT));
        Breakpoint breakpoint = null;
        if (StringUtils.isNotBlank(key)) {
            for (Breakpoint candidate : RequestUtil.getBreakpoints(context.request())) {
                if (candidate.key().equals(key)) {
                    breakpoint = candidate;
                    break;
                }
            }
        }
        return breakpoint;
    }

    String getRule(String breakpointKey) {
        return getWorker().getRule(getBreakpoint(breakpointKey), "someid", context.request());
    }

    String getDeclaration(@NotNull String breakpointKey) {
        return getWorker().getDeclaration(getBreakpoint(breakpointKey), context.request());
    }
}
