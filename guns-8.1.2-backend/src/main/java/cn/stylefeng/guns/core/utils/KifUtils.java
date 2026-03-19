package cn.stylefeng.guns.core.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KifUtils {

    /**
     * 按count分割list
     *
     * @param list  要分割的列表
     * @param count 每组的数量
     * @return 分割后的列表集合
     */
    public static <T> List<List<T>> splitList(List<T> list, int count) {
        List<List<T>> rs = new ArrayList<List<T>>();
        int start = 0, end = 0, total = list.size();
        while (start < total) {
            end = start + count <= total ? start + count : total;
            rs.add(new ArrayList<>(list.subList(start, end)));
            start = end;
        }
        return rs;
    }

    public static <T> List<List<T>> splitIterable(Iterable<T> it, int count) {
        return splitList(it2List(it), count);
    }

    public static <T> List<T> it2List(Iterable<T> it) {
        List<T> copy = new ArrayList<T>();
        Iterator<T> iter = it.iterator();
        while (iter.hasNext())
            copy.add(iter.next());
        return copy;
    }
}
