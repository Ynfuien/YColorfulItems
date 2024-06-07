package pl.ynfuien.ycolorfulitems.commands.itemlore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.ynfuien.ycolorfulitems.Lang;
import pl.ynfuien.ycolorfulitems.api.event.ItemLoreChangeEvent;
import pl.ynfuien.ycolorfulitems.commands.Subcommand;
import pl.ynfuien.ycolorfulitems.commands.YCommand;
import pl.ynfuien.ycolorfulitems.config.CommandsConfig;
import pl.ynfuien.ydevlib.messages.colors.ColorFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SetSubcommand implements Subcommand {
    private final YCommand command;
    private final CommandsConfig config;

    public SetSubcommand(YCommand command) {
        this.command = command;
        this.config = command.getCommandsConfig();
    }

    @Override
    public String permission() {
        return String.format("%s.%s", command.permissionBase, name());
    }

    @Override
    public String name() {
        return "set";
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
            Lang.Message.COMMAND_ITEMLORE_FAIL_SET_NO_NUMBER.send(sender, placeholders);
            return;
        }

        int lineNumber;
        placeholders.put("line-number", args[0]);
        try {
            lineNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            Lang.Message.COMMAND_ITEMLORE_FAIL_INCORRECT_NUMBER.send(sender, placeholders);
            return;
        }

        placeholders.put("line-number", lineNumber);
        if (lineNumber < 1) {
            Lang.Message.COMMAND_ITEMLORE_FAIL_INCORRECT_NUMBER.send(sender, placeholders);
            return;
        }

        int lineLimit = config.getItemloreLineLimit();
        if (lineLimit > 0 && lineNumber > lineLimit) {
            placeholders.put("limit", lineLimit);
            Lang.Message.COMMAND_ITEMLORE_FAIL_SET_REACH_LIMIT.send(sender, placeholders);
            return;
        }

        if (args.length < 2) {
            Lang.Message.COMMAND_ITEMLORE_FAIL_SET_NO_LORE.send(sender, placeholders);
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

        if (lineNumber > lore.size()) {
            int missingLines = lineNumber - lore.size();
            for (int i = 0; i < missingLines; i++) {
                lore.add(Component.text(""));
            }
        }
        lineNumber--;

        String input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        boolean emptyLine = input.equalsIgnoreCase("-e");

        Component formatted = Component.empty();
        if (!emptyLine) {
            formatted = ItemloreCommand.colorFormatter.format(p, input, !config.isItemloreDisablePAPI());
            // Negate italic format where any color is used
            formatted = ItemloreCommand.resetDecorationOnColor(formatted, TextDecoration.ITALIC);
        }

        // API event
        ItemLoreChangeEvent event = new ItemLoreChangeEvent(ItemLoreChangeEvent.Type.SET, p, itemStack, lineNumber, formatted, input);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        formatted = event.getFormattedLine();

        lore.set(lineNumber, formatted);
        meta.lore(lore);
        itemStack.setItemMeta(meta);

        if (emptyLine) {
            Lang.Message.COMMAND_ITEMLORE_SUCCESS_SET_EMPTY.send(p, placeholders);
            return;
        }

        placeholders.put("line-text", MiniMessage.miniMessage().serialize(formatted));
        Lang.Message.COMMAND_ITEMLORE_SUCCESS_SET.send(p, placeholders);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length > 2) return completions;
        if (!(sender instanceof Player p)) return completions;

        // Get item, return if it's air
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType().isEmpty()) return completions;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return completions;
        List<Component> lore = meta.lore();
        int loreLines = lore.size();

        String arg1 = args[0].toLowerCase();
        if (args.length == 1) {
            for (int i = 1; i < loreLines + 1; i++) {
                String lineNumber = String.valueOf(i);
                if (lineNumber.startsWith(arg1)) completions.add(lineNumber);
            }

            return completions;
        }

        int lineNumber;
        try {
            lineNumber = Integer.parseInt(arg1);
        } catch (NumberFormatException e) {
            return completions;
        }

        if (lineNumber < 1) return completions;
        if (lineNumber > loreLines) return completions;

        String arg2 = args[1].toLowerCase();
        if ("-e".startsWith(arg2)) completions.add("-e");

        Component line = lore.get(lineNumber - 1);
        boolean minimessageFormat = config.isItemloreCompletionsMiniMessage();
        String serialized = minimessageFormat ? MiniMessage.miniMessage().serialize(line) : ColorFormatter.LEGACY_SERIALIZER.serialize(line);

        if (serialized.startsWith(arg2)) completions.add(serialized);

        return completions;
    }
}
