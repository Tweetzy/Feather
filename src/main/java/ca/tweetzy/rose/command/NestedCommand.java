package ca.tweetzy.rose.command;

import java.util.LinkedHashMap;
import java.util.stream.Stream;

/**
 * Date Created: April 09 2022
 * Time Created: 11:42 p.m.
 *
 * @author Kiran Hart
 */
public final class NestedCommand {

    final Command parent;
    final LinkedHashMap<String, Command> children = new LinkedHashMap<>();

    protected NestedCommand(Command parent) {
        this.parent = parent;
    }

    public NestedCommand addSubCommand(Command command) {
        command.getSubCommands().forEach(cmd -> children.put(cmd.toLowerCase(), command));
        return this;
    }

    public NestedCommand addSubCommands(Command... commands) {
        Stream.of(commands).forEach(command -> command.getSubCommands().forEach(cmd -> children.put(cmd.toLowerCase(), command)));
        return this;
    }
}
