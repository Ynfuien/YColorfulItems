package pl.ynfuien.ycolorfulitems.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.ycolorfulitems.Lang;
import pl.ynfuien.ycolorfulitems.YColorfulItems;
import pl.ynfuien.ycolorfulitems.config.CommandsConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class YCommand implements CommandExecutor, TabCompleter {
    protected final YColorfulItems instance;
    protected final CommandsConfig config;
    protected final String name;
    public final String permissionBase;

    public YCommand(YColorfulItems instance, String name) {
        this.instance = instance;
        this.config = instance.getCommandsConfig();
        this.name = name;
        this.permissionBase = String.format("%s.%s", instance.getName().toLowerCase(), name);
    }

    public CommandsConfig getCommandsConfig() {
        return config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        HashMap<String, Object> placeholders = new HashMap<>() {{put("command", label);}};

        // Return if plugin is reloading
        if (YColorfulItems.getInstance().isReloading()) {
            Lang.Message.PLUGIN_IS_RELOADING.send(sender, placeholders);
            return true;
        }

        run(sender, args, placeholders);
        return true;
    }

    abstract protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders);

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    protected static List<String> tabCompleteSubcommands(CommandSender sender, Subcommand[] subcommands, String[] args) {
        List<String> completions = new ArrayList<>();

        // Get commands the sender has permissions for
        List<Subcommand> canUse = Arrays.stream(subcommands).filter(cmd -> sender.hasPermission(cmd.permission())).toList();
        if (canUse.isEmpty()) return completions;

        //// Tab completion for subcommands
        String arg1 = args[0].toLowerCase();
        if (args.length == 1) {
            for (Subcommand cmd : canUse) {
                String name = cmd.name();

                if (name.startsWith(args[0])) {
                    completions.add(name);
                }
            }

            return completions;
        }

        //// Tab completion for subcommand arguments

        // Get provided command in first arg
        Subcommand subcommand = canUse.stream().filter(cmd -> cmd.name().equals(arg1)).findAny().orElse(null);
        if (subcommand == null) return completions;

        // Get completions from provided command and return them if there are any
        List<String> subcommandCompletions = subcommand.getTabCompletions(sender, Arrays.copyOfRange(args, 1, args.length));
        if (subcommandCompletions != null) return subcommandCompletions;

        return completions;
    }
}
