package ca.tweetzy.rose.utils.colors.patterns;


import ca.tweetzy.rose.utils.colors.ColorFormatter;
import ca.tweetzy.rose.utils.colors.ColorPattern;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date Created: April 02 2022
 * Time Created: 11:15 a.m.
 *
 * @author Kiran Hart
 */
public final class GradientColorPattern implements ColorPattern {

    Pattern pattern = Pattern.compile("<GRADIENT:([0-9A-Fa-f]{6})>(.*?)</GRADIENT:([0-9A-Fa-f]{6})>");

    /**
     * Applies a gradient pattern to the provided String.
     * Output might me the same as the input if this pattern is not present.
     *
     * @param string The String to which this pattern should be applied to
     *
     * @return The new String with applied pattern
     */
    public String process(String string) {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String start = matcher.group(1);
            String end = matcher.group(3);
            String content = matcher.group(2);
            string = string.replace(matcher.group(), ColorFormatter.color(content, new Color(Integer.parseInt(start, 16)), new Color(Integer.parseInt(end, 16))));
        }
        return string;
    }
}
