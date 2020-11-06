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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.adobe.dx.inlinestyle.InlineStyleWorker;

import org.junit.jupiter.api.Test;

class BackgroundTest extends AbstractInlineStyleWorkerTest {

    @Override
    InlineStyleWorker getWorker() {
        return new Background();
    }


    @Test
    void lockKey() {
        assertEquals("background", getWorker().getKey());
    }

    @Test
    void getBackgroundImage() {
        context.build().resource(CONTENT_ROOT,
            "fileReference", "/content/dam/mobile.jpg",
            "fileReferenceTablet", "/content/dam/tablet.jpg",
            "fileReferenceDesktop", "/content/dam/desktop.jpg",
            "focusX", 30L,
            "focusY", 30L);
        assertEquals("background-image: url(%2fcontent%2fdam%2fmobile.jpg); background-size: cover; background-position: 30% 30% ", getDeclaration("mobile"));
        assertEquals("background-image: url(%2fcontent%2fdam%2fdesktop.jpg); background-size: cover", getDeclaration("desktop"));
    }

    @Test
    void getBackgroundColor() {
        context.build().resource(CONTENT_ROOT,
            "backgroundColorDesktop", "white");
        assertEquals("background-color: #FEFEFE", getDeclaration("desktop"));
        assertNull(getDeclaration("mobile"));
        assertNull(getDeclaration("tablet"));
    }

    @Test
    void getBackgroundGradient() {
        context.build().resource(CONTENT_ROOT,
            "gradientTablet", "red");
        assertEquals("background-image: linear-gradient(180deg, rgba(0, 0, 0, 0.5) 36.8%,rgba(255, 0, 0, 0.78) 95.0%)", getDeclaration("tablet"));
        assertNull(getDeclaration("mobile"));
        assertNull(getDeclaration("desktop"));
    }

    @Test
    void getBackgroundCumulate() {
        context.build().resource(CONTENT_ROOT,
            "fileReference", "/content/dam/mobile.jpg",
            "focusX", 30L,
            "focusY", 30L,
            "backgroundColor", "white",
            "gradient", "red");
        assertEquals("background-color: #FEFEFE; background-image: linear-gradient(180deg, rgba(0, 0, 0, 0.5) 36.8%,rgba(255, 0, 0, 0.78) 95.0%),url(%2fcontent%2fdam%2fmobile.jpg); background-size: cover; background-position: 30% 30% ",
            getDeclaration("mobile"));
    }

    @Test
    void assertNoRule() {
        assertNull(getRule("mobile"));
    }
}