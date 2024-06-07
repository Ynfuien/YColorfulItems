package pl.ynfuien.ycolorfulitems.commands.editsign;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.HangingSign;
import org.bukkit.block.data.type.WallHangingSign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.ycolorfulitems.Lang;
import pl.ynfuien.ycolorfulitems.YColorfulItems;
import pl.ynfuien.ycolorfulitems.commands.Subcommand;
import pl.ynfuien.ycolorfulitems.commands.YCommand;
import pl.ynfuien.ydevlib.messages.colors.ColorFormatter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EditsignCommand extends YCommand {
    public static ColorFormatter colorFormatter;

    public final Subcommand[] subcommands = {
            new ClearSubcommand(this),
            new SetSubcommand(this),
            new ShowSubcommand(this)
    };
    public EditsignCommand(YColorfulItems instance, String name) {
        super(instance, name);

        colorFormatter = new ColorFormatter(permissionBase + ".formats");
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (!(sender instanceof Player)) {
            Lang.Message.COMMAND_EDITSIGN_FAIL_ONLY_PLAYER.send(sender, placeholders);
            return;
        }

        // Run help subcommand if none is provided
        if (args.length == 0) {
            Lang.Message.COMMAND_EDITSIGN_USAGE.send(sender, placeholders);
            return;
        }

        // Loop through and check every subcommand
        String arg1 = args[0].toLowerCase();
        for (Subcommand cmd : subcommands) {
            if (!cmd.name().equals(arg1)) continue;

            if (!sender.hasPermission(cmd.permission())) {
                Lang.Message.COMMAND_EDITSIGN_FAIL_NO_PERMISSIONS.send(sender, placeholders);
                return;
            }

            String[] argsLeft = Arrays.copyOfRange(args, 1, args.length);
            cmd.run(sender, argsLeft, placeholders);
            return;
        }

        Lang.Message.COMMAND_EDITSIGN_USAGE.send(sender, placeholders);
    }

    public static Pair<Sign, SignSide> getTargetSign(Player p) {
        RayTraceResult result = p.rayTraceBlocks(5, FluidCollisionMode.NEVER);
        if (result == null) return null;

        Block b = result.getHitBlock();
        if (b == null) return null;

        BlockState state = b.getState();
        if (!(state instanceof Sign sign)) return null;

        BlockFace signFront = getFrontDirection(state.getBlockData());

        Location signLocation = b.getLocation().clone().toCenterLocation();
        signLocation.setDirection(signFront.getDirection());

        Location playerLocation = p.getLocation();

        // Some magic math, that I don't really understand,
        // but it calculates an angle between sign and a player.
        // So when player stands directly in front of the sign, angle is 90°.
        // directly behind - 270°
        // on the left - 0°
        // on the right - 180°
        // Magic supported by this post: https://www.spigotmc.org/threads/angle-between-2-vectors.350524/#post-3245439
        Vector inBetween = playerLocation.clone().subtract(signLocation).toVector();
        double angle = Math.atan2(inBetween.getZ(), inBetween.getX()) / 2 / Math.PI * 360 + 90;
        angle = (angle + 360) % 360;
        angle = (angle - 90 - signLocation.getYaw() + 360) % 360;

        Side side = angle < 180 ? Side.FRONT : Side.BACK;


        return Pair.of(sign, sign.getSide(side));
    }

    public static BlockFace getFrontDirection(BlockData data) {
        // Sign
        if (data instanceof org.bukkit.block.data.type.Sign) {
            return ((org.bukkit.block.data.type.Sign) data).getRotation();
        }

        if (data instanceof WallSign) {
            return ((WallSign) data).getFacing();
        }

        // Hanging sign
        if (data instanceof HangingSign) {
            return ((HangingSign) data).getRotation();
        }

        if (data instanceof WallHangingSign) {
            return ((WallHangingSign) data).getFacing();
        }

        return BlockFace.NORTH;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return tabCompleteSubcommands(sender, subcommands, args);
    }
}
