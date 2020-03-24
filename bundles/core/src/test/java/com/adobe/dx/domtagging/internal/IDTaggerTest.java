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

import static com.adobe.dx.domtagging.internal.IDTagger.PN_COMPID;
import static com.adobe.dx.domtagging.internal.IDTagger.PN_PAGEHASH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.adobe.dx.testing.AbstractTest;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.IteratorUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Test;

class IDTaggerTest extends AbstractTest {

    private static final String REL_ROOT = "/jcr:content/root";

    List<String> expected = Arrays.asList(
        CONTENT_ROOT + REL_ROOT,
        CONTENT_ROOT + REL_ROOT + "/children/image",
        CONTENT_ROOT + REL_ROOT + "/children/unwantedContainer/image");

    @Test
    public void testComponentIterator() {
        context.load().json("/mocks/domtagging.internal/page-tree.json", CONTENT_ROOT);
        Pattern dx = Pattern.compile("some/dx/.*");
        Pattern other = Pattern.compile("some/other/.*");
        IDTagger.ComponentIterator iterator = new IDTagger.ComponentIterator(Arrays.asList(dx, other),
            context.resourceResolver().getResource(CONTENT_ROOT + "/jcr:content"));
        List<String> result = (List<String>) IteratorUtils.toList(iterator).stream()
            .map(r -> ((Resource)r).getPath())
            .collect(Collectors.toList());
        assertEquals(expected, result, "Configured resource types (and only them) should have been grabbed");
    }

    @Test
    public void testFirstTagging() throws Exception {
        firstTag();
        for (String path : expected) {
            ValueMap comp  = getVM(path);
            assertNotNull(comp.get(PN_PAGEHASH, String.class), "there should be a page hash for " + path);
            assertNotNull(comp.get(PN_COMPID, String.class), "there should be a component hash for " + path);
        }
    }

    @Test
    public void testMovedTagging() throws Exception {
        IDTagger tagger = firstTag();
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
        tagger.preprocess(mockContentRootActivation(), null);
        ValueMap comp = getVM(copiedComponent);
        String pageHash = comp.get(PN_PAGEHASH, String.class);
        String newHash = comp.get(PN_COMPID, String.class);
        assertFalse(oldPageHash.equals(pageHash), "page hash should have been updated");
        assertFalse(oldHash.equals(newHash), "comp hash should have been updated");
    }

    @Test
    public void testNonPage() throws Exception {
        context.build().resource(CONTENT_ROOT, "blah", "blah").commit();
        IDTagger tagger = mockTagger();
        Map oldMap = getVM(CONTENT_ROOT);
        tagger.preprocess(mockContentRootActivation(), null);
        Map newMap = getVM(CONTENT_ROOT);
        assertEquals(oldMap, newMap, "should be left untouched");
    }

    IDTagger mockTagger() throws ReplicationException, LoginException {
        IDTagger tagger = new IDTagger();
        ResourceResolverFactory factory = mock(ResourceResolverFactory.class);
        when(factory.getServiceResourceResolver(any())).thenReturn(context.resourceResolver());
        tagger.resourceResolverFactory = factory;
        context.registerInjectActivateService(tagger, "acceptedTypes",
            new String[] {"some/dx/.*","some/other/.*"});
        return tagger;
    }

    IDTagger firstTag() throws ReplicationException, LoginException {
        context.load().json("/mocks/domtagging.internal/page-tree.json", CONTENT_ROOT);
        IDTagger tagger = mockTagger();
        tagger.preprocess(mockContentRootActivation() , null);
        return tagger;
    }

    ReplicationAction mockContentRootActivation() {
        ReplicationAction action = mock(ReplicationAction.class);
        when(action.getType()).thenReturn(ReplicationActionType.ACTIVATE);
        when(action.getPath()).thenReturn(CONTENT_ROOT);
        return action;
    }
}