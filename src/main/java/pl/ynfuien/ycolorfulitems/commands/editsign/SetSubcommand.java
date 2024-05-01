package pl.ynfuien.ycolorfulitems.commands.editsign;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.SignSide;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.ynfuien.ycolorfulitems.CommandsConfig;
import pl.ynfuien.ycolorfulitems.api.event.SignEditEvent;
import pl.ynfuien.ycolorfulitems.colors.ColorFormatter;
import pl.ynfuien.ycolorfulitems.commands.Subcommand;
import pl.ynfuien.ycolorfulitems.commands.YCommand;
import pl.ynfuien.ycolorfulitems.utils.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SetSubcommand implements Subcommand {
    private final YCommand command;
    private final CommandsConfig config;

    public SetSubcommand(YCommand command) {
        this.command = command;
        this.config = command.getCommandsConfig();
    }

    @Override
    public String permission() {
        return String.format("%s.%s", command.permissionBase, name());
    }

    @Override
    public String name() {
        return "set";
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
            Lang.Message.COMMAND_EDITSIGN_FAIL_SET_NO_NUMBER.send(sender, placeholders);
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

        if (args.length < 2) {
            Lang.Message.COMMAND_EDITSIGN_FAIL_SET_NO_TEXT.send(sender, placeholders);
            return;
        }


        Player p = (Player) sender;
        Pair<Sign, SignSide> target = EditsignCommand.getTargetSign(p);
        if (target == null) {
            Lang.Message.COMMAND_EDITSIGN_FAIL_NO_SIGN.send(p, placeholders);
            return;
        }

        Sign sign = target.getLeft();
        SignSide signSide = target.getRight();

        String input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        Component formatted = EditsignCommand.colorFormatter.format(p, input, !config.isEditsignDisablePAPI());

        int lineLimit = config.getEditsignLineLimit();
        placeholders.put("line-limit", lineLimit);
        if (lineLimit > 0 && PlainTextComponentSerializer.plainText().serialize(formatted).length() > lineLimit) {
            Lang.Message.COMMAND_EDITSIGN_FAIL_SET_ABOVE_LIMIT.send(p, placeholders);
            return;
        }

        // API event
        SignEditEvent event = new SignEditEvent(SignEditEvent.Type.SET, sign, signSide, p, lineNumber, formatted, input);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        formatted = event.getFormattedLine();

        signSide.line(lineNumber, formatted);
        if (config.isEditsignAutoWax()) sign.setWaxed(true);
        sign.update();

        placeholders.put("line-text", MiniMessage.miniMessage().serialize(formatted));
        Lang.Message.COMMAND_EDITSIGN_SUCCESS_SET.send(p, placeholders);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length > 2) return completions;
        if (!(sender instanceof Player p)) return completions;


        String arg1 = args[0].toLowerCase();
        if (args.length == 1) {
            for (String number : new String[] {"1", "2", "3", "4"}) {
                if (number.startsWith(arg1)) completions.add(number);
            }

            return completions;
        }

        int lineNumber;
        try {
            lineNumber = Integer.parseInt(arg1);
        } catch (NumberFormatException e) {
            return completions;
        }

        if (lineNumber < 1) return completions;
        if (lineNumber > 4) return completions;


        Pair<Sign, SignSide> target = EditsignCommand.getTargetSign(p);
        if (target == null) return completions;

        Component line = target.getRight().line(lineNumber - 1);
        boolean minimessageFormat = config.isEditsignCompletionsMiniMessage();
        String serialized = minimessageFormat ? MiniMessage.miniMessage().serialize(line) : ColorFormatter.LEGACY_SERIALIZER.serialize(line);

        if (serialized.startsWith(args[1].toLowerCase())) completions.add(serialized);

        return completions;
    }
}
