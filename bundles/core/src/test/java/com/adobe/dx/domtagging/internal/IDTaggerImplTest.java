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

import static com.adobe.dx.domtagging.internal.IDTaggerImpl.PN_COMPID;
import static com.adobe.dx.domtagging.internal.IDTaggerImpl.PN_PAGEHASH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.adobe.dx.domtagging.IDTagger;
import com.adobe.dx.testing.AbstractTest;
import com.adobe.dx.utils.TypeFilter;
import com.adobe.dx.utils.TypeIterator;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.IteratorUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.ModificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IDTaggerImplTest extends AbstractTest {

    private static final String REL_ROOT = "/jcr:content/root";
    private static final String IMAGE_PATH = CONTENT_ROOT + REL_ROOT + "/children/image";

    IDTaggerImpl tagger;

    List<String> expected = Arrays.asList(
        CONTENT_ROOT + REL_ROOT,
        IMAGE_PATH,
        CONTENT_ROOT + REL_ROOT + "/children/unwantedContainer/image");

    void registerTagger(boolean rewrite, boolean onModification, boolean onActivation) {
        context.registerInjectActivateService((IDTagger)tagger, "acceptedTypes",
            new String[] {"some/dx/.*","some/other/.*"},
            "shouldRewriteComponentHash", rewrite,
            "referenceTypes", new String[] { "some/dx/reference" },
            "tagOnModification", onModification,
            "tagOnPublication", onActivation);
    }

    ReplicationAction mockReplication(ReplicationActionType type, String path) {
        ReplicationAction action = mock(ReplicationAction.class);
        when(action.getType()).thenReturn(type);
        when(action.getPath()).thenReturn(path);
        return action;
    }

    ReplicationAction mockActivation(String path) {
        return mockReplication(ReplicationActionType.ACTIVATE, path);
    }

    void mockCreationPost(String path) {
        Modification modification = mock(Modification.class);
        when(modification.getType()).thenReturn(ModificationType.CREATE);
        when(modification.getSource()).thenReturn(path);
        tagger.process(context.request(), Collections.singletonList(modification));
    }

    void mockCopyPost(String path) {
        Modification modification = mock(Modification.class);
        when(modification.getType()).thenReturn(ModificationType.COPY);
        when(modification.getDestination()).thenReturn(path);
        tagger.process(context.request(), Collections.singletonList(modification));
    }

    void assertTagged(boolean tagged, String path) {
        ValueMap comp  = getVM(path);
        assertEquals(tagged, comp.get(PN_PAGEHASH, String.class) != null, "there should " +
            (tagged ? "": "not") + " be a page hash for " + path);
        assertEquals(tagged, comp.get(PN_COMPID, String.class) != null, "there should" +
            (tagged ? "": " not") + " be a component hash for " + path);
    }

    @BeforeEach
    public void setup() {
        context.load().json("/mocks/domtagging.internal/page-tree.json", CONTENT_ROOT);
        tagger = new IDTaggerImpl();
        registerTagger(true, true, true);
    }

    @Test
    public void testComponentIterator() {
        TypeFilter typeFilter = new TypeFilter(new String[]{"some/dx/.*", "some/other/.*"});
        TypeIterator iterator = new TypeIterator(typeFilter,context.resourceResolver().getResource(CONTENT_ROOT + "/jcr:content"));
        List<String> result = (List<String>) IteratorUtils.toList(iterator).stream()
            .map(r -> ((Resource)r).getPath())
            .collect(Collectors.toList());
        assertEquals(expected, result, "Configured resource types (and only them) should have been grabbed");
    }

    @DisplayName("Depending on configuration, untagged resources should be tagged on activation")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testTagOnActivation(boolean activeOnPublication) throws Exception {
        registerTagger(false, false, activeOnPublication);
        tagger.preprocess(mockActivation(CONTENT_ROOT), null);
        for (String path : expected) {
            assertTagged(activeOnPublication, path);
        }
    }

    @DisplayName("computeComponentId should prefix ids with component types")
    @ParameterizedTest
    @ValueSource(strings = {
        "viewedPage/jcr:content/root/prefix/image,dx/component/image,image-",
        "viewedPage/jcr:content/root/prefix/text,dx/component/text,text-"})
    public void testPrefix(String input){
        registerTagger(false, false, false);
        String[] parameters = input.split(",");
        String path = CONTENT_ROOT + "/" + parameters[0];
        context.create().resource(path, "sling:resourceType", parameters[1]);
        context.currentResource(path);
        String id = tagger.computeComponentId(context.request(), null);
        assertTrue(id.startsWith(parameters[2]), "id " + id + " of type " + parameters[1] + " should be prefixed by " + parameters[2]);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testMovedTagging(boolean rewriteComponentHash) throws Exception {
        registerTagger(rewriteComponentHash, true, true);
        tagger.preprocess(mockActivation(CONTENT_ROOT), null);
        Map<String, String> oldHashes = new HashMap<>();
        //collecting old hashes
        for (String path : expected) {
            ValueMap comp = getVM(path);
            String hash = comp.get(PN_COMPID, String.class);
            assertNotNull(hash);
            oldHashes.put(path, hash);
        }
        //adding a component with bad hashes
        String copiedComponent = CONTENT_ROOT + "/jcr:content/root/moved";
        String oldPageHash = "somehash";
        String oldHash = "blahblah";
        context.build().resource(copiedComponent,
            "sling:resourceType", "some/dx/comp",
            PN_COMPID, oldHash,
            PN_PAGEHASH, oldPageHash).commit();
        for (String path : expected) {
            ValueMap comp = getVM(path);
            assertEquals(oldHashes.get(path), comp.get(PN_COMPID, String.class),"old hashes should still be untouched");
        }
        tagger.preprocess(mockActivation(CONTENT_ROOT), null);
        ValueMap comp = getVM(copiedComponent);
        String pageHash = comp.get(PN_PAGEHASH, String.class);
        String newHash = comp.get(PN_COMPID, String.class);
        assertFalse(oldPageHash.equals(pageHash), "page hash should have been updated");
        assertEquals(!rewriteComponentHash, oldHash.equals(newHash), "comp hash should" + (rewriteComponentHash ? "" : " not") + " have been updated");
    }

    @DisplayName("computeComponentId should works with override, XF context, no default id set")
    @ParameterizedTest
    @ValueSource(strings = {
        "viewedPage/jcr:content/root/children/ref1,ref1",
        "viewedPage/jcr:content/root/children/image,blueskywithbirds",
        "viewedPage/jcr:content/root/children/ref2,ref2",
        "somereferredPage/jcr:content/root/children/image,ref1-refimageid,viewedPage/jcr:content/root/children/ref1",
        "somereferredPage/jcr:content/root/children/image,ref1-refimageid,viewedPage/jcr:content/root/children/ref1,somereferredPage/jcr:content/root/children/anotherref",
        "somereferredPage/jcr:content/root/children/image,ref2-refimageid,viewedPage/jcr:content/root/children/ref2",
        "viewedPage/jcr:content/root/children/text,NON_NULL",
        "viewedPage/jcr:content/root/children/image,blueskywithbirds,viewedPage/jcr:content/root/children/ref1",
        "viewedPage/jcr:content/root/children/image,anotherref-blueskywithbirds,somereferredPage/jcr:content/root/children/anotherref",
    })
    /**
     * @param input format as follows
     *  <code><PATH of the required resource id>,<Expected id>(,<previously viewed path1>,<previously viewed path2>,...</code>
     *  if expected is NON_NULL, then we just check non nullity of the value
     */
    void testGetId(String input) {
        //typical publish configuration
        registerTagger(false, false, false);
        final String PROPERTY = "myOwnProperty";
        String[] inputs = input.split(",");
        String path = CONTENT_ROOT + "/" + inputs[0];
        if (inputs.length > 2) {
            for (int i = 2; i < inputs.length; i++) {
                String contextPath = CONTENT_ROOT + "/" + inputs[i];
                Resource resource = context.resourceResolver().getResource(contextPath);
                if (resource == null) {
                    throw new IllegalArgumentException("something is wrong with the resource mock " + path);
                }
                context.request().setResource(resource);
                tagger.computeComponentId(context.request(), PROPERTY);
            }
        }
        Resource resource = context.resourceResolver().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("something is wrong with the resource mock " + path);
        }
        context.request().setResource(resource);
        String value = tagger.computeComponentId(context.request(), PROPERTY);
        String expected = inputs[1];
        if ("NON_NULL".equals(expected)) {
            assertNotNull(value, "id for " + path + " should not be null");
        } else {
            assertEquals(expected, value, "Value for " + path + " should equal " + expected);
        }
    }

    @DisplayName("on creation modification, there should be a tag depending on configuration")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testCreation(boolean onModification) {
        registerTagger(false, onModification, false);
        String path = CONTENT_ROOT + REL_ROOT;
        mockCreationPost(path);
        assertTagged(onModification, path);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testCopy(boolean onModification) {
        //we need a first tag to test it
        registerTagger(true, true, false);
        mockCreationPost(IMAGE_PATH);
        String copyPath = IMAGE_PATH + "copy";
        context.build().resource(copyPath, getVM(IMAGE_PATH));
        String oldId = getVM(copyPath).get(PN_COMPID, String.class);
        //this time we re-register with variable onModification
        registerTagger(true, onModification, false);
        mockCopyPost(copyPath);
        String newId = getVM(copyPath).get(PN_COMPID, String.class);
        assertEquals(onModification, !oldId.equals(newId), "a new id should have been created");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "/content/not/existing",
        CONTENT_ROOT + "/someRandomFolder",
        CONTENT_ROOT + "/someRandomAsset"
    })
    public void assertOtherActivationStayUntagged(String path) throws Exception {
        registerTagger(true, true, true);
        Map oldMap = getVM(path);
        tagger.preprocess(mockActivation(path), null);
        Map newMap = getVM(path);
        assertEquals(oldMap, newMap, "should be left untouched");
    }

    @Test
    public void testNonActivationReplication() throws Exception {
        registerTagger(true, true, true);
        tagger.preprocess(mockReplication(ReplicationActionType.DELETE, "/content/not/existing/anymore"), null);
        //just checking everything ran well
    }
}