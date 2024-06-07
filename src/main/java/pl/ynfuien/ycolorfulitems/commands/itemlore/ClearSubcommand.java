package pl.ynfuien.ycolorfulitems.commands.itemlore;

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

import java.util.HashMap;
import java.util.List;

public class ClearSubcommand implements Subcommand {
    private final YCommand command;
    private final CommandsConfig config;

    public ClearSubcommand(YCommand command) {
        this.command = command;
        this.config = command.getCommandsConfig();
    }

    @Override
    public String permission() {
        return String.format("%s.%s", command.permissionBase, name());
    }

    @Override
    public String name() {
        return "clear";
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

        boolean requiredConfirm = config.isItemloreClearConfirm();
        if (requiredConfirm && (args.length == 0 || !args[0].equalsIgnoreCase("-y"))) {
            Lang.Message.COMMAND_ITEMLORE_CONFIRM_CLEAR.send(p, placeholders);
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasLore()) {
            Lang.Message.COMMAND_ITEMLORE_FAIL_NO_LORE.send(p, placeholders);
            return;
        }

        // API event
        ItemLoreChangeEvent event = new ItemLoreChangeEvent(ItemLoreChangeEvent.Type.CLEAR, p, itemStack, -1, null, null);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        meta.lore(null);
        itemStack.setItemMeta(meta);

        Lang.Message.COMMAND_ITEMLORE_SUCCESS_CLEAR.send(p, placeholders);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return null;
    }
}
