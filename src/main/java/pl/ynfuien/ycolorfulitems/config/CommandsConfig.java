package pl.ynfuien.ycolorfulitems.config;

import org.bukkit.configuration.ConfigurationSection;
import pl.ynfuien.ydevlib.config.ConfigObject;
import pl.ynfuien.ydevlib.messages.YLogger;

public class CommandsConfig {
    private final ConfigObject config;

    // Itemname
    private boolean itemnameCompletionsMiniMessage;
    private boolean itemnameSignatures;
    private boolean itemnameDisablePAPI;

    // Itemlore
    private boolean itemloreClearConfirm;
    private boolean itemloreCompletionsMiniMessage;
    private int itemloreLineLimit;
    private boolean itemloreDisablePAPI;

    // Editsign
    private boolean editsignAutoWax;
    private boolean editsignCompletionsMiniMessage;
    private int editsignLineLimit;
    private boolean editsignDisablePAPI;
    private boolean checkRegionProtection;

    public CommandsConfig(ConfigObject config) {
        this.config = config;
    }

    public boolean load() {
        ConfigurationSection config = this.config.getConfig().getConfigurationSection("commands");

        // Itemname
        String completionsFormat = config.getString("itemname.completions-format");
        if (!checkCompletionsFormat(completionsFormat)) {
            itemnameCompletionsMiniMessage = false;
            logError("[ItemName] Field 'completions-format' is incorrect! Will be used 'legacy'.");
        } else {
            itemnameCompletionsMiniMessage = completionsFormat.equalsIgnoreCase("minimessage");
        }
        itemnameSignatures = config.getBoolean("itemname.signatures");
        itemnameDisablePAPI = config.getBoolean("itemname.disable-papi");

        // Itemlore
        itemloreClearConfirm = config.getBoolean("itemlore.clear-confirm");
        completionsFormat = config.getString("itemlore.completions-format");
        if (!checkCompletionsFormat(completionsFormat)) {
            itemloreCompletionsMiniMessage = false;
            logError("[ItemLore] Field 'completions-format' is incorrect! Will be used 'legacy'.");
        } else {
            itemloreCompletionsMiniMessage = completionsFormat.equalsIgnoreCase("minimessage");
        }
        itemloreLineLimit = config.getInt("itemlore.line-limit");
        itemloreDisablePAPI = config.getBoolean("itemlore.disable-papi");

        // Editsign
        editsignAutoWax = config.getBoolean("editsign.auto-wax");
        completionsFormat = config.getString("editsign.completions-format");
        if (!checkCompletionsFormat(completionsFormat)) {
            editsignCompletionsMiniMessage = false;
            logError("[EditSign] Field 'completions-format' is incorrect! Will be used 'legacy'.");
        } else {
            editsignCompletionsMiniMessage = completionsFormat.equalsIgnoreCase("minimessage");
        }
        editsignLineLimit = config.getInt("editsign.line-limit");
        editsignDisablePAPI = config.getBoolean("editsign.disable-papi");
        checkRegionProtection = config.getBoolean("editsign.check-region-protection");

        return true;
    }

    private static void logError(String message) {
        YLogger.warn("[Config] " + message);
    }

    private static boolean checkCompletionsFormat(String format) {
        return format.equalsIgnoreCase("legacy") || format.equalsIgnoreCase("minimessage");
    }


    // Itemname
    public boolean isItemnameCompletionsMiniMessage() {
        return itemnameCompletionsMiniMessage;
    }
    public boolean isItemnameSignatures() {
        return itemnameSignatures;
    }
    public boolean isItemnameDisablePAPI() {
        return itemnameDisablePAPI;
    }

    // Itemlore
    public boolean isItemloreClearConfirm() {
        return itemloreClearConfirm;
    }
    public boolean isItemloreCompletionsMiniMessage() {
        return itemloreCompletionsMiniMessage;
    }
    public int getItemloreLineLimit() {
        return itemloreLineLimit;
    }
    public boolean isItemloreDisablePAPI() {
        return itemloreDisablePAPI;
    }

    // Editsign
    public boolean isEditsignAutoWax() {
        return editsignAutoWax;
    }
    public boolean isEditsignCompletionsMiniMessage() {
        return editsignCompletionsMiniMessage;
    }
    public int getEditsignLineLimit() {
        return editsignLineLimit;
    }
    public boolean isEditsignDisablePAPI() {
        return editsignDisablePAPI;
    }
    public boolean isCheckRegionProtection() {
        return checkRegionProtection;
    }
}
