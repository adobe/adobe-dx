/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.dx.admin.config.fonts;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides an interface to retrieve {@code Settings} for a
 * {@link SlingHttpServletRequest}.
 */
@ProviderType
public interface SettingsProvider {

    /**
     * Returns the {@link Settings} for the specified {@code request}.
     *
     * @param request SlingHttpServletRequest
     * @return the {@link Settings} or {@code null} if not applicable for the
     *         {@code request}
     * @throws IllegalArgumentException if {@code request} argument is
     *             {@code null}
     */
    @Nullable
    Settings getSettings(@Nullable SlingHttpServletRequest request, @NotNull String configName);

}
