package pl.ynfuien.ycolorfulitems.commands.itemlore;

import net.kyori.adventure.text.Component;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RemoveSubcommand implements Subcommand {
    private final YCommand command;
    private final CommandsConfig config;

    public RemoveSubcommand(YCommand command) {
        this.command = command;
        this.config = command.getCommandsConfig();
    }

    @Override
    public String permission() {
        return String.format("%s.%s", command.permissionBase, name());
    }

    @Override
    public String name() {
        return "remove";
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
            Lang.Message.COMMAND_ITEMLORE_FAIL_REMOVE_NO_NUMBER.send(sender, placeholders);
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

        Player p = (Player) sender;
        ItemStack itemStack = p.getInventory().getItemInMainHand();
        if (itemStack.getType().isEmpty()) {
            Lang.Message.COMMAND_ITEMLORE_FAIL_NO_ITEM.send(p, placeholders);
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();

        if (!meta.hasLore()) {
            Lang.Message.COMMAND_ITEMLORE_FAIL_NO_LORE.send(p, placeholders);
            return;
        }

        List<Component> lore = meta.lore();
        if (lineNumber > lore.size()) {
            Lang.Message.COMMAND_ITEMLORE_FAIL_REMOVE_LINE_DOESNT_EXIST.send(p, placeholders);
            return;
        }
        lineNumber--;

        Component lineText = lore.remove(lineNumber);

        // API event
        ItemLoreChangeEvent event = new ItemLoreChangeEvent(ItemLoreChangeEvent.Type.REMOVE, p, itemStack, lineNumber, lineText, null);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        meta.lore(lore.isEmpty() ? null : lore);
        itemStack.setItemMeta(meta);

        placeholders.put("line-text", MiniMessage.miniMessage().serialize(lineText));
        Lang.Message.COMMAND_ITEMLORE_SUCCESS_REMOVE.send(p, placeholders);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length > 1) return completions;
        if (!(sender instanceof Player p)) return completions;

        String arg = args[0].toLowerCase();

        // Get item, return if it's air
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType().isEmpty()) return completions;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return completions;

        int loreLines = meta.lore().size();
        for (int i = 1; i < loreLines + 1; i++) {
            String lineNumber = String.valueOf(i);
            if (lineNumber.startsWith(arg)) completions.add(lineNumber);
        }

        return completions;
    }
}
