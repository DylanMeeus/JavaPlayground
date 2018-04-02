package net.itca.datastructures.probabilistic.Cuckoo;

import java.util.Random;

public class Bucket {

    private Byte[] slots; // byte[depth][fingerprint]

    public Bucket(int capacity){
        slots = new Byte[capacity];
    }

    public boolean hasEmptySlot(){
        for (Byte slot : slots) {
            if (slot == null) {
                return true;
            }
        }
        return false;
    }

    public void insert(int index, byte fingerprint) {
        slots[index] = fingerprint;
    }

    public void insert(byte fingerprint){
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == null) {
                slots[i] = fingerprint;
            }
        }
    }

    public Tuple<Integer, Byte> getRandomFingerprint(){
        var randomSlot = new Random().nextInt(slots.length);
        return new Tuple<>(randomSlot, slots[randomSlot]);
    }


}
