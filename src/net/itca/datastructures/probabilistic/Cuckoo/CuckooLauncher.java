package net.itca.datastructures.probabilistic.Cuckoo;

public class CuckooLauncher {

    public static void main(String... args) {
        CuckooFilter filter = new CuckooFilter(10);
        for (int i = 0; i < 10; i++) {
            filter.insert(Integer.toString(i));
        }
    }
}

