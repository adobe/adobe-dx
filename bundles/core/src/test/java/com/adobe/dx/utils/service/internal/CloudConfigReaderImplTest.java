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

package com.adobe.dx.utils.service.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.adobe.dx.testing.AbstractTest;

import java.util.Collection;
import java.util.Collections;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CloudConfigReaderImplTest extends AbstractTest {

    private CloudConfigReaderImpl cloudConfigReaderImpl = new CloudConfigReaderImpl();

    @BeforeEach
    private void setup() {
        context.load().json("/mocks/utils.service.internal/cloud-config.json",
            "/conf/global/settings/cloudconfigs/simpleConfig");
        context.load().json("/mocks/utils.service.internal/simplepage.json",
            "/content/dx");
        context.registerService(ConfigurationResourceResolver.class, new MockConfigurationResourceResolver());
        context.registerInjectActivateService(cloudConfigReaderImpl);
    }

    @Test
    void testWhenConfigResourceIsNotPresent() {
        ValueMap cloudConfig = cloudConfigReaderImpl.getContextAwareCloudConfigRes("/content/dx",
            "notExistentConfig", ValueMap.class);
        assertNull(cloudConfig);
    }

    @Test
    void testWhenConfigResourceIsPresent() {
        ValueMap cloudConfig = cloudConfigReaderImpl.getContextAwareCloudConfigRes("/content/dx",
            "simpleConfig", ValueMap.class);
        validateCloudConfigValues(cloudConfig);
    }

    @Test
    void testWhenConfigResourceIsPresentAndNotPage() {
        ValueMap cloudConfig = cloudConfigReaderImpl.getContextAwareCloudConfigRes("/content/dx",
            "simpleConfig/jcr:content", ValueMap.class);
        validateCloudConfigValues(cloudConfig);
    }

    private void validateCloudConfigValues(ValueMap cloudConfig) {
        assertNotNull(cloudConfig);
        assertEquals("Simple Config", cloudConfig.get("name", String.class));
        assertArrayEquals(new String[] {"array1", "array2"}, cloudConfig.get("array", String[].class));
    }

    private static class MockConfigurationResourceResolver implements ConfigurationResourceResolver {

        @Override
        public Resource getResource(@NotNull Resource resource, @NotNull String bucketName,
                                    @NotNull String configName) {
            return resource.getResourceResolver().getResource("/conf/global/"
                + bucketName + "/" + configName);
        }

        @Override
        @NotNull
        public Collection<Resource> getResourceCollection(@NotNull Resource resource, @NotNull String bucketName,
                                                          @NotNull String configName) {
            return Collections.emptyList();
        }

        @Override
        public String getContextPath(@NotNull Resource resource) {
            return null;
        }

        @Override
        @NotNull
        public Collection<String> getAllContextPaths(@NotNull Resource resource) {
            return Collections.emptyList();
        }
    }
}
