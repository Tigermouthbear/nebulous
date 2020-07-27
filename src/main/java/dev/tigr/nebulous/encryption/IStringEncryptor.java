package dev.tigr.nebulous.encryption;

/**
 * @author Tigermouthbear
 */
public interface IStringEncryptor {
    String encrypt(String key, String text);

    String getName();
}
