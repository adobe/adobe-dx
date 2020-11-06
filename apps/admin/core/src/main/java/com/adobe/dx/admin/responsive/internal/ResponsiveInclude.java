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
package com.adobe.dx.admin.responsive.internal;

import static org.apache.jackrabbit.JcrConstants.JCR_LASTMODIFIED;
import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;
import static org.apache.sling.api.resource.observation.ResourceChangeListener.PATHS;
import static org.apache.sling.api.servlets.HttpConstants.METHOD_GET;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES;
import static org.apache.sling.servlets.post.SlingPostConstants.TYPE_HINT_SUFFIX;

import com.adobe.dx.responsive.Breakpoint;
import com.adobe.dx.responsive.ResponsiveConfiguration;
import com.adobe.dx.utils.TypeFilter;
import com.adobe.dx.utils.TypeIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {Servlet.class, ResourceChangeListener.class}, configurationPolicy = ConfigurationPolicy.REQUIRE,
    property = {
        PATHS + "=/apps",
        PATHS + "=/conf",
        SLING_SERVLET_RESOURCE_TYPES + "=" + ResponsiveInclude.RESOURCE_TYPE,
        SLING_SERVLET_METHODS + "=" + METHOD_GET})
public class ResponsiveInclude extends SlingSafeMethodsServlet implements ResourceChangeListener {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String RESOURCE_TYPE = "dx/admin/responsive/include";
    private static final String PN_TYPE = "resourceType";
    private static final String PN_LOOP = "dxResponsiveItem";
    private static final String PN_FOLLOW = "dxResponsiveFollow";
    private static final String PN_NAME = "name";
    private static final String PN_TITLE = "jcr:title";
    private static final String PN_PATH = "path";
    private static final String PN_INVALID = "invalid";
    private static final String SLING_FOLDER = "sling:Folder";
    private static final String PN_RESOURCE_TYPE = "sling:" + PN_TYPE;
    private static final String MNT_PREFIX = "/mnt/override";
    private static final String CONTENT_ROOT = "/content";
    private static final String APPS_PREFIX = "/apps/";
    private static final String SLASH = "/";

    Configuration configuration;

    @Reference
    ResourceResolverFactory factory;

    private static final String ADMIN_SERVICE = "adminService";

    private static final Map<String, Object> ADMIN_SERVICE_ACCOUNT =
        Collections.singletonMap(SUBSERVICE, ADMIN_SERVICE);

    @Activate
    public void activate(Configuration configuration) {
        this.configuration = configuration;
        try (ResourceResolver resolver = factory.getServiceResourceResolver(ADMIN_SERVICE_ACCOUNT)) {
            scanRoots(resolver);
        } catch (LoginException e) {
            logger.error("unable to scan roots", e);
        }
    }

    private void markInvalidation(Collection<String> paths) {
        try (ResourceResolver resolver = factory.getServiceResourceResolver(ADMIN_SERVICE_ACCOUNT)) {
            for (String path : paths) {
                Resource resource = resolver.getResource(path);
                ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
                map.put(PN_INVALID, true);
            }
            resolver.commit();
        } catch (LoginException | PersistenceException e) {
            logger.error("unable to properly mark {}", paths, e);
        }
    }

    private class Context implements AutoCloseable {
        ResourceResolver resolver;
        Collection<String> paths;

        Collection<String> getPaths() {
            return paths;
        }

        void addPath(String path) {
            if (paths == null) {
                paths = new ArrayList<>();
            }
            paths.add(path);
        }

        ResourceResolver getResolver() throws LoginException {
            if (resolver == null) {
                resolver = factory.getServiceResourceResolver(ADMIN_SERVICE_ACCOUNT);
            }
            return resolver;
        }

        @Override
        public void close() {
            if (resolver != null) {
                resolver.close();
            }
        }
    }

    /**
     * @param path change path
     * @return responsive include containing the change if any
     */
    String handleDialogPath(ResourceResolver resolver, String path) {
        Resource resource = resolver.getResource(path);
        while (path.length() > 0) {
            if (resource != null && resource.isResourceType(RESOURCE_TYPE)) {
                break;
            }
            if (resource != null) {
                resource = resource.getParent();
                path = resource != null ? resource.getPath() : StringUtils.EMPTY;
            } else {
                path = StringUtils.substringBeforeLast(path, SLASH);
                resource = resolver.getResource(path);
            }
        }
        if (resource != null) {
            return resource.getPath();
        }
        return null;
    }

    void fillPathFromConfChange(Context ctx) throws LoginException {
        applyToAllDialog(ctx.getResolver(), r -> ctx.addPath(r.getPath()));
    }

