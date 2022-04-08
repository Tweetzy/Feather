package ca.tweetzy.rose.utils;

import ca.tweetzy.rose.exception.CalculatorException;

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

    /**
     * Evaluate the given expression
     *
     * @param expression math expression
     * @return the calculated result
     */
    public static double calculate(final String expression) {
        class Parser {
            int pos = -1, c;

            void eatChar() {
                c = ++pos < expression.length() ? expression.charAt(pos) : -1;
            }

            void eatSpace() {
                while (Character.isWhitespace(c))
                    eatChar();
            }

            double parse() {
                eatChar();

                final double v = parseExpression();

                if (c != -1)
                    throw new CalculatorException("Unexpected: " + (char) c);

                return v;
            }

            double parseExpression() {
                double v = parseTerm();

                for (;;) {
                    eatSpace();

                    if (c == '+') { // addition
                        eatChar();
                        v += parseTerm();
                    } else if (c == '-') { // subtraction
                        eatChar();
                        v -= parseTerm();
                    } else
                        return v;

                }
            }

            double parseTerm() {
                double v = parseFactor();

                for (;;) {
                    eatSpace();

                    if (c == '/') { // division
                        eatChar();
                        v /= parseFactor();
                    } else if (c == '*' || c == '(') { // multiplication
                        if (c == '*')
                            eatChar();
                        v *= parseFactor();
                    } else
                        return v;
                }
            }

            double parseFactor() {
                double v;
                boolean negate = false;

                eatSpace();

                if (c == '+' || c == '-') { // unary plus & minus
                    negate = c == '-';
                    eatChar();
                    eatSpace();
                }

                if (c == '(') { // brackets
                    eatChar();
                    v = parseExpression();
                    if (c == ')')
                        eatChar();
                } else { // numbers
                    final StringBuilder sb = new StringBuilder();

                    while (c >= '0' && c <= '9' || c == '.') {
                        sb.append((char) c);
                        eatChar();
                    }

                    if (sb.length() == 0)
                        throw new CalculatorException("Unexpected: " + (char) c);

                    v = Double.parseDouble(sb.toString());
                }
                eatSpace();
                if (c == '^') { // exponentiation
                    eatChar();
                    v = Math.pow(v, parseFactor());
                }
                if (negate)
                    v = -v; // unary minus is applied after exponentiation; e.g. -3^2=-9
                return v;
            }
        }
        return new Parser().parse();
    }
}
