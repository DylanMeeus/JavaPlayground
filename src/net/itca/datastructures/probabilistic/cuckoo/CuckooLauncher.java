package net.itca.datastructures.probabilistic.cuckoo;

public class CuckooLauncher {

    public static void main(String... args) {
        CuckooFilter filter = new CuckooFilter(10);
        for (int i = 0; i < 10; i++) {
            filter.insert(Integer.toString(i));
        }

        for (int i = 0; i < 10; i++) {
            System.out.println(filter.contains(Integer.toString(i)));
        }

    }
}

