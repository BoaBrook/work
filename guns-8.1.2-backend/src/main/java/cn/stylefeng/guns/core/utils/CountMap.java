package cn.stylefeng.guns.core.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CountMap<T> {
    private Map<T, Integer> map = new ConcurrentHashMap<>();

    public Set<T> keySet() {
        synchronized (map) {
            return map.keySet();
        }
    }

    public boolean isEmpty() {
        return keySet().isEmpty();
    }

    public boolean contains(T key) {
        synchronized (map) {
            return map.keySet().contains(key);
        }
    }

    public void add(T key) {
        if (key == null) {
            return;
        }
        synchronized (map) {
            Integer count = map.get(key);
            if (count == null) {
                count = 1;
            } else {
                count++;
            }
            map.put(key, count);
        }
    }

    public void add(T key, Integer count) {
        if (key == null) {
            return;
        }
        synchronized (map) {
            Integer count2 = map.get(key);
            if (count2 == null) {
                count2 = count;
            } else {
                count2 += count;
            }
            map.put(key, count2);
        }
    }

    public void addAll(Collection<T> keys) {
        for (T t : keys) {
            add(t);
        }
    }

    public void remove(T key) {
        if (key == null) {
            return;
        }
        synchronized (map) {
            Integer count = map.get(key);
            if (count == null) {
                return;
            } else {
                count--;
            }
            if (count <= 0) {
                map.remove(key);
            }
        }
    }

    public void removeAll(Collection<T> keys) {
        for (T t : keys) {
            remove(t);
        }
    }

    public Integer get(T key) {
        synchronized (map) {
            return map.get(key);
        }
    }

    public Map<T, Integer> asMap() {
        synchronized (map) {
            return new HashMap<>(map);
        }
    }
}
