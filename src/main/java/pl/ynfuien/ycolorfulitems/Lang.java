package pl.ynfuien.ycolorfulitems;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import pl.ynfuien.ydevlib.messages.LangBase;
import pl.ynfuien.ydevlib.messages.Messenger;
import pl.ynfuien.ydevlib.messages.colors.ColorFormatter;

import java.util.HashMap;

public class Lang extends LangBase {
    public enum Message implements LangBase.Message {
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
        COMMAND_EDITSIGN_FAIL_PROTECTED_REGION,
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

        ;

        /**
         * Gets name/path of this message.
         */
        @Override
        public String getName() {
            return name().toLowerCase().replace('_', '-');
        }

        /**
         * Gets original unformatted message.
         */
        public String get() {
            return Lang.get(getName());
        }

        /**
         * Gets message with parsed:
         * - {prefix} placeholder
         * - additional provided placeholders
         */
        public String get(HashMap<String, Object> placeholders) {
            return Lang.get(getName(), placeholders);
        }

        /**
         * Gets message with parsed:
         * - PlaceholderAPI
         * - {prefix} placeholder
         * - additional provided placeholders
         */
        public String get(CommandSender sender, HashMap<String, Object> placeholders) {
            return ColorFormatter.parsePAPI(sender, Lang.get(getName(), placeholders));
        }

        /**
         * Gets message as component with parsed:
         * - MiniMessage
         * - PlaceholderAPI
         * - {prefix} placeholder
         * - additional provided placeholders
         */
        public Component getComponent(CommandSender sender, HashMap<String, Object> placeholders) {
            return Messenger.parseMessage(sender, Lang.get(getName()), placeholders);
        }

        /**
         * Sends this message to provided sender.<br/>
         * Parses:<br/>
         * - MiniMessage<br/>
         * - PlaceholderAPI<br/>
         * - {prefix} placeholder
         */
        public void send(CommandSender sender) {
            this.send(sender, new HashMap<>());
        }

        /**
         * Sends this message to provided sender.<br/>
         * Parses:<br/>
         * - MiniMessage<br/>
         * - PlaceholderAPI<br/>
         * - {prefix} placeholder<br/>
         * - additional provided placeholders
         */
        public void send(CommandSender sender, HashMap<String, Object> placeholders) {
            Lang.sendMessage(sender, this, placeholders);
        }
    }
}
