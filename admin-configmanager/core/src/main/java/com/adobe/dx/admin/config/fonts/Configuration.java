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

import java.util.Calendar;
import java.util.List;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Represents an Activation DTM configuration item from {@code /conf}.
 * <p>
 * The configuration might be of type {@code sling:Folder} or
 * {@code cq/dtm-reactor/component/conf/page}.
 * </p>
 */
@ProviderType
public interface Configuration {

    /**
     * Returns the title of the item.
     * 
     * @return Item title or resource name if none found. Returns never {@code null}
     */
    @NotNull
    String getTitle();

    /**
     * Indicates if item has children.
     * 
     * @return {@code true} if item has children
     */
    @NotNull
    boolean hasChildren();

    /**
     * Returns the last modified time stamp.
     * 
     * @return Last modified time in milliseconds or {@code null}
     */
    @Nullable
    Calendar getLastModifiedDate();

    /**
     * Returns the user which last modified the item
     * 
     * @return User identifier or {@code null}
     */
    @Nullable
    String getLastModifiedBy();

    /**
     * Returns the last published time stamp.
     * 
     * @return Last published time in milliseconds or {@code null}
     */
    @Nullable
    Calendar getLastPublishedDate();

    /**
     * Returns a list of quickactions rel identifiers for that item.
     * 
     * @return List of quickactions rel identifiers or an empty list
     */
    @NotNull
    List<String> getQuickactionsRels();
}