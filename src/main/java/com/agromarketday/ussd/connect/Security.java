/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.connect;

/**
 *
 * @author smallgod
 */

/* 
 * Password Hashing With PBKDF2 (http://crackstation.net/hashing-security.htm).
 * Copyright (c) 2013, Taylor Hornby
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
import com.agromarketday.ussd.constant.ErrorCode;
import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.logger.LoggerUtil;
import com.agromarketday.ussd.util.GeneralUtils;
import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
//import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpRequestBase;

/*
 * PBKDF2 salted password hashing.
 * Author: havoc AT defuse.ca
 * www: http://crackstation.net/hashing-security.htm
 */
public class Security {

    private static final LoggerUtil logger = new LoggerUtil(Security.class);

    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

    //define("PBKDF2_HASH_ALGORITHM", "sha512");
    //define("PBKDF2_ITERATIONS", 20000);
    //define("PBKDF2_SALT_BYTES", 512);
    //define("PBKDF2_HASH_BYTES", 512);
    //Put in config file some of them like the iterations
    // The following constants may be changed without breaking existing hashes.
    public static final int SALT_BYTE_SIZE = 24;
    public static final int HASH_BYTE_SIZE = 24;
    public static final int PBKDF2_ITERATIONS = 500;// use a high enough iteration count, ideally as high as you can tolerate on your hardware

    public static final int ITERATION_INDEX = 0;
    public static final int SALT_INDEX = 1;
    public static final int PBKDF2_INDEX = 2;

    /**
     * Returns a salted PBKDF2 hash of the password.
     *
     * @param password the password to hash
     * @return a salted PBKDF2 hash of the password
     * @throws com.library.customexception.MyCustomException
     */
    public static String createHash(String password) throws MyCustomException {
        return createHash(password.toCharArray());
    }

    /**
     * Returns a salted PBKDF2 hash of the password.
     *
     * @param password the password to hash
     * @return a salted PBKDF2 hash of the password
     * @throws com.library.customexception.MyCustomException
     */
    private static String createHash(char[] password) throws MyCustomException {

        String errorDetails;
        String errorDescription;
        ErrorCode errorCode;

        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_BYTE_SIZE];
            random.nextBytes(salt);

