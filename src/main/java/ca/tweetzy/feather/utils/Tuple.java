package ca.tweetzy.feather.utils;

import lombok.Data;

/**
 * Date Created: April 10 2022
 * Time Created: 12:38 p.m.
 *
 * @author Kiran Hart
 */
@Data
public final class Tuple<K, V> {

    private final K key;
    private final V value;
}
