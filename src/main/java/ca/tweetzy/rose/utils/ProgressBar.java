package ca.tweetzy.rose.utils;

import joptsimple.internal.Strings;

/**
 * Date Created: April 10 2022
 * Time Created: 12:20 p.m.
 *
 * @author Kiran Hart
 * @see <a href="https://www.spigotmc.org/threads/progress-bars-and-percentages.276020/"></a>
 */
public final class ProgressBar {

    public static String make(final int current, int max, int totalBars, char symbol, String completedColor, String notCompletedColor) {
        final float percent = (float) current / max;
        final int bars = (int) (totalBars * percent);
        return Common.colorize(completedColor + Strings.repeat(symbol, bars)) + Common.colorize(notCompletedColor + Strings.repeat(symbol, totalBars - bars));
    }

    public static String make(final int current, int max, int totalBars, char symbol) {
        return make(current, max, totalBars, symbol, "&e", "&7");
    }

    public static String make(final int current, int max, int totalBars) {
        return make(current, max, totalBars, '|', "&e", "&7");
    }
}
