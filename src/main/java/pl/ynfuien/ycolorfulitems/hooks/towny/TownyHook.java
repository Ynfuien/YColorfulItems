package pl.ynfuien.ycolorfulitems.hooks.towny;

import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pl.ynfuien.ycolorfulitems.YColorfulItems;

public class TownyHook {
    private final YColorfulItems instance;

    public TownyHook(YColorfulItems instance) {
        this.instance = instance;
    }

    public boolean canEditSign(Player player, Block block) {
        return PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.BUILD);
    }
}
