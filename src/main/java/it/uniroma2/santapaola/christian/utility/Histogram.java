package it.uniroma2.santapaola.christian.utility;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * TODO: rewrite this better.
 * A it.uniroma2.santapaola.christian.utility.Histogram is a Data Structure that given a Type K that must implements equals and Comparable interface
 * Given an Object K, it tells how many Objects J so that J == K were inserted inside the it.uniroma2.santapaola.christian.utility.Histogram.
 * @param <K> Type K that must implements equals and Comparable interface
 */
public class Histogram<K> {
    private Map<K, Integer> Histogram;

    /**
     * Constructor of a it.uniroma2.santapaola.christian.utility.Bucket.
     */
    public Histogram() {
        Histogram = new TreeMap<K, Integer>();
    }

    /**
     * insert key inside the bucket.
     * if the key was inserted before, add the value associated with it by 1.
     * if the key was not inserted before, create a new pair (key, 1).
     * @param key
     */
    public void insert(K key) {
        if (Histogram.containsKey(key) == true) {
            int value = Histogram.get(key);
            Histogram.put(key, value + 1);
        } else {
            Histogram.put(key, 1);
        }
    }

    /**
     * retrieve the number of time key was inserted inside the bucket.
     * @param key
     * @return
     */
    public int retrieve(K key) {
        if (Histogram.containsKey(key) == true) {
            return Histogram.get(key);
        } else {
            return 0;
        }
    }

    /**
     * return a set containg all key inserted inside the bucket.
     * @return
     */
    public Set<K> getKeys() {
        return Histogram.keySet();
    }

}
