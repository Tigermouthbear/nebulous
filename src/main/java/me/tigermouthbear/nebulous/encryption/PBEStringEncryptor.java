package me.tigermouthbear.nebulous.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * @author Tigermouthbear
 */

public class PBEStringEncryptor implements IStringEncryptor {
	public String getName() {
		return "PBEEncryptor";
	}

	public String encrypt(String key, String text) {
		int iterationCount = 19;
		byte[] salt = {
				(byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
				(byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
		};

		try {
			// key generation for encryption
			KeySpec keySpec = new PBEKeySpec(key.toCharArray(), salt, iterationCount);
			SecretKey secretKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

			// prepare the parameter to the ciphers
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

			// encrypt
			Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);

			byte[] encrypted = Base64.getEncoder().encode(cipher.doFinal(text.getBytes(StandardCharsets.UTF_8)));
			return new String(encrypted, StandardCharsets.UTF_8);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String decrypt(String key, String encryptedText) {
		int iterationCount = 19;
		byte[] salt = {
				(byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
				(byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
		};

		try {
			// key generation for decryption
			KeySpec keySpec = new PBEKeySpec(key.toCharArray(), salt, iterationCount);
			SecretKey secretKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

			// prepare the parameter to the ciphers
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

			// decrypt
			Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);

			byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
			return new String(decrypted, StandardCharsets.UTF_8);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
