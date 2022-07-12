package ca.tweetzy.feather.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Date Created: April 10 2022
 * Time Created: 12:38 p.m.
 *
 * @author Kiran Hart
 */
@AllArgsConstructor
@Getter
public class Tuple<K, V> {

    private final K key;
    private final V value;
}
