package io.jdbd.vendor.util;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;

public abstract class JdbdKeys {

    protected JdbdKeys() {
        throw new UnsupportedOperationException();
    }


    protected static final String KEY_BEGIN = "-----BEGIN %s %s KEY-----";

    protected static final String KEY_END = "-----END %s %s KEY-----";

    private static final Pattern BOUNDARY_PATTERN = Pattern.compile("(?:^\\s*-{3,}\\s*[^-]+\\s*-{3,}\\s*$)");

    public static Key readKey(KeyType keyType, String base64Text) {
        return readKey(keyType, getDefaultDecoder(), base64Text);
    }

    public static Key readKey(KeyType keyType, Base64.Decoder decoder, String base64Text) {
        return new SecretKeySpec(decodeBase64(decoder, base64Text), keyType.getAlgorithm());
    }

    public static PrivateKey readPrivateKey(KeyPairType type, String base64Text) {
        return readPrivateKey(type, getDefaultDecoder(), base64Text);
    }

    public static PrivateKey readPrivateKey(KeyPairType type, Base64.Decoder decoder, String base64Text) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(type.getAlgorithm());
            return keyFactory.generatePrivate(
                    new PKCS8EncodedKeySpec(decodeBase64(decoder, base64Text))
            );
        } catch (NoSuchAlgorithmException e) {
            // never here
            throw createNoSuchAlgorithmException(type.getAlgorithm(), e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("base64Text error.", e);
        }
    }

    public static PublicKey readPublicKey(KeyPairType type, String base64Text) {
        return readPublicKey(type, getDefaultDecoder(), base64Text);
    }

    public static PublicKey readPublicKey(KeyPairType type, Base64.Decoder decoder, String base64Text) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(type.getAlgorithm());
            return keyFactory.generatePublic(
                    new X509EncodedKeySpec(decodeBase64(decoder, base64Text))
            );
        } catch (NoSuchAlgorithmException e) {
            // never here
            throw createNoSuchAlgorithmException(type.getAlgorithm(), e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("base64Text error.", e);
        }
    }

    public static Key createKey(KeyType type, int keySize) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(type.getAlgorithm());
            keyGenerator.init(keySize, new SecureRandom());
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            // never here
            throw createNoSuchAlgorithmException(type.getAlgorithm(), e);
        } catch (InvalidParameterException e) {
            throw new IllegalArgumentException("keySize error.", e);
        }
    }

    public static KeyPair createKeyPair(KeyPairType type, int keySize) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(type.getAlgorithm());
            generator.initialize(keySize, new SecureRandom());
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            // never here
            throw createNoSuchAlgorithmException(type.getAlgorithm(), e);
        } catch (InvalidParameterException e) {
            throw new IllegalArgumentException("keySize error.", e);
        }
    }

    public static void writeToPath(Path path, Key key) throws IOException {
        writeToPath(path, getDefaultEncoder(), key);
    }

    public static void writeToPath(Path path, Base64.Encoder encoder, Key key) throws IOException {
        writeTo(Files.newBufferedWriter(path), encoder, key);
    }

    public static void writeTo(Writer w, Key key) throws IOException {
        writeTo(w, getDefaultEncoder(), key);
    }

    public static void writeTo(Writer w, Base64.Encoder encoder, Key key) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(w)) {

            doWriteToFile(writer, encoder, key);

        }
    }

    public static String writeToString(Key key) {
        return writeToString(getDefaultEncoder(), key);
    }

    public static String writeToString(Base64.Encoder encoder, Key key) {
        return encoder.encodeToString(key.getEncoded());
    }


    protected static byte[] decodeBase64(Base64.Decoder decoder, String base64Text) {
        return decoder.decode(
                BOUNDARY_PATTERN.matcher(base64Text).replaceAll("")
        );
    }

    protected static Base64.Encoder getDefaultEncoder() {
        return Base64.getMimeEncoder();
    }

    protected static Base64.Decoder getDefaultDecoder() {
        return Base64.getMimeDecoder();
    }


    /*##################### private ########################*/

    private static void doWriteToFile(BufferedWriter writer, Base64.Encoder encoder, Key key)
            throws IOException {

        final String base64 = encoder.encodeToString(key.getEncoded());
        final String typeDesc = getTypeDesc(key);
        //write top boundary
        writer.write(String.format(KEY_BEGIN, key.getAlgorithm(), typeDesc));
        writer.newLine();

        final int bit = 6, size = 1 << bit;
        int start, end;
        int count = base64.length() / size;
        count = base64.length() % size == 0 ? count : count + 1;

        for (int i = 0; i < count; i++) {
            // start and end
            start = i << bit;
            end = start + size;
            end = Math.min(end, base64.length());
            //write a line
            writer.write(base64.substring(start, end));
            writer.newLine();
        }
        //write bottom boundary
        writer.write(String.format(KEY_END, key.getAlgorithm(), typeDesc));
    }

    private static String getTypeDesc(Key key) {
        String typeDesc;
        if (key instanceof PrivateKey) {
            typeDesc = key.getAlgorithm() + " PRIVATE";
        } else if (key instanceof PublicKey) {
            typeDesc = key.getAlgorithm() + " PUBLIC";
        } else {
            typeDesc = key.getAlgorithm();
        }
        return typeDesc;
    }

    private static RuntimeException createNoSuchAlgorithmException(String algorithm, NoSuchAlgorithmException e) {
        return new RuntimeException(String.format("no such algorithm[%s]", algorithm), e);
    }

}
