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

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;

import com.adobe.dx.domtagging.IDTagger;
import com.day.cq.replication.Preprocessor;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.jetbrains.annotations.Nullable;
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
 *     <li><code>dx_id</code> that are the "almost" unique first 8 chars of paths + date's SHA-256</li>
 *     <li><code>dx_pageId</code> that are the "almost" unique first 8 chars of page path SHA-256</li>
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
@Designate(ocd = IDTaggerImpl.Configuration.class)
public class IDTaggerImpl implements Preprocessor, SlingPostProcessor, IDTagger {
    private final Logger log = LoggerFactory.getLogger(IDTaggerImpl.class);

    /**
     * page id (obtained from path)
     */
    static final String PN_PAGEHASH = "dx_pageId";

    /**
     * component id (obtained from path + time salt)
     */
    static final String PN_COMPID = "dx_id";

    static final String ID_SEPARATOR = "-";

    static final short ID_SIZE = 8;

    static final String SERVICE_NAME = "content-writer";

    static final String ATT_REFERENCE = "dx:idtagger:reference_id";

    static final String ATT_ROOTID = "dx:idtagger:root_id";

    Function<Resource, Boolean> resourceFilter;

    List<String> referenceTypes;

    Configuration configuration;

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Activate
    @Modified
    public void activate(Configuration configuration) {
        Collection<Pattern> acceptedTypes = new ArrayList<>();
        for (String regexp : configuration.acceptedTypes()) {
            acceptedTypes.add(Pattern.compile(regexp));
        }
        resourceFilter = getFilter(acceptedTypes);
        this.configuration = configuration;
        referenceTypes = Arrays.asList(configuration.referenceTypes());
    }

    /**
     * @param acceptedTypes list of patterns of accepted types for the id
     * @return filter that use the list
     */
    Function<Resource, Boolean> getFilter(Collection<Pattern> acceptedTypes) {
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
        if (configuration.tagOnPublication() &&
            ReplicationActionType.ACTIVATE.equals(replicationAction.getType())) {
            try (ResourceResolver resolver =
                     resourceResolverFactory.getServiceResourceResolver(Collections.singletonMap(SUBSERVICE, SERVICE_NAME))) {
                Resource currentResource = resolver.getResource(replicationAction.getPath());
                if (currentResource == null) {
                    log.debug("replication action made on a non existing or not readable resource {}, abort", replicationAction.getPath());
                    return;
                }
                Page currentPage = currentResource.adaptTo(Page.class);
                if (currentPage == null) {
                    log.debug("replication action made on something else than a page, abort {}", currentResource.getPath());
                    return;
                }
                //we just tag page resources
                tagPage(currentPage);
                resolver.commit();
            } catch (LoginException | PersistenceException e) {
                log.error("Issues with current user or content, will not tag that resource", e);
            }
        }
    }

    /**
     * tag every single resource in given page
     * @param currentPage
     */
    void tagPage(Page currentPage) {
        String pageHash = getUniqueId(currentPage.getPath(), false);
        for (Iterator<Resource> componentIterator = new ComponentIterator(resourceFilter, currentPage.getContentResource());
             componentIterator.hasNext();) {
            Resource component = componentIterator.next();
            if (needsUpdate(component, pageHash)) {
                tagResource(component, pageHash);
            }
        }
    }

    /**
     * Indicates wether that resource tags need to be updated
     *
     * @param resource resource we want to check
     * @param currentPageHash  hash from page whom we are browsing the content from
     * @return true if we need to update, false otherwise
     */
    boolean needsUpdate(@NotNull Resource resource, @NotNull String currentPageHash) {
        ValueMap map = resource.getValueMap();
        if (map.containsKey(PN_PAGEHASH) && map.containsKey(PN_COMPID)) {
            return !currentPageHash.equals(map.get(PN_PAGEHASH, String.class));
        }
        return true;
    }

    /**
     * Get current page Hash
     * @param resource
     * @param checkCache
     * @return page hash or null if not possible to compute
     */
    String getCurrentPageId(Resource resource, boolean checkCache) {
        if (checkCache && resource.getValueMap().containsKey(PN_PAGEHASH)) {
            return resource.getValueMap().get(PN_PAGEHASH, String.class);
        }
        PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
        Page currentPage = pageManager.getContainingPage(resource);
        if (currentPage != null) {
            return getUniqueId(currentPage.getPath(), false);
        }
        return null;
    }

    /**
     * Get current resource Hash
     * @param resource
     * @param checkCache
     * @return resource path hash
     */
    @NotNull String getResourceId(Resource resource, boolean checkCache) {
        if (checkCache && resource.getValueMap().containsKey(PN_COMPID)) {
            return resource.getValueMap().get(PN_COMPID, String.class);
        }
        return getUniqueId(resource.getPath(), false);
    }

    @Override
    public void tagResource(Resource resource) {
        if (Boolean.TRUE.equals(resourceFilter.apply(resource))) {
            String pageHash = getCurrentPageId(resource, true);
            if (needsUpdate(resource, pageHash)) {
                tagResource(resource, pageHash);
            }
        }
    }

    void tagResource(ResourceResolver resolver, String path) {
        tagResource(resolver.getResource(path));
    }

    /**
     * Tags a given resource with both current page id, and component id
     *
     * @param resource resource we want to tag
     * @param currentPageHash hash from page whom we are browsing the content from
     */
    void tagResource(Resource resource, String currentPageHash) {
        String existingId = resource.getValueMap().get(PN_COMPID, String.class);
        String compid = StringUtils.isBlank(existingId) || configuration.shouldRewriteComponentHash() ?
            getUniqueId(resource.getPath(), true) : existingId;
        tagResource(resource, currentPageHash, compid);
    }

