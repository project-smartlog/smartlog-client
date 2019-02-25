/*
 * Copyright 2016-2019
 *
 * Interreg Central Baltic 2014-2020 funded project
 * Smart Logistics and Freight Villages Initiative, CB426
 *
 * Kouvola Innovation Oy, FINLAND
 * Region Örebro County, SWEDEN
 * Tallinn University of Technology, ESTONIA
 * Foundation Valga County Development Agency, ESTONIA
 * Transport and Telecommunication Institute, LATVIA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.propentus.smartlog.blockchain

import com.propentus.smartlog.security.AesCryptoHandler
import com.propentus.smartlog.security.CryptoUtil
import com.propentus.smartlog.security.RsaCryptoHandler

import java.security.KeyPair

class CryptoController {

    def index() { }

    def generateAesKey() {
        String key = AesCryptoHandler.generateRandomKey()
        render "KEY: <br>" + key
    }

    def decryptAes() {
        String key = params.key
        String message = params.message

        try {
        String plainText = AesCryptoHandler.decrypt(key, message)
        render "Decrypted message: " + plainText
        }
        catch (Exception e) {
            e.printStackTrace();
            render "Error encrypting the message"
        }
    }

    def encryptAes() {

        try {
            String message = params.message
            AesCryptoHandler aes = new AesCryptoHandler(message)
            String encrypted = aes.encrypt()
            render "Encrypted message: " + encrypted
            render "<br>"
            render "Encryption key: " + aes.getKey()
        }
        catch (Exception e) {
            e.printStackTrace();
            render "Error encrypting the message"
        }
    }

    /**
     * Encrypt message with AES-algorithm with given AES-key
     * @return
     */
    def encryptAesWithKey() {

        try {
            String message = params.message
            String key = params.key
            AesCryptoHandler aes = new AesCryptoHandler(message)
            String encrypted = aes.encrypt(key)
            render "Encrypted message: " + encrypted
            render "<br>"
            render "Encryption key: " + aes.getKey()
        }
        catch (Exception e) {
            e.printStackTrace();
            render "Error encrypting the message"
        }

    }

    def encryptWithRsa() {
        String message = params.message
        String base64 = params.keypair

        //Convert base64 keypair to KeyPair object
        KeyPair keypair = null;
        try {
            keypair = CryptoUtil.base64ToKeyPair(base64)
        }
        catch (Exception e) {
            e.printStackTrace();
            render "Invalid keypair"
            return
        }

        String encrypted = RsaCryptoHandler.encrypt(message, keypair);
        render "Encrypted message: " + encrypted
    }

    def decryptWithRsa() {
        String message = params.message
        String base64 = params.keypair

        //Convert base64 keypair to KeyPair object
        KeyPair keypair = null;
        try {
            keypair = CryptoUtil.base64ToKeyPair(base64)
        }
        catch (Exception e) {
            e.printStackTrace();
            render "Couldn't create keypair from base64"
            return
        }

        String plainText = null
        try {
            plainText = RsaCryptoHandler.decrypt(message, keypair);
        }
        catch (Exception e) {
            e.printStackTrace()
            render "Error encrypting the message with given key"
        }


        render "Plaintext message: " + plainText
    }

    /**
     * Upload keypair, validate it and convert to base64 format
     */
    def uploadKeypair() {
        //InputStream fis = request.getFile('certFile').inputStream
    }

}
