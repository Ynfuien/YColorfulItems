package pl.ynfuien.ycolorfulitems.hooks;

import org.bukkit.Bukkit;
import pl.ynfuien.ycolorfulitems.YColorfulItems;
import pl.ynfuien.ycolorfulitems.hooks.placeholderapi.PlaceholderAPIHook;
import pl.ynfuien.ycolorfulitems.utils.YLogger;

public class Hooks {
    private static PlaceholderAPIHook papiHook = null;

    public static void load(YColorfulItems instance) {
        // Register PlaceholderAPI hook
        if (isPapiEnabled()) {
            papiHook = new PlaceholderAPIHook(instance);
            if (!papiHook.register()) {
                papiHook = null;
                YLogger.error("[Hooks] Something went wrong while registering PlaceholderAPI hook!");
            }
            else {
                YLogger.info("[Hooks] Successfully registered hook for PlaceholderAPI!");
            }
        }
    }

    public static boolean isPapiEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }
    public static boolean isPapiHookEnabled() {
        return papiHook != null;
    }

}
