package ca.tweetzy.rose.command;

import ca.tweetzy.rose.comp.enums.ServerProject;
import ca.tweetzy.rose.comp.enums.ServerVersion;
import ca.tweetzy.rose.utils.Common;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Date Created: April 09 2022
 * Time Created: 11:44 p.m.
 *
 * @author Kiran Hart
 */
public final class CommandManager implements CommandExecutor, TabCompleter {

    private final HashMap<String, NestedCommand> commands = new HashMap<>();

    @Setter
    private String playerOnlyMessage = "&cOnly players can use this command!";

    @Setter
    private String consoleOnlyMessage = "&cThis can only be ran by the console!";

    @Setter
    private String noPermissionMessage = "&cYou do not have permission to use that command!";

    @Setter
    private String unknownCommandMessage = "&cThat command does not exist!";

    @Setter
    private List<String> syntaxErrorMessages = Arrays.asList(
            "&8&m-----------------------------------------------------",
            "&cIncorrect Syntax!",
            "&aValid Syntax&f: ",
            "&e%syntax%",
            "&8&m-----------------------------------------------------"
    );


    private final JavaPlugin plugin;
    private boolean allowLooseCommands = false;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Set<String> getCommands() {
        return Collections.unmodifiableSet(commands.keySet());
    }

    public List<String> getSubCommands(String command) {
        final NestedCommand nested = command == null ? null : commands.get(command.toLowerCase());

        if (nested == null) {
            return Collections.emptyList();
        }

        return new ArrayList<>(nested.children.keySet());
    }

    public Set<Command> getAllCommands() {
        final HashSet<Command> all = new HashSet<>();

        commands.values().stream()
                .filter(c -> c.parent != null && !all.contains(c.parent))
                .forEach(c -> {
                    all.add(c.parent);
                    c.children.values().stream().filter(s -> !all.contains(s)).forEach(all::add);
                });

        return all;
    }

    public CommandManager registerCommandDynamically(String command) {
        CommandManager.registerCommandDynamically(plugin, command, this, this);
        return this;
    }

    public NestedCommand registerCommandDynamically(Command command) {
        final NestedCommand nested = new NestedCommand(command);

        command.getSubCommands().forEach(cmd -> {
            CommandManager.registerCommandDynamically(plugin, cmd, this, this);
            commands.put(cmd.toLowerCase(), nested);
            final PluginCommand pluginCommand = plugin.getCommand(cmd);

            if (pluginCommand != null) {
                pluginCommand.setExecutor(this);
                pluginCommand.setTabCompleter(this);
            } else {
                Common.log("&cFailed to register command: &f/&e" + cmd);
            }
        });

        return nested;
    }

    public NestedCommand addCommand(Command command) {
        final NestedCommand nested = new NestedCommand(command);

        command.getSubCommands().forEach(cmd -> {
            commands.put(cmd.toLowerCase(), nested);
            final PluginCommand pluginCommand = plugin.getCommand(cmd);

            if (pluginCommand == null) {
                Common.log("&cFailed to register command: &f/&e" + cmd);
                return;
            }

            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        });

        return nested;
    }

    public MainCommand addMainCommand(String command) {
        final MainCommand nested = new MainCommand(plugin, command);
        commands.put(command.toLowerCase(), nested.nestedCommands);

        final PluginCommand pluginCommand = plugin.getCommand(command);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        } else {
            Common.log("&cFailed to register command: &f/&e" + command);
        }

