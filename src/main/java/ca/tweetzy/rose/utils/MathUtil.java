package ca.tweetzy.rose.utils;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Date Created: April 06 2022
 * Time Created: 4:35 p.m.
 *
 * @author Kiran Hart
 */
public final class MathUtil {

    /**
     * Holds all valid roman numbers
     */
    private final static NavigableMap<Integer, String> romanNumbers = new TreeMap<>();

    // Load the roman numbers
    static {
        romanNumbers.put(1000, "M");
        romanNumbers.put(900, "CM");
        romanNumbers.put(500, "D");
        romanNumbers.put(400, "CD");
        romanNumbers.put(100, "C");
        romanNumbers.put(90, "XC");
        romanNumbers.put(50, "L");
        romanNumbers.put(40, "XL");
        romanNumbers.put(10, "X");
        romanNumbers.put(9, "IX");
        romanNumbers.put(5, "V");
        romanNumbers.put(4, "IV");
        romanNumbers.put(1, "I");
    }

    /**
     * Return a roman number representation of the given number
     *
     * @param number to be converted
     *
     * @return converted number
     */
    public static String toRoman(final int number) {
        if (number == 0)
            return "0";

        final int literal = romanNumbers.floorKey(number);

        if (number == literal)
            return romanNumbers.get(number);

        return romanNumbers.get(literal) + toRoman(number - literal);
    }
}
