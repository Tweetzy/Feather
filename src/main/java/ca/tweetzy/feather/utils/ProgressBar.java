package ca.tweetzy.feather.utils;

import joptsimple.internal.Strings;

/**
 * Date Created: April 10 2022
 * Time Created: 12:20 p.m.
 *
 * @author Kiran Hart
 * @see <a href="https://www.spigotmc.org/threads/progress-bars-and-percentages.276020/"></a>
 */
public final class ProgressBar {

    /**
     * It takes in a current value, a max value, the total number of bars, a symbol, a color for the completed bars, and a color for the not completed bars, and returns a string with the
     * completed bars colored with the completed color and the not completed bars colored with the not completed color
     *
     * @param current The current value
     * @param max The maximum value of the bar.
     * @param totalBars The total amount of bars you want to display.
     * @param symbol The symbol to use for the bar.
     * @param completedColor The color of the completed part of the bar.
     * @param notCompletedColor The color of the bar that hasn't been completed yet.
     * @return A string of a certain length, with a certain amount of a certain character, with a certain color.
     */
    public static String make(final int current, int max, int totalBars, char symbol, String completedColor, String notCompletedColor) {
        final float percent = (float) current / max;
        final int bars = (int) (totalBars * percent);
        return Common.colorize(completedColor + Strings.repeat(symbol, bars)) + Common.colorize(notCompletedColor + Strings.repeat(symbol, totalBars - bars));
    }

    /**
     * It returns a string that represents a progress bar
     *
     * @param current The current value
     * @param max The maximum value of the bar.
     * @param totalBars The total number of bars you want to display.
     * @param symbol The symbol to use for the progress bar.
     * @return A string
     */
    public static String make(final int current, int max, int totalBars, char symbol) {
        return make(current, max, totalBars, symbol, "&e", "&7");
    }

    /**
     * It takes the current value, the max value, the total number of bars, the character to use for the bars, the color of the filled bars, and the color of the empty bars.
     *
     * @param current The current value
     * @param max The maximum value of the bar.
     * @param totalBars The total number of bars to display.
     * @return A string
     */
    public static String make(final int current, int max, int totalBars) {
        return make(current, max, totalBars, '|', "&e", "&7");
    }
}
