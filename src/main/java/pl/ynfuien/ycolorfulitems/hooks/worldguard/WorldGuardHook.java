package pl.ynfuien.ycolorfulitems.hooks.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pl.ynfuien.ycolorfulitems.YColorfulItems;

public class WorldGuardHook {
    private final YColorfulItems instance;
    private final WorldGuardPlugin worldGuardPlugin;
    private final WorldGuardPlatform worldGuardPlatform;
    private final RegionContainer container;

    public WorldGuardHook(YColorfulItems instance) {
        this.instance = instance;
        this.worldGuardPlugin = WorldGuardPlugin.inst();
        this.worldGuardPlatform = WorldGuard.getInstance().getPlatform();
        this.container = worldGuardPlatform.getRegionContainer();
    }

    public boolean canEditSign(Player player, Block block) {
        LocalPlayer localPlayer = worldGuardPlugin.wrapPlayer(player);
        if (worldGuardPlatform.getSessionManager().hasBypass(localPlayer, BukkitAdapter.adapt(player.getWorld()))) return true;

        RegionQuery query = container.createQuery();
        return query.testState(BukkitAdapter.adapt(block.getLocation()), localPlayer, Flags.BUILD);
    }
}
