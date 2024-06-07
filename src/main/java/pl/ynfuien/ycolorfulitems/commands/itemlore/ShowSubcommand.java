package pl.ynfuien.ycolorfulitems.commands.itemlore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.ynfuien.ycolorfulitems.Lang;
import pl.ynfuien.ycolorfulitems.commands.Subcommand;
import pl.ynfuien.ycolorfulitems.commands.YCommand;
import pl.ynfuien.ycolorfulitems.config.CommandsConfig;
import pl.ynfuien.ydevlib.messages.colors.ColorFormatter;

import java.util.HashMap;
import java.util.List;

public class ShowSubcommand implements Subcommand {
    private final YCommand command;
    private final CommandsConfig config;

    public ShowSubcommand(YCommand command) {
        this.command = command;
        this.config = command.getCommandsConfig();
    }

    @Override
    public String permission() {
        return String.format("%s.%s", command.permissionBase, name());
    }

    @Override
    public String name() {
        return "show";
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

        Lang.Message.COMMAND_ITEMLORE_SHOW_HEADER.send(p, placeholders);
        Lang.Message lineMessage = Lang.Message.COMMAND_ITEMLORE_SHOW_LINE;
        for (int i = 0; i < lore.size(); i++) {
            Component line = lore.get(i);

            placeholders.put("line-number", i + 1);
            placeholders.put("line-text-minimessage", MiniMessage.miniMessage().serialize(line));
            placeholders.put("line-text-legacy", ColorFormatter.LEGACY_SERIALIZER.serialize(line));
            lineMessage.send(p, placeholders);
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return null;
    }
}
