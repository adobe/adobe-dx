package com.adobe.dx.structure.flex;

import static org.junit.jupiter.api.Assertions.*;

import com.adobe.dx.testing.AbstractRequestModelTest;

import org.apache.abdera.protocol.util.AbstractRequest;
import org.junit.jupiter.api.Test;

class FlexModelTest extends AbstractRequestModelTest {

    @Test
    void getHello() throws ReflectiveOperationException {
        assertEquals("Hello", getModel(FlexModel.class).getHello());
    }
}