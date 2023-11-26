package pl.ynfuien.ycolorfulitems.commands.main;

import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.command.CommandSender;
import pl.ynfuien.ycolorfulitems.YColorfulItems;
import pl.ynfuien.ycolorfulitems.commands.Subcommand;
import pl.ynfuien.ycolorfulitems.commands.YCommand;
import pl.ynfuien.ycolorfulitems.utils.Lang;

import java.util.HashMap;
import java.util.List;

public class VersionSubcommand implements Subcommand {
    private final YCommand command;
    public VersionSubcommand(YCommand command) {
        this.command = command;
    }

    @Override
    public String permission() {
        return String.format("%s.%s", command.permissionBase, name());
    }

    @Override
    public String name() {
        return "version";
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
        PluginMeta info = YColorfulItems.getInstance().getPluginMeta();

        placeholders.put("name", info.getName());
        placeholders.put("version", info.getVersion());
        placeholders.put("author", info.getAuthors().get(0));
        placeholders.put("description", info.getDescription());
        placeholders.put("website", info.getWebsite());

        Lang.Message.COMMAND_MAIN_VERSION.send(sender, placeholders);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return null;
    }
}