    /**
     * Tags a given resource with both current page id, and provided component id
     *
     * @param resource
     * @param currentPageHash
     * @param compHash in case this is null, it's not updated
     */
    void tagResource(Resource resource, String currentPageHash, @Nullable String compHash) {
        ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
        if (map != null) {
            map.put(PN_PAGEHASH, currentPageHash);
            map.put(PN_COMPID, compHash);
        }
    }

    /**
     * Creates a Unique ID based on a path.
     *
     * @param path          The path we should generate an ID from
     * @param timeSalt      wether we should salt it with a time based salt or not
     * @return              The unique ID (first <code>ID_SIZE</code> chars of sha1 hash)
     */
    public String getUniqueId(final String path, boolean timeSalt) {
        String source = path;
        if (timeSalt) {
            source += Calendar.getInstance().toString();
        }
        String sha256 = DigestUtils.sha256Hex(source);
        return sha256.substring(0, ID_SIZE);
    }

    @Override
    public void process(SlingHttpServletRequest request, List<Modification> list) {
        if (configuration.tagOnModification()) {
            for (Modification modification : list) {
                log.trace("{}", modification);
                ResourceResolver resolver = request.getResourceResolver();
                if (ModificationType.CREATE.equals(modification.getType())) {
                    tagResource(resolver, modification.getSource());
                } else if (ModificationType.COPY.equals(modification.getType())) {
                    Resource resource = resolver.getResource(modification.getDestination());
                    //removing old tags
                    tagResource(resource, EMPTY, EMPTY);
                    tagResource(resolver, modification.getDestination());
                }
            }
        }
    }

    boolean isCurrentRequestForRootPage(SlingHttpServletRequest request, String pageId) {
        return pageId.equals(request.getAttribute(ATT_ROOTID));
    }

    /**
     * Prefix a given component id with an eventual reference
     * @param request current request
     * @param pageId current page hash
     * @param componentId component hash
     * @return (<XF reference component Hash>-)?<component Hash> prefix only if we are in the XF context and if
     * we are not in the page root already
     */
    String prefixWithRootReference(SlingHttpServletRequest request, String pageId, String componentId) {
        if (!isCurrentRequestForRootPage(request, pageId) && request.getAttribute(ATT_REFERENCE) != null) {
            return request.getAttribute(ATT_REFERENCE) + ID_SEPARATOR + componentId;
        }
        return componentId;
    }

    /**
     * checks if current component request has an override set for the ID
     * @param request current request
     * @param pageId hash of the page path
     * @param property override property name
     * @return
     */
    String getOverride(SlingHttpServletRequest request, String pageId, String property) {
        if (StringUtils.isNotBlank(property)) {
            String override = request.getResource().getValueMap().get(property, String.class);
            if (StringUtils.isNotBlank(override)) {
                return prefixWithRootReference(request, pageId, override);
            }
        }
        return null;
    }

    @Override
    public String computeComponentId(SlingHttpServletRequest request, @Nullable String property) {
        Resource resource = request.getResource();
        String pageId = getCurrentPageId(resource, true);
        if (request.getAttribute(ATT_ROOTID) == null) {
            request.setAttribute(ATT_ROOTID, pageId);
        }
        //first we check if an override exist
        String id = getOverride(request, pageId, property);
        if (StringUtils.isBlank(id)) {
            //the we go for default id tagging
            id = prefixWithRootReference(request, pageId, getResourceId(resource, true));
        }
        if (isCurrentRequestForRootPage(request, pageId)) {
            String refId = null;
            if (referenceTypes.contains(resource.getResourceType())) {
                refId = id;
            }
            request.setAttribute(ATT_REFERENCE, refId);
        }
        return id;
    }

    /**
     * lists all resource in a resource tree that have resource type corresponding to passed patterns
     */
    static class ComponentIterator extends AbstractResourceVisitor implements Iterator<Resource> {

        Function<Resource, Boolean> rFilter;
        Collection<Resource> internalList = new ArrayList<>();
        Iterator<Resource> internalIT;

        public ComponentIterator(Function<Resource, Boolean> filter, Resource root) {
            rFilter = filter;
            accept(root);
        }

        @Override
        protected void visit(@NotNull Resource resource) {
            if (Boolean.TRUE.equals(rFilter.apply(resource))) {
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
            name = "Tag on publication",
            description = "should tag components on publication"
        )
        boolean tagOnPublication();

        @AttributeDefinition(
            name = "Tag on creation or copy",
            description = "should tag components on creation or copy"
        )
        boolean tagOnModification();

        @AttributeDefinition(
            name = "Accepted types",
            description = "list of pattern (https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html) "
                + "for resource types that should be tagged with an id"
        )
        @SuppressWarnings("squid:S00100")
        String[] acceptedTypes() default { "dx/structure/components/.*" };

        @AttributeDefinition(
            name = "Reference type",
            description = "type with which a reference is made, we should consider for ID generation"
        )
        String[] referenceTypes() default { "cq/experience-fragments/editor/components/experiencefragment" };

        @AttributeDefinition(
            name = "Should rewrite hash over copies",
            description = "in anyway, a copy of the component in a new page will have rewritten page hash."
            + "in case this is checked, the component hash will be rewritten as well"
        )
        boolean shouldRewriteComponentHash() default true;
    }
}
