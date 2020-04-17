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

import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;

import com.day.cq.replication.Preprocessor;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.AbstractResourceVisitor;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.ModificationType;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * will tag every untagged component with an id that is meant to be unique and to stick to it:
 *
 * <ol>
 *     <li><code>dx_id</code> that are the "almost" unique first 8 chars of paths + date's SHA-1</li>
 *     <li><code>dx_pageId</code> that are the "almost" unique first 8 chars of page path SHA-1</li>
 * </ol>
 *
 * the tag consists in checking page hash, then if it's absent or inconsistent with current page,
 * write a new one, together with dx_id
 *
 * that operation happens at several times:
 * <ul>
 *     <li>each time a post is made to the component, including the first time</li>
 *     <li>each time the containing page is replicated (this addition is to catch special cases when
 *     component is copied / live copied without POST request)</li>
 * </ul>
 */
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = IDTagger.Configuration.class)
public class IDTagger implements Preprocessor, SlingPostProcessor {
    private final Logger LOG = LoggerFactory.getLogger(IDTagger.class);

    /**
     * page id (obtained from path)
     */
    static final String PN_PAGEHASH = "dx_pageId";

    /**
     * component id (obtained from path + time salt)
     */
    static final String PN_COMPID = "dx_id";

    static final String ID_PREFIX = "d";

    static final short ID_SIZE = 8;

    static final String SERVICE_NAME = "content-writer";

    Function<Resource, Boolean> resourceFilter;

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Activate
    @Modified
    public void activate(Configuration configuration) {
        List<Pattern> acceptedTypes = new ArrayList<>();
        for (String regexp : configuration.acceptedTypes()) {
            acceptedTypes.add(Pattern.compile(regexp));
        }
        resourceFilter = getFilter(acceptedTypes);
    }

    Function<Resource, Boolean> getFilter(List<Pattern> acceptedTypes) {
        return r -> {
            if (StringUtils.isNotBlank(r.getResourceType())) {
                for (Pattern pattern: acceptedTypes) {
                    if (pattern.matcher(r.getResourceType()).matches()) {
                        return true;
                    }
                }
            }
            return false;
        };
    }

    @Override
    public void preprocess(ReplicationAction replicationAction, ReplicationOptions replicationOptions) throws ReplicationException {
        if (ReplicationActionType.ACTIVATE.equals(replicationAction.getType())) {
            try (ResourceResolver resourceResolver =
                     resourceResolverFactory.getServiceResourceResolver(Collections.singletonMap(SUBSERVICE, SERVICE_NAME))) {
                Resource currentResource = resourceResolver.getResource(replicationAction.getPath());
                if (currentResource == null) {
                    LOG.debug("replication action made on a non existing or not readable resource {}, abort", replicationAction.getPath());
                    return;
                }
                Page currentPage = currentResource.adaptTo(Page.class);
                if (currentPage == null) {
                    LOG.debug("replication action made on something else than a page, abort {}", currentResource.getPath());
                    return;
                }
                //we just tag page resources
                tagPage(currentPage);
                resourceResolver.commit();
            } catch (LoginException | PersistenceException e) {
                LOG.error("Issues with current user or content, will not tag that resource", e);
            }
        }
        String path = replicationAction.getPath();
    }

    void tagPage(Page currentPage) {
        String pageHash = getUniqueId(currentPage.getPath(), false);
        for (Iterator<Resource> componentIterator = new ComponentIterator(resourceFilter, currentPage.getContentResource());
             componentIterator.hasNext();) {
            Resource component = componentIterator.next();
            if (!isResourceTagValid(pageHash, component)) {
                tagResource(pageHash, component);
            }
        }
    }

    /**
     * Assuming this resource is "taggable", this method will tell us it is validly tagged
     * that is it has a tag, and current page id tag (not time salted) is still ok.
     *
     * @param currentPageHash  hash from page whom we are browsing the content from
     * @param resource resource we want to check
     * @return
     */
    boolean isResourceTagValid(@NotNull String currentPageHash, @NotNull Resource resource) {
        ValueMap map = resource.getValueMap();
        if (map.containsKey(PN_PAGEHASH) && map.containsKey(PN_COMPID)) {
            return currentPageHash.equals(map.get(PN_PAGEHASH, String.class));
        }
        return false;
    }

    /**
     * Tags a given resource with both current page id, and component id
     *
     * @param currentPageHash hash from page whom we are browsing the content from
     * @param resource resource we want to tag
     */
    void tagResource(String currentPageHash, Resource resource) {
        ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
        if (map != null) {
            map.put(PN_PAGEHASH, currentPageHash);
            map.put(PN_COMPID, getUniqueId(resource.getPath(), true));
        }
    }

    /**
     * Creates a Unique ID based on a path.
     *
     * @param path          The path we should generate an ID from
     * @param timeSalt      wether we should salt it with a time based salt or not
     * @return              The unique ID (first <code>ID_SIZE</code> chars of sha1 hash)
     */
    String getUniqueId(final String path, boolean timeSalt) {
        String source = path;
        if (timeSalt) {
            source += Calendar.getInstance().toString();
        }
        String sha1 = DigestUtils.sha1Hex(source);
        return ID_PREFIX + sha1.substring(0, ID_SIZE);
    }

    @Override
    public void process(SlingHttpServletRequest slingHttpServletRequest, List<Modification> list) throws Exception {
        for (Modification modification : list) {
            LOG.trace("{}", modification);
            if (ModificationType.CREATE.equals(modification.getType())) {
                String path = modification.getSource();
                ResourceResolver resolver = slingHttpServletRequest.getResourceResolver();
                Resource resource = resolver.getResource(path);
                if (resourceFilter.apply(resource)) {
                    PageManager pageManager = resolver.adaptTo(PageManager.class);
                    Page currentPage = pageManager.getContainingPage(resource);
                    if (currentPage != null) {
                        String pageHash = getUniqueId(currentPage.getPath(), false);
                        if (!isResourceTagValid(pageHash, resource)) {
                            tagResource(pageHash, resource);
                        }
                    }
                }
            }
        }
    }

    /**
     * lists all resource in a resource tree that have resource type corresponding to passed patterns
     */
    static class ComponentIterator extends AbstractResourceVisitor implements Iterator<Resource> {

        Function<Resource, Boolean> rFilter;
        List<Resource> internalList = new ArrayList<>();
        Iterator<Resource> internalIT;

        public ComponentIterator(Function<Resource, Boolean> filter, Resource root) {
            rFilter = filter;
            accept(root);
        }

        @Override
        protected void visit(@NotNull Resource resource) {
            if (rFilter.apply(resource)) {
                internalList.add(resource);
            }
        }

        Iterator<Resource> getIterator() {
            if (internalIT == null) {
                internalIT = internalList.iterator();
            }
            return internalIT;
        }

        @Override
        public boolean hasNext() {
            return getIterator().hasNext();
        }

        @Override
        public Resource next() {
            return getIterator().next();
        }
    }

    @ObjectClassDefinition(name = "Adobe DX ID Tagger")
    public @interface Configuration {

        @AttributeDefinition(
            name = "Accepted types",
            description = "list of pattern (https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html) "
                + "for resource types that should be tagged with an id"
        )
        @SuppressWarnings("squid:S00100")
        String[] acceptedTypes() default { "dx/components/structure/.*" };
    }
}