        return nested;
    }

    public MainCommand getMainCommand(String command) {
        final NestedCommand nested = command == null ? null : commands.get(command.toLowerCase());

        if (nested != null && nested.parent instanceof MainCommand) {
            return (MainCommand) nested.parent;
        }

        return null;
    }

    public CommandManager addCommands(Command... commands) {
        for (Command command : commands) {
            addCommand(command);
        }

        return this;
    }

    public CommandManager setExecutor(String command) {
        final PluginCommand pluginCommand = command == null ? null : plugin.getCommand(command);

        if (pluginCommand != null) {
            pluginCommand.setExecutor(this);
        } else {
            Common.log("&cFailed to register command: &f/&e" + command);
        }

        return this;
    }

    public CommandManager setUseClosestCommand(boolean bool) {
        this.allowLooseCommands = bool;
        return this;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command command, @NotNull String s, @NotNull String[] args) {
        // grab the specific command that's being called
        final NestedCommand nested = commands.get(command.getName().toLowerCase());

        if (nested != null) {
            // check to see if we're trying to call a sub-command
            if (args.length != 0 && !nested.children.isEmpty()) {
                final String subCommand = getSubCommand(nested, args);

                if (subCommand != null) {
                    // we have a subcommand to use!
                    final Command sub = nested.children.get(subCommand);
                    // adjust the arguments to match - BREAKING!!
                    int i = subCommand.indexOf(' ') == -1 ? 1 : 2;
                    String[] newArgs = new String[args.length - i];
                    System.arraycopy(args, i, newArgs, 0, newArgs.length);

                    // now process the command
                    processRequirements(sub, commandSender, newArgs);
                    return true;
                }
            }

            // if we've gotten this far, then just use the command we have
            if (nested.parent != null) {
                processRequirements(nested.parent, commandSender, args);
                return true;
            }
        }

        Common.tell(commandSender, unknownCommandMessage);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        // grab the specific command that's being called
        final NestedCommand nested = commands.get(command.getName().toLowerCase());

        if (nested != null) {
            if (args.length == 0 || nested.children.isEmpty()) {
                return nested.parent != null ? nested.parent.tab(sender, args) : null;
            }

            // check for each sub-command that they have access to
            final boolean op = sender.isOp();
            final boolean console = !(sender instanceof Player);

            if (args.length == 1) {
                // suggest sub-commands that this user has access to
                final String arg = args[0].toLowerCase();
                return nested.children.entrySet().stream()
                        .filter(e -> !console || !e.getValue().isNoConsole())
                        .filter(e -> e.getKey().startsWith(arg))
                        .filter(e -> op || e.getValue().getPermissionNode() == null || sender.hasPermission(e.getValue().getPermissionNode()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
            }

            // more than one arg, let's check to see if we have a command here
            final String subCmd = getSubCommand(nested, args);
            Command sub;
            if (subCmd != null && (sub = nested.children.get(subCmd)) != null && (!console || !sub.isNoConsole()) && (op || sub.getPermissionNode() == null || sender.hasPermission(sub.getPermissionNode()))) {
                // adjust the arguments to match - BREAKING!!
                int i = subCmd.indexOf(' ') == -1 ? 1 : 2;
                final String[] newArgs = new String[args.length - i];
                System.arraycopy(args, i, newArgs, 0, newArgs.length);

                // we're good to go!
                return fetchList(sub, newArgs, sender);
            }
        }

        return Collections.emptyList();
    }

    private String getSubCommand(NestedCommand nested, String[] args) {
        final String cmd = args[0].toLowerCase();
        if (nested.children.containsKey(cmd)) {
            return cmd;
        }

        String match = null;
        // support for mulit-argument subcommands
        if (args.length >= 2 && nested.children.keySet().stream().anyMatch(k -> k.indexOf(' ') != -1)) {
            for (int len = args.length; len > 1; --len) {
                final String cmd2 = String.join(" ", Arrays.copyOf(args, len)).toLowerCase();
                if (nested.children.containsKey(cmd2)) {
                    return cmd2;
                }
            }
        }

        // if we don't have a subcommand, should we search for one?
        if (allowLooseCommands) {
            // do a "closest match"
            int count = 0;
            for (String c : nested.children.keySet()) {
                if (c.startsWith(cmd)) {
                    match = c;
                    if (++count > 1) {
                        // there can only be one!
                        match = null;
                        break;
                    }
                }
            }
        }

        return match;
    }

    private void processRequirements(Command command, CommandSender sender, String[] args) {
        if (!(sender instanceof ConsoleCommandSender) && command.getAllowedExecutor() == AllowedExecutor.CONSOLE) {
            Common.tell(sender, consoleOnlyMessage);
            return;
        }

        if (!(sender instanceof Player) && command.isNoConsole()) {
            Common.tell(sender, playerOnlyMessage);
            return;
        }

        if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
            final ReturnType returnType = command.execute(sender, args);

            if (returnType == ReturnType.REQUIRES_PLAYER) {
                Common.tell(sender, playerOnlyMessage);
                return;
            }

            if (returnType == ReturnType.REQUIRES_CONSOLE) {
                Common.tell(sender, consoleOnlyMessage);
                return;
            }

            if (returnType == ReturnType.INVALID_SYNTAX) {
                for (String s : syntaxErrorMessages) {
                    Common.tell(sender, s.replace("%syntax%", command.getSyntax()));
                }
            }

            return;
        }

        Common.tell(sender, noPermissionMessage);
    }

    private List<String> fetchList(Command Command, String[] args, CommandSender sender) {
        final List<String> list = Command.tab(sender, args);

        if (args.length != 0) {
            final String str = args[args.length - 1];

            if (list != null && str != null && str.length() >= 1) {
                try {
                    list.removeIf(s -> !s.toLowerCase().startsWith(str.toLowerCase()));
                } catch (UnsupportedOperationException ignore) {
                }
            }
        }

        return list;
    }

    public static void registerCommandDynamically(Plugin plugin, String command, CommandExecutor executor, TabCompleter tabManager) {
        try {
            // Retrieve the SimpleCommandMap from the server
            Class<?> clazzCraftServer = Bukkit.getServer().getClass();
            Object craftServer = clazzCraftServer.cast(Bukkit.getServer());
            SimpleCommandMap commandMap = (SimpleCommandMap) craftServer.getClass()
                    .getDeclaredMethod("getCommandMap").invoke(craftServer);

            // Construct a new Command object
            Constructor<PluginCommand> constructorPluginCommand = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructorPluginCommand.setAccessible(true);
            PluginCommand commandObject = constructorPluginCommand.newInstance(command, plugin);

            // If we're on Paper 1.8, we need to register timings (spigot creates timings on init, paper creates it on register)
            // later versions of paper create timings if needed when the command is executed
            if (ServerProject.isServer(ServerProject.PAPER, ServerProject.TACO) && ServerVersion.isServerVersionBelow(ServerVersion.V1_9)) {
                Class<?> clazz = Class.forName("co.aikar.timings.TimingsManager");
                Method method = clazz.getMethod("getCommandTiming", String.class, Command.class);
                Field field = PluginCommand.class.getField("timings");

                field.set(commandObject, method.invoke(null, plugin.getName().toLowerCase(), commandObject));
            }

            // Set command action
            commandObject.setExecutor(executor);

            // Set tab complete
            commandObject.setTabCompleter(tabManager);

            // Register the command
            Field fieldKnownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            fieldKnownCommands.setAccessible(true);
            Map<String, org.bukkit.command.Command> knownCommands = (Map<String, org.bukkit.command.Command>) fieldKnownCommands.get(commandMap);
            knownCommands.put(command, commandObject);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }
}
