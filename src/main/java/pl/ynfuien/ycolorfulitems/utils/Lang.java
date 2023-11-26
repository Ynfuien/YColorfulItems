package pl.ynfuien.ycolorfulitems.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class Lang {
    private static String prefix;
    private static FileConfiguration langConfig;

    public static void loadLang(FileConfiguration langConfig) {
        Lang.langConfig = langConfig;
        prefix = Message.PREFIX.get();
    }

    // Gets message by message enum
    @Nullable
    public static String get(Message message) {
        return get(message.getName());
    }
    // Gets message by path
    @Nullable
    public static String get(String path) {
        return langConfig.getString(path);
    }
    // Gets message by path and replaces placeholders
    @Nullable
    public static String get(String path, HashMap<String, Object> placeholders) {
        placeholders.put("prefix", prefix);
        // Return message with used placeholders
        return Messenger.parsePluginPlaceholders(langConfig.getString(path), placeholders);
    }

    public static void sendMessage(CommandSender sender, Message message) {
        sendMessage(sender, message.getName());
    }
    public static void sendMessage(CommandSender sender, String path) {
        sendMessage(sender, path, new HashMap<>());
    }
    public static void sendMessage(CommandSender sender, String path, HashMap<String, Object> placeholders) {
        List<String> messages;

        if (langConfig.isList(path)) {
            messages = langConfig.getStringList(path);
        } else {
            messages = List.of(langConfig.getString(path));
            if (messages.get(0) == null) {
                YLogger.error(String.format("There is no message '%s'!", path));
                return;
            }
        }

        for (String message : messages) {
            // Return if message is empty
            if (message.isEmpty()) continue;

            // Get message with used placeholders
            placeholders.put("prefix", prefix);
//            message = Messenger.replacePlaceholders(message, placeholders);

            Messenger.send(sender, message, placeholders);
        }
    }

    // Messages enum
    public enum Message {
        PREFIX,
        PLUGIN_IS_RELOADING,

        // Main command
        COMMAND_MAIN_USAGE,
        COMMAND_MAIN_FAIL_NO_PERMISSION,
        COMMAND_MAIN_RELOAD_FAIL,
        COMMAND_MAIN_RELOAD_SUCCESS,
        COMMAND_MAIN_VERSION,

        //<editor-fold desc="Itemname" defaultstate="collapsed">
        COMMAND_ITEMNAME_USAGE,
        COMMAND_ITEMNAME_INFO,
        COMMAND_ITEMNAME_INFO_NO_NAME,
        COMMAND_ITEMNAME_FAIL_ONLY_PLAYER,
        COMMAND_ITEMNAME_FAIL_NO_ITEM,
        COMMAND_ITEMNAME_SUCCESS,
        COMMAND_ITEMNAME_SUCCESS_RESET,
        //</editor-fold>

        //<editor-fold desc="Itemlore" defaultstate="collapsed">
        COMMAND_ITEMLORE_USAGE,
        COMMAND_ITEMLORE_FAIL_NO_PERMISSIONS,
        COMMAND_ITEMLORE_FAIL_ONLY_PLAYER,
        COMMAND_ITEMLORE_FAIL_NO_ITEM,
        COMMAND_ITEMLORE_FAIL_ADD_NO_LORE,
        COMMAND_ITEMLORE_FAIL_ADD_REACH_LIMIT,
        COMMAND_ITEMLORE_FAIL_SET_NO_LORE,
        COMMAND_ITEMLORE_FAIL_SET_NO_NUMBER,
        COMMAND_ITEMLORE_FAIL_SET_REACH_LIMIT,
        COMMAND_ITEMLORE_FAIL_REMOVE_NO_NUMBER,
        COMMAND_ITEMLORE_FAIL_REMOVE_LINE_DOESNT_EXIST,
        COMMAND_ITEMLORE_FAIL_INCORRECT_NUMBER,
        COMMAND_ITEMLORE_FAIL_NO_LORE,
        COMMAND_ITEMLORE_CONFIRM_CLEAR,
        COMMAND_ITEMLORE_SUCCESS_ADD,
        COMMAND_ITEMLORE_SUCCESS_ADD_EMPTY,
        COMMAND_ITEMLORE_SUCCESS_REMOVE,
        COMMAND_ITEMLORE_SUCCESS_SET,
        COMMAND_ITEMLORE_SUCCESS_SET_EMPTY,
        COMMAND_ITEMLORE_SUCCESS_CLEAR,
        COMMAND_ITEMLORE_SHOW_HEADER,
        COMMAND_ITEMLORE_SHOW_LINE,
        //</editor-fold>

        //<editor-fold desc="Editsign" defaultstate="collapsed">
        COMMAND_EDITSIGN_USAGE,
        COMMAND_EDITSIGN_FAIL_NO_PERMISSIONS,
        COMMAND_EDITSIGN_FAIL_ONLY_PLAYER,
        COMMAND_EDITSIGN_FAIL_NO_SIGN,
        COMMAND_EDITSIGN_FAIL_SET_NO_NUMBER,
        COMMAND_EDITSIGN_FAIL_SET_NO_TEXT,
        COMMAND_EDITSIGN_FAIL_SET_ABOVE_LIMIT,
        COMMAND_EDITSIGN_FAIL_CLEAR_NO_NUMBER,
        COMMAND_EDITSIGN_FAIL_CLEAR_EMPTY,
        COMMAND_EDITSIGN_FAIL_INCORRECT_NUMBER,
        COMMAND_EDITSIGN_SUCCESS_SET,
        COMMAND_EDITSIGN_SUCCESS_CLEAR,
        COMMAND_EDITSIGN_SHOW_HEADER,
        COMMAND_EDITSIGN_SHOW_LINE;
        //</editor-fold>


        // Gets message name
        public String getName() {
            return name().toLowerCase().replace('_', '-');
        }

        // Gets message
        public String get() {
            return Lang.get(getName());
        }
        // Gets message with replaced normal placeholders
        public String get(HashMap<String, Object> placeholders) {
            return Lang.get(getName(), placeholders);
        }
        // Gets component message with replaced all placeholders
        public Component get(CommandSender sender, HashMap<String, Object> placeholders) {
            return Messenger.parseMessage(sender, Lang.get(getName()), placeholders);
        }

        // Sends message
        public void send(CommandSender sender) {
            Lang.sendMessage(sender, getName());
        }
        // Sends message with replaced placeholders
        public void send(CommandSender sender, HashMap<String, Object> placeholders) {
            Lang.sendMessage(sender, getName(), placeholders);
        }
    }
}
