package com.example.projectecripto;

import android.util.Log;

import com.example.projectecripto.model.Contact;
import com.example.projectecripto.model.SignedData;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Xifrador {
    private static KeyPair keyPair;
    private static PublicKey serverPublicKey;

    public static String hashPassword(String password){
        String hashedPassword = null;
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            hashedPassword = Base64.getEncoder().encodeToString(hash);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return hashedPassword;
    }
    public static boolean verifyPassword(String password, String hashedPassword){
        String hash = hashPassword(password);
        Log.v("Xifrador", "Hash: " + hash);
        Log.v("Xifrador", "Hashed password: " + hashedPassword);
        return hash.equals(hashedPassword);
    }

    public static SignedData signData(String data) {
        //string to byte[] base64
        byte[] byteData = data.getBytes();
        byte[] signature = null;
        try {
            Log.v("Xifrador", "Signing data...");
            Log.v("Xifrador", "Private key: " + keyPair.getPrivate());
            Log.v("Xifrador", "Public key: " + keyPair.getPublic());
            Signature signer = Signature.getInstance("SHA1withRSA");
            signer.initSign(keyPair.getPrivate());
            signer.update(byteData);
            signature = signer.sign();
        } catch (Exception ex) {
            System.err.println("Error signant les dades: " + ex);
        }
        return new SignedData(data, Base64.getEncoder().encodeToString(signature));
    }

    public static boolean validateSignature(byte[] data,byte[] signature)
    {
        boolean isValid = false;
        try {
            Signature signer = Signature.getInstance("SHA1withRSA");
            signer.initVerify(keyPair.getPublic());
            signer.update(data);
            isValid = signer.verify(signature);
        } catch (Exception ex) {
            System.err.println("Error validant les dades: " + ex);
        }
        return isValid;
    }

    private static KeyPair randomGenerate(int len) {
        KeyPair keys = null;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(len);
            keys = keyGen.genKeyPair();
        } catch (Exception ex) {
            System.err.println("Generador no disponible.");
        }
        return keys;
    }
    public static void generateKeyPair() {
        keyPair = randomGenerate(1024);
        Log.v("Xifrador", "Generating key pair...");
    }

    public static PublicKey getPublicKey() {
        if(keyPair == null) generateKeyPair();
        return keyPair.getPublic();
    }

    public static void setServerPublicKey(PublicKey serverPublicKey) {
        Xifrador.serverPublicKey = serverPublicKey;
    }

    // https://drive.google.com/drive/folders/1R48zBECVVToy79lXJHQ4Zt2gF5GHsrX4

    public static byte[][] encryptWrappedData(String data) {

        byte[] byteData = signData(data).toJson().getBytes();
        byte[][] encWrappedData = new byte[2][];
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            SecretKey sKey = kgen.generateKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sKey);
            byte[] encMsg = cipher.doFinal(byteData);
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            //cipher.init(Cipher.WRAP_MODE, keyPair.getPublic());
            cipher.init(Cipher.WRAP_MODE, serverPublicKey);
            byte[] encKey = cipher.wrap(sKey);
            encWrappedData[0] = encMsg;
            encWrappedData[1] = encKey;
        } catch (Exception ex) {
            System.err.println("Ha succeït un error xifrant: " + ex);
        }
        return encWrappedData;
    }
    public static String decryptWrappedData(byte[][] data) {
        byte[] decryptedData = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.UNWRAP_MODE, keyPair.getPrivate());
            Log.v("Xifrador","Public key: " + publicKeyToString(keyPair.getPublic()));
            Log.v("Xifrador","Obtenint clau secreta desxifrada...");
            SecretKey sKey = (SecretKey) cipher.unwrap(data[1], "AES", Cipher.SECRET_KEY);
            Log.v("Xifrador", "Clau secreta desxifrada: ");
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sKey);
            decryptedData = cipher.doFinal(data[0]);
            Log.v("Xifrador", "Missatge desxifrat: " + new String(decryptedData));
        } catch (InvalidKeyException e) {
            System.err.println("Invalid key for decryption: " + e);
        } catch (BadPaddingException e) {
            System.err.println("Bad padding for decryption: " + e);
        } catch (Exception ex) {
            System.err.println("An error occurred during decryption: " + ex);
        }
        return new String(decryptedData);
    }

    public static PublicKey getPublicKeyFromString(String key){
        try{
            byte[] byteKey = Base64.getDecoder().decode(key.getBytes());
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        }catch (Exception e) {
            throw new RuntimeException("Error while converting string to PublicKey: " + e);
        }
    }

    public static String publicKeyToString(PublicKey key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

}
