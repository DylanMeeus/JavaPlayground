package net.itca.datastructures.probabilistic.cuckoo;

import java.util.Random;

/**
 * cuckoo filter class
 * For testing purposes, let's assume we are storing Strings
 */
public class CuckooFilter {

    private Bucket[] buckets;
    private final int size;
    private final int MAX_RETRIES = 500; // how often do we try to reshuffle before giving up and failing?

    public CuckooFilter(final int size) {
        this.size = size;
        buckets = new Bucket[size];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new Bucket(2);
        }
    }

    public boolean contains(final String item) {
        try {
            var fingerPrint = CuckooUtil.getFingerprint(item); // 1 byte fingerprint
            var hash1 = CuckooUtil.sha256(item);
            var intermediate = CuckooUtil.sha256(fingerPrint);
            byte[] hash2 = new byte[hash1.length];
            for (int i = 0; i < hash1.length; i++) {
                hash2[i] = (byte) (hash1[i] ^ intermediate[i]);
            }

            //get indices of the hashes
            var i1 = CuckooUtil.getIndexInArray(hash1, size);
            var i2 = CuckooUtil.getIndexInArray(hash2, size);
            if (buckets[i1].contains(fingerPrint) || buckets[i2].contains(fingerPrint)) {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    /**
     * Could throw a "NoSuchAlgorithmException" if the JVM does not support the chosen hashing algo
     * @param item
     * @return
     */
    public boolean insert(final String item) {
        try {
            if (contains(item)) {
                return true;
            }
            var fingerPrint = CuckooUtil.getFingerprint(item); // 1 byte fingerprint
            var hash1 = CuckooUtil.sha256(item);
            var intermediate = CuckooUtil.sha256(fingerPrint);
            byte[] hash2 = new byte[hash1.length];
            for (int i = 0; i < hash1.length; i++) {
                hash2[i] = (byte) (hash1[i] ^ intermediate[i]);
            }

            //get indices of the hashes
            var i1 = CuckooUtil.getIndexInArray(hash1, size);
            var i2 = CuckooUtil.getIndexInArray(hash2, size);

            if (buckets[i1].hasEmptySlot()) {
                buckets[i1].insert(fingerPrint);
                return true;
            } else if (buckets[i2].hasEmptySlot()) {
                buckets[i2].insert(fingerPrint);
                return true;
            } else {
                // no slots found, start moving things around!
                var randomHash = new Random().nextInt(100) % 2 == 0 ? hash1 : hash2;
                for (int n = 0; n < MAX_RETRIES; n++) {
                    int indexToFree = CuckooUtil.getIndexInArray(randomHash, size);
                    var indexAndFp = buckets[indexToFree].getRandomEntry();
                    buckets[indexToFree].insert(indexAndFp.getA(), fingerPrint);
                    fingerPrint = indexAndFp.getB();
                    byte[] printHash = CuckooUtil.sha256(fingerPrint);
                    // get new location
                    byte[] temp = new byte[randomHash.length];
                    for (int i = 0; i < randomHash.length; i++) {
                        temp[i] = (byte) (randomHash[i] ^ printHash[i]);
                    }
                    randomHash = temp;
                    int newIndexInArray = CuckooUtil.getIndexInArray(randomHash, size);
                    if (buckets[newIndexInArray].hasEmptySlot()) {
                        buckets[newIndexInArray].insert(fingerPrint);
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        throw new RuntimeException("Out of space!");
    }

}
