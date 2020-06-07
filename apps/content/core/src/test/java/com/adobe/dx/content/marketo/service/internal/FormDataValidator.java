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

package com.adobe.dx.content.marketo.service.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.adobe.dx.content.marketo.service.MarketoFormData;

import java.util.List;

class FormDataValidator {

    void validateFormData(List<MarketoFormData> forms, List<String> expectedNames, List<Integer> expectedIds,
                                  List<String> expectedLocales) {
        assertEquals(expectedNames.size(), forms.size());
        int index = 0;
        for (String expectedText : expectedNames) {
            assertEquals(expectedText, forms.get(index).getName());
            assertEquals(expectedIds.get(index), forms.get(index).getId());
            assertEquals(expectedLocales.get(index), forms.get(index).getLocale());
            index++;
        }
    }
}   
