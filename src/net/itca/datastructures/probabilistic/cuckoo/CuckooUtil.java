package net.itca.datastructures.probabilistic.cuckoo;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class CuckooUtil {

    private static final String MD5 = "md5";
    private static final String SHA1 = "SHA-1";
    private static final String SHA256 = "SHA-256";

    /**
     * Create a one-byte fingerprint for ease of use later
     * @param entry
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte getFingerprint(final String entry) throws NoSuchAlgorithmException {
        return (byte) (Objects.hash(entry) & 0xff);
    }


    public static byte[] sha256(final String entry) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance(SHA256);
        return hashIndexResult(md, entry);
    }

    public static byte[] sha256(final byte fingerprint) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance(SHA256);
        return hashIndexResult(md, new byte[]{fingerprint});
    }

    /**
     * Might not have to use this
     * @param fingerprint
     * @param arraySize
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static int sha1index(final byte[] fingerprint, final int arraySize) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance(SHA1);
        var hash = hashIndexResult(md, fingerprint);
        return getIndexInArray(hash, arraySize);
    }

    public static int getIndexInArray(final byte[] hash, final int arraySize) {
        var bigHash = new BigInteger(hash);
        var bigIndex = bigHash.mod(BigInteger.valueOf(arraySize));
        return bigIndex.intValue();
    }

    private static byte[] hashIndexResult(final MessageDigest md, final String element) {
        return hashIndexResult(md, element.getBytes());
    }

    private static byte[] hashIndexResult(final MessageDigest md, final byte[] element) {
        md.update(element);
        return md.digest();
    }




}
