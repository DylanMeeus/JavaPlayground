package net.itca;

import java.util.*;

/**
 * Created by dmeeus1 on 10-7-2017.
 */
public class CollectionTools {



    public static <E> List<E> reverse(List<E> input) {
        List<E> copy = new ArrayList<E>(input);
        input.clear();
        for (int i = copy.size() - 1; i >= 0; i--) {
            input.add(copy.get(i));
        }
        return input;
    }

    public static <E> List<E> reverse3(final List<E> list) {
        List<E> copy = new ArrayList<E>(list);
        final Iterator<E> i = list.iterator();
        while (i.hasNext()) {
            i.next();
            i.remove();
        }
        return list;
    }


    /*
      List<T> newList = new ArrayList<T>();
        ListIterator<T> i = filterList.listIterator();
        while(i.hasNext()){
            if(!listFilter.filter(i.next())){
                i.remove();
            }
        }
     */
}