    void fillPathFromDialogChange(Context ctx, String path) throws LoginException {
        if (StringUtils.startsWithAny(path, configuration.dialogRoots())) {
            String ref = handleDialogPath(ctx.getResolver(), path);
            if (StringUtils.isNotBlank(ref)) {
                ctx.addPath(configuration.cacheRoot() + ref);
            }
        }
    }

    @Override
    public void onChange(@NotNull List<ResourceChange> list) {
        if (configuration.appChange()) {
            try (Context ctx = new Context()) {
                for (ResourceChange change : list) {
                    if (change.getPath().contains(ResponsiveConfiguration.class.getName())) {
                        fillPathFromConfChange(ctx);
                        break;
                    } else {
                        fillPathFromDialogChange(ctx, change.getPath());
                    }
                }
                if (ctx.getPaths() != null) {
                    markInvalidation(ctx.getPaths());
                }
            } catch (Exception e) {
                logger.error("unable to login with admin session", e);
            }
        }
    }

    void applyToAllDialog(final ResourceResolver resolver, Consumer<Resource> function) {
        TypeFilter filter = new TypeFilter(new String[] {RESOURCE_TYPE});
        for (String root : configuration.dialogRoots()) {
            for (TypeIterator children = new TypeIterator(filter, resolver.getResource(root)); children.hasNext();) {
                function.accept(children.next());
            }
        }
    }

    void scanRoots(ResourceResolver resolver) {
        final Breakpoint[] breakpoints = getBreakpoints(resolver.getResource(CONTENT_ROOT));
        applyToAllDialog(resolver, r -> {
            try {
                getIncludeResource(breakpoints, r);
            } catch (RepositoryException e) {
                logger.error("unable to properly build {}", r);
            }
        });
    }

    /**
     * Write properties from the configuration to the target resource,
     * instantiating both property names & values
     *
     * @param conf configured resource that holds all properties to write (and subpipes)
     * @param target target resource on which configured values will be written
     */
    private void copyProperties(@Nullable Resource conf, Resource target, Breakpoint breakpoint)  {
        ValueMap writeMap = conf != null ? conf.adaptTo(ValueMap.class) : null;
        ModifiableValueMap properties = target.adaptTo(ModifiableValueMap.class);

        //writing current node
        if (properties != null && writeMap != null) {
            for (Map.Entry<String, Object> entry : writeMap.entrySet()) {
                Object value = entry.getValue();
                if (breakpoint != null && PN_NAME.equals(entry.getKey())) {
                    String confValue = entry.getValue().toString();
                    value = confValue.endsWith(TYPE_HINT_SUFFIX) ?
                        StringUtils.substringBefore(confValue, TYPE_HINT_SUFFIX) + breakpoint.propertySuffix() + TYPE_HINT_SUFFIX :
                        confValue + breakpoint.propertySuffix();
                }
                properties.put(entry.getKey(), value);
            }
        }
    }

    Breakpoint[] getBreakpoints(Resource contentResource) {
        if (contentResource != null) {
            return contentResource.adaptTo(ConfigurationBuilder.class)
                .as(ResponsiveConfiguration.class).breakpoints();
        }
        return new Breakpoint[0];
    }

    Breakpoint[] getBreakpoints(SlingHttpServletRequest request) {
        ResourceResolver resolver = request.getResourceResolver();
        String resourcePath = request.getRequestPathInfo().getSuffix();
        return getBreakpoints(resolver.getResource(resourcePath));
    }

    /**
     * loop over breakpoints and write current tree that many times
     * @param breakpoints
     * @param loop
     * @param target
     * @throws RepositoryException
     */
    void loopTree(Breakpoint[] breakpoints, Node loop, Resource target) throws RepositoryException {
        Node targetNode = target.adaptTo(Node.class);
        for (Breakpoint breakpoint : breakpoints) {
            Node child = targetNode.addNode(loop.getName() + breakpoint.propertySuffix(), loop.getPrimaryNodeType().getName());
            child.setProperty(PN_TITLE, breakpoint.label());
            logger.debug("writing responsive tree {}", child.getPath());
            copyTree(breakpoints, loop, target.getResourceResolver().getResource(child.getPath()), breakpoint);
        }
    }
    
