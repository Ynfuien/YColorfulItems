package pl.ynfuien.ycolorfulitems.commands.itemlore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.ycolorfulitems.YColorfulItems;
import pl.ynfuien.ycolorfulitems.colors.ColorFormatter;
import pl.ynfuien.ycolorfulitems.commands.Subcommand;
import pl.ynfuien.ycolorfulitems.commands.YCommand;
import pl.ynfuien.ycolorfulitems.utils.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ItemloreCommand extends YCommand {
    public static ColorFormatter colorFormatter;

    public final Subcommand[] subcommands = {
            new ShowSubcommand(this),
            new AddSubcommand(this),
            new SetSubcommand(this),
            new RemoveSubcommand(this),
            new ClearSubcommand(this)
    };
    public ItemloreCommand(YColorfulItems instance, String name) {
        super(instance, name);

        colorFormatter = new ColorFormatter(permissionBase + ".formats");
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (!(sender instanceof Player)) {
            Lang.Message.COMMAND_ITEMLORE_FAIL_ONLY_PLAYER.send(sender, placeholders);
            return;
        }

        // Run help subcommand if none is provided
        if (args.length == 0) {
            Lang.Message.COMMAND_ITEMLORE_USAGE.send(sender, placeholders);
            return;
        }

        // Loop through and check every subcommand
        String arg1 = args[0].toLowerCase();
        for (Subcommand cmd : subcommands) {
            if (!cmd.name().equals(arg1)) continue;

            if (!sender.hasPermission(cmd.permission())) {
                Lang.Message.COMMAND_ITEMLORE_FAIL_NO_PERMISSIONS.send(sender, placeholders);
                return;
            }

            String[] argsLeft = Arrays.copyOfRange(args, 1, args.length);
            cmd.run(sender, argsLeft, placeholders);
            return;
        }

        Lang.Message.COMMAND_ITEMLORE_USAGE.send(sender, placeholders);
    }

    public static Component resetDecorationOnColor(Component component, TextDecoration decoration) {
        List<Component> children = new ArrayList<>(component.children());

        for (int i = 0; i < children.size(); i++) {
            Component child = children.get(i);

            TextDecoration.State state = child.decoration(decoration);
            if (!state.equals(TextDecoration.State.NOT_SET) || child.color() == null) {
                children.set(i, resetDecorationOnColor(child, decoration));
                continue;
            }

            children.set(i, child.decoration(decoration, TextDecoration.State.FALSE));
        }

        return component.children(children);
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return tabCompleteSubcommands(sender, subcommands, args);
    }
}
