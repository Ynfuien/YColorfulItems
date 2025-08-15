package pl.ynfuien.ycolorfulitems.hooks;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import pl.ynfuien.ycolorfulitems.YColorfulItems;
import pl.ynfuien.ycolorfulitems.hooks.placeholderapi.PlaceholderAPIHook;
import pl.ynfuien.ycolorfulitems.hooks.superiorskyblock2.SuperiorSkyblock2Hook;
import pl.ynfuien.ycolorfulitems.hooks.towny.TownyHook;
import pl.ynfuien.ycolorfulitems.hooks.worldguard.WorldGuardHook;
import pl.ynfuien.ydevlib.messages.YLogger;

public class Hooks {
    private static PlaceholderAPIHook papiHook = null;
    private static WorldGuardHook worldGuardHook = null;
    private static TownyHook townyHook = null;
    private static SuperiorSkyblock2Hook superiorSkyblockHook = null;

    public static void load(YColorfulItems instance) {
        PluginManager pm = Bukkit.getPluginManager();

        // Register PlaceholderAPI hook
        if (pm.isPluginEnabled("PlaceholderAPI")) {
            papiHook = new PlaceholderAPIHook(instance);
            if (!papiHook.register()) {
                papiHook = null;
                YLogger.error("[Hooks] Something went wrong while registering PlaceholderAPI hook!");
            }
            else {
                YLogger.info("[Hooks] Successfully registered hook for PlaceholderAPI!");
            }
        }

        if (pm.isPluginEnabled("WorldGuard")) {
            worldGuardHook = new WorldGuardHook(instance);
        }

        if (pm.isPluginEnabled("Towny")) {
            townyHook = new TownyHook(instance);
        }

        if (pm.isPluginEnabled("SuperiorSkyblock2")) {
            superiorSkyblockHook = new SuperiorSkyblock2Hook(instance);
        }
    }

    public static boolean canEditSign(Player player, Block block) {
        if (worldGuardHook != null && !worldGuardHook.canEditSign(player, block)) return false;
        if (townyHook != null && !townyHook.canEditSign(player, block)) return false;
        if (superiorSkyblockHook != null && !superiorSkyblockHook.canEditSign(player, block)) return false;

        return true;
    }
}
