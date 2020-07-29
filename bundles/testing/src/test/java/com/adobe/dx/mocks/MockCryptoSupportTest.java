package com.adobe.dx.mocks;

import static org.junit.jupiter.api.Assertions.*;

import com.adobe.granite.crypto.CryptoException;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MockCryptoSupportTest {

    MockCryptoSupport cryptoSupport;

    @BeforeEach
    public void setup() {
        cryptoSupport = new MockCryptoSupport();
    }

    @Test
    void setException() throws CryptoException {
        cryptoSupport.protect("blah");
        cryptoSupport.setException();
        assertThrows(CryptoException.class, () -> cryptoSupport.protect("blah"));
    }

    @Test
    void isProtected() {
        assertTrue(cryptoSupport.isProtected("protected_foo"));
        assertFalse(cryptoSupport.isProtected("foo"));
    }

    @Test
    void protect() throws CryptoException {
        assertEquals("protected_foo", cryptoSupport.protect("foo"));
        assertThrows(CryptoException.class, () -> { cryptoSupport.setException();cryptoSupport.protect("foo");});
    }

    @Test
    void unprotect() throws CryptoException {
        assertEquals("foo", cryptoSupport.unprotect("protected_foo"));
        assertThrows(CryptoException.class, () -> { cryptoSupport.setException();cryptoSupport.unprotect("foo");});
    }

    @Test
    void testUnsupportedOperations() {
        final Collection<Callable> unsupportedOperations = Arrays.asList(
            () -> cryptoSupport.encrypt(null),
            () -> cryptoSupport.encrypt(null, null),
            () -> cryptoSupport.decrypt(null),
            () -> cryptoSupport.decrypt(null, null),
            () -> cryptoSupport.protect(null, null),
            () -> cryptoSupport.unprotect(null, null),
            () -> cryptoSupport.wrapKey(null),
            () -> cryptoSupport.wrapKey(null, null),
            () -> cryptoSupport.unwrapKey(null),
            () -> cryptoSupport.unwrapKey(null, null),
            () -> {cryptoSupport.nextRandomBytes(null);return true;},
            () -> cryptoSupport.hmac_sha256(null),
            () -> cryptoSupport.hmac_sha256(null,null),
            () -> cryptoSupport.createKeyPair(null),
            () -> cryptoSupport.sign(null, null, null),
            () -> cryptoSupport.sign(null, null, null, 0L, 0L),
            () -> cryptoSupport.verify(null, null, null, null));
        for (Callable callable : unsupportedOperations) {
            assertThrows(UnsupportedOperationException.class, () -> callable.call());
        }
    }
}