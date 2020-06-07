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

package com.adobe.dx.content.mocks;

import com.adobe.dx.utils.service.CloudConfigReader;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

public class MockCloudConfigReader implements CloudConfigReader {

    private Map<String, Object> toReturnMap = new HashMap<>();

    public <T>  void setWhatToReturn(@NotNull String resourcePath, @NotNull String configName, T configObj) {
        toReturnMap.put(resourcePath + configName, configObj);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getContextAwareCloudConfigRes(@NotNull String resourcePath, String configName, Class<T> type) {
        return (T) toReturnMap.get(resourcePath + configName);
    }
}
