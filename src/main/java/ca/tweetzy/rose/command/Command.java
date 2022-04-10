package ca.tweetzy.rose.command;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Date Created: April 09 2022
 * Time Created: 11:37 p.m.
 *
 * @author Kiran Hart
 */
public abstract class Command {

    private final AllowedExecutor allowedExecutor;
    private final List<String> subCommands = new ArrayList<>();

    protected Command(AllowedExecutor allowedExecutor, String... subCommands) {
        this.allowedExecutor = allowedExecutor;
        this.subCommands.addAll(Arrays.asList(subCommands));
    }

    public final List<String> getSubCommands() {
        return Collections.unmodifiableList(this.subCommands);
    }

    public final void addSubCommand(String command) {
        this.subCommands.add(command);
    }

    protected boolean isNoConsole() {
        return this.allowedExecutor == AllowedExecutor.PLAYER;
    }

    public AllowedExecutor getAllowedExecutor() {
        return allowedExecutor;
    }

    protected abstract ReturnType execute(CommandSender sender, String... args);

    protected abstract List<String> tab(CommandSender sender, String... args);

    public abstract String getPermissionNode();

    public abstract String getSyntax();

    public abstract String getDescription();
}
