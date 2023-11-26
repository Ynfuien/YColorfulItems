package pl.ynfuien.ycolorfulitems.commands.itemlore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.ynfuien.ycolorfulitems.CommandsConfig;
import pl.ynfuien.ycolorfulitems.api.event.ItemLoreChangeEvent;
import pl.ynfuien.ycolorfulitems.commands.Subcommand;
import pl.ynfuien.ycolorfulitems.commands.YCommand;
import pl.ynfuien.ycolorfulitems.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddSubcommand implements Subcommand {
    private final YCommand command;
    private final CommandsConfig config;

    public AddSubcommand(YCommand command) {
        this.command = command;
        this.config = command.getCommandsConfig();
    }

    @Override
    public String permission() {
        return String.format("%s.%s", command.permissionBase, name());
    }

    @Override
    public String name() {
        return "add";
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void run(CommandSender sender, String[] args, HashMap<String, Object> placeholders) {
        if (args.length == 0) {
            Lang.Message.COMMAND_ITEMLORE_FAIL_ADD_NO_LORE.send(sender, placeholders);
            return;
        }

        Player p = (Player) sender;
        ItemStack itemStack = p.getInventory().getItemInMainHand();
        if (itemStack.getType().isEmpty()) {
            Lang.Message.COMMAND_ITEMLORE_FAIL_NO_ITEM.send(p, placeholders);
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();

        List<Component> lore = new ArrayList<>();
        if (meta.hasLore()) lore = meta.lore();

        int lineLimit = config.getItemloreLineLimit();
        if (lineLimit > 0 && lore.size() + 1 > lineLimit) {
            placeholders.put("limit", lineLimit);
            Lang.Message.COMMAND_ITEMLORE_FAIL_ADD_REACH_LIMIT.send(sender, placeholders);
            return;
        }

        String input = String.join(" ", args);
        boolean emptyLine = input.equalsIgnoreCase("-e");

        // Format provided input
        Component formatted = Component.empty();
        if (!emptyLine) {
            formatted = ItemloreCommand.colorFormatter.format(p, input, !config.isItemloreDisablePAPI());
            // Negate italic format where any color is used
            formatted = ItemloreCommand.resetDecorationOnColor(formatted, TextDecoration.ITALIC);
        }

        // API event
        ItemLoreChangeEvent event = new ItemLoreChangeEvent(ItemLoreChangeEvent.Type.ADD, p, itemStack, lore.size(), formatted, input);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        formatted = event.getFormattedLine();

        lore.add(formatted);

        meta.lore(lore);
        itemStack.setItemMeta(meta);

        placeholders.put("line-number", lore.size());
        if (emptyLine) {
            Lang.Message.COMMAND_ITEMLORE_SUCCESS_ADD_EMPTY.send(p, placeholders);
            return;
        }

        placeholders.put("line-text", MiniMessage.miniMessage().serialize(formatted));
        Lang.Message.COMMAND_ITEMLORE_SUCCESS_ADD.send(p, placeholders);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length > 1) return completions;

        if ("-e".startsWith(args[0].toLowerCase())) completions.add("-e");
        return completions;
    }
}