            // Hash the password
            byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
            // format iterations:salt:hash
            return PBKDF2_ITERATIONS + ":" + toHex(salt) + ":" + toHex(hash);

        } catch (NoSuchAlgorithmException nsae) {
            errorDetails = "NoSuchAlgorithmException while hashing the password: " + nsae.getMessage();
        } catch (InvalidKeySpecException ikse) {
            errorDetails = "InvalidKeySpecException while hashing the password: " + ikse.getMessage();

        }

        errorCode = ErrorCode.PROCESSING_ERR;
        errorDescription = "Failed to encrypt password";

        MyCustomException error = GeneralUtils.getSingleError(errorCode, errorDescription, errorDetails);
        throw error;

    }

    /**
     * Validates a password using a hash.
     *
     * @param password the password to check
     * @param correctHash the hash of the valid password
     * @throws com.library.customexception.MyCustomException
     */
    public static void validatePassword(String password, String correctHash) throws MyCustomException {
        validatePassword(password.toCharArray(), correctHash);
    }

    /**
     * Validates a password using a hash.
     *
     * @param password the password to check
     * @param correctHash the hash of the valid password
     * @throws com.library.customexception.MyCustomException
     */
    private static void validatePassword(char[] password, String correctHash) throws MyCustomException {

        String errorDetails;
        String errorDescription;
        ErrorCode errorCode;

        try {
            // Decode the hash into its parameters
            String[] params = correctHash.split(":");
            int iterations = Integer.parseInt(params[ITERATION_INDEX]);
            byte[] salt = fromHex(params[SALT_INDEX]);
            byte[] hash = fromHex(params[PBKDF2_INDEX]);
            // Compute the hash of the provided password, using the same salt, 
            // iteration count, and hash length
            byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
            // Compare the hashes in constant time. The password is correct if
            // both hashes match.
            if (slowEquals(hash, testHash)) {
                return;
            }

            errorDescription = "Error! Invalid user Id or password. Please try again";
            errorCode = ErrorCode.PASSWORDS_DONT_MATCH_ERR;
            errorDetails = "Provided password doesn't match user password";

        } catch (NoSuchAlgorithmException nsae) {
            
            errorCode = ErrorCode.PROCESSING_ERR;
            errorDescription = "Error! Failed to validate user login. Please try again";
            errorDetails = "NoSuchAlgorithmException while validating the password: " + nsae.getMessage();
            
        } catch (InvalidKeySpecException ikse) {
            
            errorCode = ErrorCode.PROCESSING_ERR;
            errorDescription = "Error! Failed to validate user login. Please try again";
            errorDetails = "InvalidKeySpecException while validating the password: " + ikse.getMessage();

        }

        MyCustomException error = GeneralUtils.getSingleError(errorCode, errorDescription, errorDetails);
        throw error;
    }

    /**
     * Compares two byte arrays in length-constant time. This comparison method
     * is used so that password hashes cannot be extracted from an on-line
     * system using a timing attack and then attacked off-line.
     *
     * @param a the first byte array
     * @param b the second byte array
     * @return true if both byte arrays are the same, false if not
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    /**
     * Computes the PBKDF2 hash of a password.
     *
     * @param password the password to hash.
     * @param salt the salt
     * @param iterations the iteration count (slowness factor)
     * @param bytes the length of the hash to compute in bytes
     * @return the PBDKF2 hash of the password
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * Converts a string of hexadecimal characters into a byte array.
     *
     * @param hex the hex string
     * @return the hex string decoded into a byte array
     */
    private static byte[] fromHex(String hex) {
        byte[] binary = new byte[hex.length() / 2];
        for (int i = 0; i < binary.length; i++) {
            binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return binary;
    }

    /**
     * Converts a byte array into a hexadecimal string.
     *
     * @param array the byte array to convert
     * @return a length*2 character string encoding the byte array
     */
    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    /**
     * Tests the basic functionality of the PasswordHash class
     *
     * @param args ignored
     * @throws com.library.customexception.MyCustomException
     */
//    public static void main(String[] args) throws MyCustomException {
//        try {
//            // Print out 10 hashes
//            for (int i = 0; i < 10; i++) {
//                System.out.println(Security.createHash("p\r\nassw0Rd!"));
//            }
//
//            // Test password validation
//            boolean failure = false;
//            System.out.println("Running tests...");
//            for (int i = 0; i < 100; i++) {
//                String password = "" + i;
//                String hash = createHash(password);
//                String secondHash = createHash(password);
//                if (hash.equals(secondHash)) {
//                    System.out.println("FAILURE: TWO HASHES ARE EQUAL!");
//                    failure = true;
//                }
//                String wrongPassword = "" + (i + 1);
//                if (validatePassword(wrongPassword, hash)) {
//                    System.out.println("FAILURE: WRONG PASSWORD ACCEPTED!");
//                    failure = true;
//                }
//                if (!validatePassword(password, hash)) {
//                    System.out.println("FAILURE: GOOD PASSWORD NOT ACCEPTED!");
//                    failure = true;
//                }
//            }
//            if (failure) {
//                System.out.println("TESTS FAILED!");
//            } else {
//                System.out.println("TESTS PASSED!");
//            }
//        } catch (Exception ex) {
//            System.out.println("ERROR: " + ex);
//        }
//    }
    /**
     * *
     *
     * @param authorization
     * @param splitLimit
     * @return a string array of all decoded values
     */
    public static String[] decodeBasicAuthCredentials(String authorization, int splitLimit) {

        //final String authorization = httpRequest.getHeader("Authorization");
        String[] decodedCredentals = null;

        if (authorization != null && authorization.startsWith("Basic")) {
            //Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            Base64 base64Decoder = new Base64();
            String credentials = new String(base64Decoder.decode(base64Credentials), Charset.forName("UTF-8"));

            //if credentials = "username:password:requestclientcredentials" //use splitlimit 5
            //if credentials = "username:password" //use splitlimit 2
            decodedCredentals = credentials.split(":", splitLimit);
        }

        if (splitLimit == 5 && (decodedCredentals == null || decodedCredentals.length < 3)) {
            // throw new MyCustomException("missing parameters", "BAD_REQUEST", "less or missing parameters while trying to decode credentials", "CLIENT_ERROR");
            logger.error("less or missing parameters while trying to decode credentials");
        }

        if (splitLimit == 2 && (decodedCredentals == null || decodedCredentals.length < 2)) {
            // throw new MyCustomException("missing parameters", "BAD_REQUEST", "less or missing parameters while trying to decode credentials", "CLIENT_ERROR");
            logger.error("less or missing parameters while trying to decode credentials");
        }

        int x = 0;

        for (String cred : decodedCredentals) {
            logger.debug("cred " + ++x + ": " + cred);
        }
        return decodedCredentals;
    }

    /**
     * *
     *
     * @param httpObject
     * @param arg1
     * @param arg2
     * @return httpObject with basic auth header added
     */
    public static HttpRequestBase setBasicEncoding(HttpRequestBase httpObject, String arg1, String arg2) {

        String concatCreds;
        if (arg2 == null) {
            concatCreds = arg1;
        } else if (arg1 == null) {
            concatCreds = arg2;
        } else {
            concatCreds = arg1.trim() + ":" + arg2.trim(); //username:password for instance
        }
        concatCreds = concatCreds.trim();

        //String encodedCreds = new Base64().encodeToString(concatCreds.getBytes());
        //conn.setRequestProperty("Authorization", "Basic " + base64Creds);
        //httpRequest.setHeader("Authorization", "Basic " + encodedCreds);        
        byte[] encodedAuth = Base64.encodeBase64(concatCreds.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);

        httpObject.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        return httpObject;

    }

    /**
     * *
     *
     * @param httpServRespObject
     * @param arg1
     * @param arg2
     * @return
     */
//    public static HttpServletResponse setBasicEncoding(HttpServletResponse httpServRespObject, String arg1, String arg2) {
//
//        String concatCreds;
//        if (arg2 == null) {
//            concatCreds = arg1;
//        } else if (arg1 == null) {
//            concatCreds = arg2;
//        } else {
//            concatCreds = arg1.trim() + ":" + arg2.trim(); //username:password for instance
//        }
//
//        concatCreds = concatCreds.trim();
//
//        //String encodedCreds = new Base64().encodeToString(concatCreds.getBytes());
//        //conn.setRequestProperty("Authorization", "Basic " + base64Creds);
//        //httpRequest.setHeader("Authorization", "Basic " + encodedCreds);        
//        byte[] encodedAuth = Base64.encodeBase64(concatCreds.getBytes(Charset.forName("US-ASCII")));
//        String authHeader = "Basic " + new String(encodedAuth);
//
//        httpServRespObject.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
//
//        return httpServRespObject;
//    }
}
