/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.adobe.dx.admin.mocks;

import org.apache.sling.xss.XSSAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MockSlingXssApi implements XSSAPI {

    private static final String NOT_IMPLEMENTED = "Not implemented";

    @Override
    public Integer getValidInteger(String s, int i) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Long getValidLong(String s, long l) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public String getValidDimension(String s, String s1) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @NotNull
    @Override
    public String getValidHref(String s) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public String getValidJSToken(String s, String s1) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public String getValidCSSColor(String s, String s1) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public String encodeForHTML(String s) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public String encodeForHTMLAttr(String s) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public String encodeForXML(String s) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public String encodeForXMLAttr(String s) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public String encodeForJSString(String s) {
        return s;
    }

    @NotNull
    public String filterHTML(String s) {
        return s;
    }

    @Nullable
    @Override
    public Double getValidDouble(@Nullable String s, double v) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Nullable
    @Override
    public String getValidStyleToken(@Nullable String s, @Nullable String s1) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public String getValidMultiLineComment(@Nullable String s, @Nullable String s1) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public String getValidJSON(@Nullable String s, @Nullable String s1) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public String getValidXML(@Nullable String s, @Nullable String s1) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Nullable
    @Override
    public String encodeForCSSString(@Nullable String s) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

}
