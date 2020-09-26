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

package com.adobe.dx.admin.datasource.internal;

import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

/**
 * Context Aware DataSource that returns a list of resources based on component DS properties.
 *
 * <p>Pass in the <code>bucketName</code> and <code>confName</code> to resolve where to pick up
 * the resources from <code>/conf</code>.</p>
 *
 * <p>Because all properties are exposed as a collection, one can use any parent resource type
 * that supports datasources... table, form/colorfield, form/select, etc.</p>
 *
 * <code>
 *     <style
 *         jcr:primaryType="nt:unstructured"
 *         sling:resourceType="granite/ui/components/coral/foundation/form/select"
 *         fieldLabel="Style"
 *         name="./style">
 *         <datasource
 *             jcr:primaryType="nt:unstructured"
 *             sling:resourceType="/apps/dexter/components/author/datasource/contextAwareDataSource"
 *             sling:bucketName="cq:styleguide"
 *             confName="components/button"/>
 *     </style>
 * </code>
 */
@Model(
    adaptables = { SlingHttpServletRequest.class }
)
public class ContextAwareDatasource {

    private static final String PN_BUCKET_NAME = "sling:bucketName";
    private static final String PN_CONF_NAME = "confName";
    private static final String DEFAULT_BUCKET_NAME = "sling:configs";
    private static final String WCM_POLICIES = "/wcm/policies";
    private static final String CONTENT_ROOT_PATH = "/content";

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private ConfigurationResourceResolver configurationResolver;

    @PostConstruct
    private void init() {

        Resource componentResource = request.getResource();

        final Config cfg = new Config(componentResource.getChild(Config.DATASOURCE));

        String confName = cfg.get(PN_CONF_NAME, StringUtils.EMPTY);
        String bucketName = cfg.get(PN_BUCKET_NAME, DEFAULT_BUCKET_NAME);

        Resource contentResource = getContentResource();

        if (contentResource != null) {
            Collection<Resource> configResources =
                configurationResolver.getResourceCollection(contentResource, bucketName, confName);

            List<Resource> rawResourceList = new ArrayList<>(configResources);
            List<Resource> modifiedResourceList = new ArrayList<Resource>();
            for (Resource resource : rawResourceList) {
                String name = resource.getName();
                ModifiableValueMap props = resource.adaptTo(ModifiableValueMap.class);

                // Store the initial value in case it's needed.
                props.put("initialValue", props.get("value"));

                // Reset the value to the CA-Config name for retrieval on render
                props.remove("value");
                props.put("value", name);

                // Ensure the is text property exists
                String text = props.get("text", String.class);
                if (text == null) {
                    text = props.get("label", String.class);
                    if (text == null) {
                        text = name;
                    }
                    props.put("text", text);
                }
                Resource modifiedResource = new ValueMapResource(resourceResolver, new ResourceMetadata(), "nt:unstructured", props);

                modifiedResourceList.add(modifiedResource);
            }

            DataSource dataSource = new SimpleDataSource(modifiedResourceList.iterator());
            request.setAttribute(DataSource.class.getName(), dataSource);
        }
    }

    /**
     * Gets the content resource.
     * @return the content resource being edited
     */
    private Resource getContentResource() {
        Resource contentResource = null;
        // Attempt to get suffix (editing a component)
        String resourcePath = request.getRequestPathInfo().getSuffix();
        if (resourcePath != null) {
            contentResource = resourceResolver.getResource(resourcePath);
            if (contentResource == null) {
                contentResource = getSyntheticResource(resourcePath);
            }
        } else {
            contentResource = getPageResource();
        }

        // If our content resource is still null, get the base content path.
        if (contentResource == null) {
            contentResource = resourceResolver.getResource(CONTENT_ROOT_PATH);
        }
        return contentResource;
    }

    /**
     * Gets a page resource if a resource cannot be resolved by path alone.
     * @return the page resource
     */
    private Resource getPageResource() {
        // If editing page properties, use the request parameter.
        String itemPath = request.getParameter("item");
        if (itemPath != null) {
            Resource itemResource = resourceResolver.getResource(itemPath);
            if (itemResource != null) {
                Page page = itemResource.adaptTo(Page.class);
                if (page != null) {
                    return page.getContentResource();
                }
            }
        }
        return null;
    }

    /**
     * Get a synthetic resource
     * @param resourcePath
     * @return the resource that best represents the path of the content being edited.
     */
    private Resource getSyntheticResource(String resourcePath) {
        // Create a synthetic resource if:
        // 1. a path was forced in HTL and the resource doesn't exist.
        // 2. We're editing a content policy.
        Resource syntheticResource =
            new SyntheticResource(resourceResolver, resourcePath, JcrConstants.NT_UNSTRUCTURED);
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager != null) {
            // Using page instead of returning the resource directly because CAConfig currently
            // doesn't handle synthetic resources (https://issues.apache.org/jira/browse/SLING-7539)
            Page page = pageManager.getContainingPage(syntheticResource);
            if (page != null) {
                Resource pageContent = page.getContentResource();
                if (pageContent != null) {
                    return pageContent;
                }
            }
            // If we still don't have a content resource, we're editing a content policy that doesn't exist.
            // traverse up the JCR to find our conf settings (/conf/myproject/settings)
            String syntheticPath = syntheticResource.getPath();
            String[] split = syntheticPath.split(WCM_POLICIES);
            String confSettingsPath = split[0];
            Resource settingsResource = resourceResolver.getResource(confSettingsPath);
            if (settingsResource != null) {
                return settingsResource;
            }
        }
        return null;
    }
}


