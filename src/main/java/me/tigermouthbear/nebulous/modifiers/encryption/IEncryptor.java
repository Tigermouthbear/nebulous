package me.tigermouthbear.nebulous.modifiers.encryption;

public interface IEncryptor {
	String encrypt(String key, String text);

	String getName();
}
