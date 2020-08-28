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

package com.adobe.dx.testing;

import com.adobe.dx.bindings.internal.DxBindingsValueProvider;
import com.adobe.dx.inlinestyle.InlineStyleWorker;
import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.testing.extensions.ResponsiveContext;
import com.adobe.dx.testing.extensions.StyleGuideContext;
import com.adobe.dx.utils.RequestUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.scripting.SlingBindings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ResponsiveContext.class)
@ExtendWith(StyleGuideContext.class)
public abstract class AbstractInlineStyleWorkerTest extends AbstractTest {

    protected abstract InlineStyleWorker getWorker();

    Breakpoint getBreakpoint(String key) {
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

    @BeforeEach
    public void setup() {
        SlingBindings bindings = (SlingBindings)context.request().getAttribute(SlingBindings.class.getName());
        bindings.put(DxBindingsValueProvider.POLICY_KEY, getVM(CONTENT_ROOT));
    }

    protected String getRule(String key, String id) {
        return getWorker().getRule(getBreakpoint(key), id, context.request());
    }

    protected String getDeclaration(String key) {
        return getWorker().getDeclaration(getBreakpoint(key), context.request());
    }
}
