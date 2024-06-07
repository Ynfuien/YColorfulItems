package pl.ynfuien.ycolorfulitems.commands.editsign;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.SignSide;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.ynfuien.ycolorfulitems.Lang;
import pl.ynfuien.ycolorfulitems.api.event.SignEditEvent;
import pl.ynfuien.ycolorfulitems.commands.Subcommand;
import pl.ynfuien.ycolorfulitems.commands.YCommand;
import pl.ynfuien.ycolorfulitems.config.CommandsConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClearSubcommand implements Subcommand {
    private final YCommand command;
    private final CommandsConfig config;

    public ClearSubcommand(YCommand command) {
        this.command = command;
        this.config = command.getCommandsConfig();
    }

    @Override
    public String permission() {
        return String.format("%s.%s", command.permissionBase, name());
    }

    @Override
    public String name() {
        return "clear";
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
        if (args.length == 0) {
            Lang.Message.COMMAND_EDITSIGN_FAIL_CLEAR_NO_NUMBER.send(sender, placeholders);
            return;
        }

        int lineNumber;
        placeholders.put("line-number", args[0]);
        try {
            lineNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            Lang.Message.COMMAND_EDITSIGN_FAIL_INCORRECT_NUMBER.send(sender, placeholders);
            return;
        }

        placeholders.put("line-number", lineNumber);
        if (lineNumber < 1 || lineNumber > 4) {
            Lang.Message.COMMAND_EDITSIGN_FAIL_INCORRECT_NUMBER.send(sender, placeholders);
            return;
        }
        lineNumber--;

        Player p = (Player) sender;
        Pair<Sign, SignSide> target = EditsignCommand.getTargetSign(p);
        if (target == null) {
            Lang.Message.COMMAND_EDITSIGN_FAIL_NO_SIGN.send(p, placeholders);
            return;
        }

        Sign sign = target.getLeft();
        SignSide signSide = target.getRight();

        Component lineText = signSide.line(lineNumber);
        if (lineText.equals(Component.empty())) {
            Lang.Message.COMMAND_EDITSIGN_FAIL_CLEAR_EMPTY.send(p, placeholders);
            return;
        }

        // API event
        SignEditEvent event = new SignEditEvent(SignEditEvent.Type.CLEAR, sign, signSide, p, lineNumber, lineText, null);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        signSide.line(lineNumber, Component.empty());
        sign.update();

        placeholders.put("line-text", MiniMessage.miniMessage().serialize(lineText));
        Lang.Message.COMMAND_EDITSIGN_SUCCESS_CLEAR.send(p, placeholders);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length > 1) return completions;
        if (!(sender instanceof Player)) return completions;

        String arg1 = args[0].toLowerCase();
        for (String number : new String[] {"1", "2", "3", "4"}) {
            if (number.startsWith(arg1)) completions.add(number);
        }

        return completions;
    }
}
