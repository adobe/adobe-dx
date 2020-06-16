/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.adobe.dx.mocks;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.lang3.StringUtils;

public class MockCryptoSupport implements CryptoSupport {

    private static final String NOT_IMPLEMENTED = "Not implemented";
    private static final String PROTECTED_PREFIX = "protected_";

    private boolean throwException  = false;

    public void setException() {
        throwException = true;
    }

    @Override
    public byte[] encrypt(byte[] bytes) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public byte[] decrypt(byte[] bytes) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public byte[] encrypt(byte[] bytes, byte[] bytes1) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public byte[] decrypt(byte[] bytes, byte[] bytes1) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean isProtected(String s) {
        return StringUtils.startsWith(s, PROTECTED_PREFIX);
    }

    @Override
    public String protect(String s) throws CryptoException {
        if (throwException) {
            throw new CryptoException("Crypto Exception");
        }
        return PROTECTED_PREFIX + s;
    }

    @Override
    public String unprotect(String s) throws CryptoException {
        if (throwException) {
            throw new CryptoException("Crypto Exception");
        }
        return StringUtils.replace(s, PROTECTED_PREFIX, StringUtils.EMPTY);
    }

    @Override
    public String protect(byte[] bytes, String s) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public String unprotect(byte[] bytes, String s) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public byte[] wrapKey(byte[] bytes, byte[] bytes1) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public byte[] wrapKey(byte[] bytes) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public byte[] unwrapKey(byte[] bytes, byte[] bytes1) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public byte[] unwrapKey(byte[] bytes) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void nextRandomBytes(byte[] bytes) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public byte[] hmac_sha256(byte[] bytes, byte[] bytes1) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public byte[] hmac_sha256(byte[] bytes) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public KeyPair createKeyPair(String s) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Certificate sign(Certificate certificate, KeyPair keyPair, X500Principal x500Principal, long l, long l1)
        throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public byte[] sign(byte[] bytes, PrivateKey privateKey, String s) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean verify(byte[] bytes, byte[] bytes1, PublicKey publicKey, String s) throws CryptoException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }
}
