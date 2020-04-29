package me.tigermouthbear.nebulous.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class BlowfishStringEncryptor implements IStringEncryptor {
	public String getName() {
		return "BlowfishEncryptor";
	}

	public String encrypt(String key, String text) {
		try {
			SecretKeySpec keySpec = new SecretKeySpec(MessageDigest.getInstance("MD5").digest(key.getBytes(StandardCharsets.UTF_8)), "Blowfish");

			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);

			byte[] encrypted = Base64.getEncoder().encode(cipher.doFinal(text.getBytes(StandardCharsets.UTF_8)));
			return new String(encrypted, StandardCharsets.UTF_8);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String decrypt(String key, String text) {
		try {
			SecretKeySpec keySpec = new SecretKeySpec(MessageDigest.getInstance("MD5").digest(key.getBytes(StandardCharsets.UTF_8)), "Blowfish");

			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, keySpec);

			byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(text));
			return new String(decrypted, StandardCharsets.UTF_8);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
