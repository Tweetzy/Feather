package ca.tweetzy.feather.utils;

import lombok.Data;

/**
 * Date Created: April 10 2022
 * Time Created: 12:38 p.m.
 *
 * @author Kiran Hart
 */
@Data
public final class Triple<L, M, R> {

    private final L left;

    private final M middle;

    private final R right;
}