    void handleChildCopy(Breakpoint[] breakpoints, Breakpoint currentBreakpoint, Node child, Resource target, Node targetNode) throws RepositoryException {
        String name = child.getName();
        if (currentBreakpoint == null && child.hasProperty(PN_LOOP)) {
            loopTree(breakpoints, child, target);
        } else if (currentBreakpoint != null && child.hasProperty(PN_FOLLOW) && child.hasProperty(PN_PATH)) {
            String path = child.getProperty(PN_PATH).getString();
            path = path.startsWith(SLASH) ? path : APPS_PREFIX + path;
            Node followedChild = child.getSession().getNode(path);
            copyTree(breakpoints, followedChild, target, currentBreakpoint);
        } else {
            Node childTarget = targetNode.hasNode(name) ? targetNode.getNode(name) : targetNode.addNode(name, child.getPrimaryNodeType().getName());
            logger.debug("writing tree {}", childTarget.getPath());
            copyTree(breakpoints, child, target.getResourceResolver().getResource(childTarget.getPath()), currentBreakpoint);
        }
    }

    /**
     * @param referrer source JCR tree to dub to the cache
     * @param target target resource to write
     * @param breakpoint current breakpoint under which this tree is written
     */
    void copyTree(Breakpoint[] breakpoints, Node referrer, Resource target, Breakpoint breakpoint) throws RepositoryException {
        ResourceResolver resolver = target.getResourceResolver();
        copyProperties(resolver.getResource(referrer.getPath()), target, breakpoint);
        NodeIterator children = referrer.getNodes();
        if (children.hasNext()){
            Node targetNode = target.adaptTo(Node.class);
            if (targetNode != null) {
                logger.debug("dubbing {} at {}", referrer.getPath(), target.getPath());
                while (children.hasNext()) {
                    handleChildCopy(breakpoints, breakpoint,  children.nextNode(), target, targetNode);
                }
            }
        }
    }

    Resource buildInclude(Breakpoint[] breakpoints, Resource resource) {
        try (ResourceResolver writeResolver = factory.getServiceResourceResolver(ADMIN_SERVICE_ACCOUNT)) {
            String path = getIncludePath(resource);
            Resource referrer = resource.getResourceResolver().getResource(getRawPath(resource));
            Map<String, Object> properties = new HashMap<>();
            properties.put(JCR_LASTMODIFIED, new Date());
            Resource target = ResourceUtil.getOrCreateResource(writeResolver, path, properties, SLING_FOLDER,false);
            copyTree(breakpoints, referrer.adaptTo(Node.class), target , null);
            String targetType = referrer.getValueMap().get(PN_TYPE, String.class);
            if (targetType != null) {
                target.adaptTo(ModifiableValueMap.class).put(PN_RESOURCE_TYPE, targetType);
            }
            writeResolver.commit();
            return resource.getResourceResolver().getResource(path);
        } catch (LoginException e) {
            logger.error("unable to login for a write session", e);
        } catch (RepositoryException e) {
            logger.error("JCR error while copying the tree", e);
        } catch (PersistenceException e) {
            logger.error("unable to write the cache", e);
        }
        return null;
    }

    boolean isValidInclude(Resource resource) {
        if (resource == null) {
            return false;
        }
        while (resource != null && !resource.getValueMap().get(PN_INVALID, false)) {
            resource = resource.getParent();
        }
        return resource == null;
    }

    String getRawPath(Resource resource) {
        return resource.getPath().startsWith(MNT_PREFIX) ? StringUtils.substringAfter(resource.getPath(), MNT_PREFIX) : resource.getPath();
    }

    String getIncludePath(Resource resource) {
        return configuration.cacheRoot() + getRawPath(resource);
    }

    Resource getIncludeResource(Breakpoint[] breakpoints, Resource resource) throws RepositoryException {
        String includePath = getIncludePath(resource);
        Resource includeResource = resource.getResourceResolver().getResource(includePath);
        if (isValidInclude(includeResource)) {
            return includeResource;
        } else if (includeResource != null) {
            includeResource.adaptTo(Node.class).remove();
            resource.getResourceResolver().adaptTo(Session.class).save();
        }
        return buildInclude(breakpoints, resource);
    }

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            Resource targetResource = getIncludeResource(getBreakpoints(request), request.getResource());
            if (targetResource != null) {
                IncludeHelper referrer = new IncludeHelper(request, targetResource);
                response.getWriter().print(referrer.include());
            }
        } catch (RepositoryException e) {
            throw new ServletException(e);
        }
    }

    @ObjectClassDefinition(name = "Adobe DX Admin Responsive Include")
    public @interface Configuration {

        @AttributeDefinition(name = "Dialog roots", description = "only responsive includes under those roots will be inspected"
        )
        String[] dialogRoots() default { "/apps/dx" };

        @AttributeDefinition(name = "Root of where responsive include are stored",
            description = "should tag components on creation or copy"
        )
        String cacheRoot() default "/var/dx/admin/responsiveinclude";

        @AttributeDefinition(name = "Listens to app changes",
            description = "should listens to app or conf changes, handy on local dev, but mostly useless in higher environments"
        )
        boolean appChange() default false;
    }
}
