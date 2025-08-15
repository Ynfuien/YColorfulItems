package pl.ynfuien.ycolorfulitems.hooks.superiorskyblock2;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import pl.ynfuien.ycolorfulitems.YColorfulItems;

public class SuperiorSkyblock2Hook implements Listener {
    private final YColorfulItems instance;

    public SuperiorSkyblock2Hook(YColorfulItems instance) {
        this.instance = instance;
    }

    public boolean canEditSign(Player player, Block block) {
        Island island = SuperiorSkyblockAPI.getGrid().getIslandAt(block.getLocation());
        return island.hasPermission(player, IslandPrivilege.getByName("SIGN_INTERACT"));
    }
}
