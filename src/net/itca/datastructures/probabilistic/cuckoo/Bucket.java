package net.itca.datastructures.probabilistic.cuckoo;

import java.util.Random;

public class Bucket {

    private final Byte[] slots;

    public Bucket(int capacity){
        slots = new Byte[capacity];
    }

    public boolean contains(byte fingerprint) {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != null && slots[i] == fingerprint) {
                return true;
            }
        }
        return false;
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

    public Tuple<Integer, Byte> getRandomEntry(){
        var randomSlot = new Random().nextInt(slots.length);
        return new Tuple<>(randomSlot, slots[randomSlot]);
    }


}
