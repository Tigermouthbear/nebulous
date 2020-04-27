package me.tigermouthbear.nebulous.encryption.strings;

public interface IStringEncryptor {
	String encrypt(String key, String text);

	String getName();
}
