package ca.tweetzy.rose.utils;

import java.util.regex.Pattern;

/**
 * Date Created: April 08 2022
 * Time Created: 12:22 a.m.
 *
 * @author Kiran Hart
 */
public final class ReplaceUtil {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[({|%)]([^{}]+)[(}|%)]");

}
