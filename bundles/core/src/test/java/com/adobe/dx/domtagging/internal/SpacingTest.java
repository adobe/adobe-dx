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
package com.adobe.dx.domtagging.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adobe.dx.responsive.internal.ResponsivePropertiesImplTest;
import com.adobe.dx.testing.AbstractTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpacingTest extends AbstractTest {

    private final static List<String> EXPECTED_WO_INHERITANCE = Arrays.asList(
        "mobile-margin-top-100",
        "mobile-margin-right-50",
        "mobile-margin-bottom-10",
        "mobile-margin-left-84",
        "mobile-padding-top-4",
        "mobile-padding-right-86",
        "mobile-padding-bottom-0",
        "mobile-padding-left-50",
        "tablet-margin-top-15",
        "tablet-margin-right-0",
        "tablet-margin-bottom-50",
        "tablet-margin-left-0",
        "desktop-padding-top-10",
        "desktop-padding-right-20",
        "desktop-padding-bottom-30",
        "desktop-padding-left-40"
    );

    private static List<String> EXPECTED_W_INHERITANCE = Arrays.asList(
        "mobile-margin-top-100",
        "mobile-margin-right-50",
        "mobile-margin-bottom-10",
        "mobile-margin-left-84",
        "mobile-padding-top-4",
        "mobile-padding-right-86",
        "mobile-padding-bottom-0",
        "mobile-padding-left-50",
        "tablet-margin-top-15",
        "tablet-margin-right-0",
        "tablet-margin-bottom-50",
        "tablet-margin-left-0",
        "desktop-padding-top-10",
        "desktop-padding-right-20",
        "desktop-padding-bottom-30",
        "desktop-padding-left-40",
        //difference is desktop inheriting here from tablet for margin:
        "desktop-margin-top-15",
        "desktop-margin-right-0",
        "desktop-margin-bottom-50",
        "desktop-margin-left-0");

    Spacing spacing;

    @BeforeEach
    public void setup() {
        ResponsivePropertiesImplTest.setBreakpoints(context);
        spacing = new Spacing();
        context.currentResource(CONTENT_ROOT);
    }

    private void enterSpacingContent() {
        context.build().resource(CONTENT_ROOT,
            "marginTopMobile", 100L,
            "marginRightMobile", 50L,
            "marginBottomMobile", 10L,
            "marginLeftMobile", 84L,
            "paddingTopMobile", 4L,
            "paddingRightMobile", 86L,
            "paddingBottomMobile", 0L,
            "paddingLeftMobile", 50L,
            "marginTopTablet", 15L,
            "marginRightTablet", 0L,
            "marginBottomTablet", 50L,
            "marginLeftTablet", 0L,
            "paddingTopDesktop", 10L,
            "paddingRightDesktop", 20L,
            "paddingBottomDesktop", 30L,
            "paddingLeftDesktop", 40L);
    }

    @Test
    public void lockKey() {
        assertEquals("spacing", spacing.getKey());
    }

    @Test
    public void lockAttributes() {
        assertNull(spacing.getClasses(context.request()));
    }

    @Test
    public void withoutInheritance() {
        enterSpacingContent();
        context.build().resource(CONTENT_ROOT,
            "inheritTablet", "override",
            "inheritDesktop", "override");
        Collection<String> classes = spacing.getClasses(context.request());
        assertNotNull(classes);
        assertTrue(CollectionUtils.isEqualCollection(classes, EXPECTED_WO_INHERITANCE));
    }

    @Test
    public void withMarginDesktopInheritance() {
        enterSpacingContent();
        context.build().resource(CONTENT_ROOT,
            "inheritTablet", "override",
            "inheritDesktop", "override",
            "inheritDesktopMargin", "inherit");
        Collection<String> classes = spacing.getClasses(context.request());
        assertNotNull(classes);
        assertTrue(CollectionUtils.isEqualCollection(classes, EXPECTED_W_INHERITANCE));
    }

    @Test
    public void assertNullWhenEmpty() {
        assertNull(spacing.getClasses(context.request()));
    }

}
