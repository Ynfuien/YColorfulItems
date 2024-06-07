package pl.ynfuien.ycolorfulitems.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.ycolorfulitems.Lang;
import pl.ynfuien.ycolorfulitems.YColorfulItems;
import pl.ynfuien.ycolorfulitems.api.event.ItemNameChangeEvent;
import pl.ynfuien.ydevlib.messages.colors.ColorFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemnameCommand extends YCommand {
    public static final String RESET_SUBCOMMAND = "reset";
    private final ColorFormatter colorFormatter = new ColorFormatter(permissionBase + ".formats");

    private static NamespacedKey signatureKey;

    public ItemnameCommand(YColorfulItems instance, String name) {
        super(instance, name);

        signatureKey = new NamespacedKey(instance, "rename-signature");
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (!(sender instanceof Player p)) {
            Lang.Message.COMMAND_ITEMNAME_FAIL_ONLY_PLAYER.send(sender, placeholders);
            return;
        }

        // Usage message
        if (args.length == 0 && !sender.hasPermission(permissionBase+".info")) {
            Lang.Message.COMMAND_ITEMNAME_USAGE.send(sender, placeholders);
            return;
        }

        // No item message
        ItemStack item = p.getInventory().getItemInMainHand();
        Material itemType = item.getType();
        if (itemType.isAir()) {
            if (args.length == 0) {
                Lang.Message.COMMAND_ITEMNAME_USAGE.send(sender, placeholders);
                return;
            }

            Lang.Message.COMMAND_ITEMNAME_FAIL_NO_ITEM.send(sender, placeholders);
            return;
        }

        // Info message
        ItemMeta meta = item.getItemMeta();
        if (args.length == 0) {
            if (!meta.hasDisplayName()) {
                Lang.Message.COMMAND_ITEMNAME_INFO_NO_NAME.send(sender, placeholders);
                return;
            }

            Component displayname = meta.displayName().decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET);
            UUID signature = getSignature(item);
            placeholders.put("signature-uuid", "none");
            placeholders.put("signature-username", "none");
            if (signature != null) {
                placeholders.put("signature-uuid", signature);
                placeholders.put("signature-username", Bukkit.getOfflinePlayer(signature).getName());
            }

            placeholders.put("name-minimessage", MiniMessage.miniMessage().serialize(displayname));
            placeholders.put("name-legacy", ColorFormatter.LEGACY_SERIALIZER.serialize(displayname));
            Lang.Message.COMMAND_ITEMNAME_INFO.send(sender, placeholders);
            return;
        }

        // Input formatting
        String input = String.join(" ", args);
        Component formatted = null;
        if (!input.equalsIgnoreCase(RESET_SUBCOMMAND)) {
            formatted = colorFormatter.format(p, input, !config.isItemnameDisablePAPI());
            formatted = formatted.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        }

        boolean signatures = config.isItemnameSignatures();
        ItemNameChangeEvent event = new ItemNameChangeEvent(p, item, formatted, input, signatures);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        formatted = event.getDisplayName();
        signatures = event.useSignature();
        Player signaturePlayer = event.getSignaturePlayer();

        // Item name change
        meta.displayName(formatted);
        item.setItemMeta(meta);

        // Success reset message
        if (formatted == null) {
            removeSignature(item);
            Lang.Message.COMMAND_ITEMNAME_SUCCESS_RESET.send(sender, placeholders);
            return;
        }

        if (signatures) setSignature(item, signaturePlayer.getUniqueId());

        // Success message
        placeholders.put("name-minimessage", MiniMessage.miniMessage().serialize(formatted));
        placeholders.put("name-legacy", ColorFormatter.LEGACY_SERIALIZER.serialize(formatted));
        Lang.Message.COMMAND_ITEMNAME_SUCCESS.send(sender, placeholders);
    }


    // Signatures
    public static UUID getSignature(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (!pdc.has(signatureKey)) return null;

        String value = pdc.get(signatureKey, PersistentDataType.STRING);
        return UUID.fromString(value);
    }

    public static void setSignature(ItemStack item, UUID uuid) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        pdc.set(signatureKey, PersistentDataType.STRING, uuid.toString());
        item.setItemMeta(meta);
    }

    public static void removeSignature(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        pdc.remove(signatureKey);
        item.setItemMeta(meta);
    }


    // Adding current item displayname to tab completions
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length > 1) return completions;
        if (!(sender instanceof Player p)) return completions;

        String arg = args[0].toLowerCase();

        // Reset completion
        if ("reset".startsWith(arg)) completions.add("reset");
        // Return if without permission
        if (!sender.hasPermission(permissionBase+".info")) return completions;

        // Get item, return if it's air
        ItemStack item = p.getInventory().getItemInMainHand();
        Material itemType = item.getType();
        if (itemType.isAir()) return completions;

        // Return item's name if no displayname is set
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) {
            String name = itemType.name().toLowerCase().replace('_', ' ');
            if (name.startsWith(arg)) completions.add(capitalize(name));

            return completions;
        }

        // Return serialized displayname
        Component displayname = meta.displayName().decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET);
        boolean minimessageFormat = config.isItemnameCompletionsMiniMessage();
        String serialized = minimessageFormat ? MiniMessage.miniMessage().serialize(displayname) : ColorFormatter.LEGACY_SERIALIZER.serialize(displayname);
        if (serialized.startsWith(arg)) completions.add(serialized);

        return completions;
    }

    // Capitalizes the first letter of every word
    private static String capitalize(final String str) {
        if (str == null) return null;
        if (str.isBlank()) return str;

        char[] chars = str.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        
        for (int i = 0; i < chars.length; i++) {
            char currentChar = chars[i];
            if (currentChar != ' ') continue;

            int nextIndex = i + 1;
            if (chars.length <= nextIndex) continue;
            chars[nextIndex] = Character.toUpperCase(chars[nextIndex]);
        }

        return new String(chars);
    }
}
