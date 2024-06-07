package pl.ynfuien.ycolorfulitems.commands.editsign;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.SignSide;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.ynfuien.ycolorfulitems.Lang;
import pl.ynfuien.ycolorfulitems.commands.Subcommand;
import pl.ynfuien.ycolorfulitems.commands.YCommand;
import pl.ynfuien.ycolorfulitems.config.CommandsConfig;
import pl.ynfuien.ydevlib.messages.colors.ColorFormatter;

import java.util.HashMap;
import java.util.List;

public class ShowSubcommand implements Subcommand {
    private final YCommand command;
    private final CommandsConfig config;

    public ShowSubcommand(YCommand command) {
        this.command = command;
        this.config = command.getCommandsConfig();
    }

    @Override
    public String permission() {
        return String.format("%s.%s", command.permissionBase, name());
    }

    @Override
    public String name() {
        return "show";
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void run(CommandSender sender, String[] args, HashMap<String, Object> placeholders) {
        Player p = (Player) sender;
        Pair<Sign, SignSide> target = EditsignCommand.getTargetSign(p);
        if (target == null) {
            Lang.Message.COMMAND_EDITSIGN_FAIL_NO_SIGN.send(p, placeholders);
            return;
        }

        SignSide signSide = target.getRight();

        Lang.Message.COMMAND_EDITSIGN_SHOW_HEADER.send(p, placeholders);
        List<Component> lines = signSide.lines();
        for (int i = 0; i < lines.size(); i++) {
            Component line = lines.get(i);
            placeholders.put("line-number", i + 1);
            placeholders.put("line-text-legacy", ColorFormatter.LEGACY_SERIALIZER.serialize(line));
            placeholders.put("line-text-minimessage", MiniMessage.miniMessage().serialize(line));

            Lang.Message.COMMAND_EDITSIGN_SHOW_LINE.send(p, placeholders);
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return null;
    }
}
