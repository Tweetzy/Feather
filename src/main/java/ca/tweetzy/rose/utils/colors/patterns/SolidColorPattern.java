package ca.tweetzy.rose.utils.colors.patterns;


import ca.tweetzy.rose.utils.colors.ColorFormatter;
import ca.tweetzy.rose.utils.colors.ColorPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date Created: April 02 2022
 * Time Created: 11:17 a.m.
 *
 * @author Kiran Hart
 */
public final class SolidColorPattern implements ColorPattern {
    Pattern pattern = Pattern.compile("#([0-9A-Fa-f]{6})");

    /**
     * Applies a solid RGB color to the provided String.
     * Output might me the same as the input if this pattern is not present.
     *
     * @param string The String to which this pattern should be applied to
     *
     * @return The new String with applied pattern
     */
    public String process(String string) {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String color = matcher.group(1);
            if (color == null) color = matcher.group(2);

            string = string.replace(matcher.group(), ColorFormatter.getColor(color) + "");
        }
        return string;
    }
}
