package dev.tigr.nebulous.encryption;

public interface IStringEncryptor {
	String encrypt(String key, String text);

	String getName();
}
