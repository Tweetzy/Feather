package ca.tweetzy.rose.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Date Created: April 10 2022
 * Time Created: 4:02 p.m.
 *
 * @author Kiran Hart
 */
@UtilityClass
public final class Replacer {

	private final Pattern TOKEN_PATTERN = Pattern.compile("[\\{|%]([^}|%]+)[\\}|%]");

	/**
	 * It takes a string and a map of key/value pairs, and replaces all occurrences of `${key}` with the corresponding value from the map
	 *
	 * @param text The text to replace tokens in.
	 * @param valuesByKey A map of key/value pairs. The keys are the tokens to be replaced, and the values are the values to be substituted.
	 * @return A string with the tokens replaced with the values from the map.
	 */
	private String replaceTokens(String text, Map<String, Object> valuesByKey) {
		StringBuilder output = new StringBuilder();
		Matcher tokenMatcher = TOKEN_PATTERN.matcher(text);

		int cursor = 0;
		while (tokenMatcher.find()) {
			int tokenStart = tokenMatcher.start();
			int tokenEnd = tokenMatcher.end();
			int keyStart = tokenMatcher.start(1);
			int keyEnd = tokenMatcher.end(1);

			output.append(text.substring(cursor, tokenStart));

			String token = text.substring(tokenStart, tokenEnd);
			String key = text.substring(keyStart, keyEnd);

			if (valuesByKey.containsKey(key)) {
				Object value = valuesByKey.get(key);
				output.append(value);
			} else {
				output.append(token);
			}

			cursor = tokenEnd;
		}
		output.append(text.substring(cursor));

		return output.toString();
	}

	/**
	 * It takes a string and a list of key/value pairs, and replaces all occurrences of the keys in the string with the corresponding values
	 *
	 * @param text The text to replace the variables in.
	 * @return A string with the tokens replaced with the values in the map.
	 */
	public String replaceVariables(String text, Object... replacements) {
		final Map<String, Object> map = new HashMap<>();

		for (int i = 0; i < replacements.length; i += 2) {
			map.put((String) replacements[i], replacements[i + 1]);
		}

		return replaceTokens(text, map);
	}

	/**
	 * Replace variables in a list of strings.
	 *
	 * @param list The list of strings to replace variables in.
	 * @return A list of strings.
	 */
	public List<String> replaceVariables(@NonNull final List<String> list, Object... replacements) {
		return list.stream().map(item -> replaceVariables(item, replacements)).collect(Collectors.toList());
	}
}
