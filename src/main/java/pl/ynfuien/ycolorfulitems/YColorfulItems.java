package pl.ynfuien.ycolorfulitems;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.ynfuien.ycolorfulitems.commands.ItemnameCommand;
import pl.ynfuien.ycolorfulitems.commands.editsign.EditsignCommand;
import pl.ynfuien.ycolorfulitems.commands.itemlore.ItemloreCommand;
import pl.ynfuien.ycolorfulitems.commands.main.MainCommand;
import pl.ynfuien.ycolorfulitems.config.ConfigHandler;
import pl.ynfuien.ycolorfulitems.config.ConfigName;
import pl.ynfuien.ycolorfulitems.hooks.Hooks;
import pl.ynfuien.ycolorfulitems.utils.Lang;
import pl.ynfuien.ycolorfulitems.utils.YLogger;

import java.util.HashMap;
import java.util.List;

public final class YColorfulItems extends JavaPlugin {
    private static YColorfulItems instance;
    private final ConfigHandler configHandler = new ConfigHandler(this);
    private CommandsConfig config;

    private boolean reloading = false;

    @Override
    public void onEnable() {
        instance = this;
        YLogger.setup("<dark_aqua>[<aqua>Y<gradient:#B833FF:#617EFF:#FF479A>ColorfulItems</gradient><dark_aqua>] <white>", getComponentLogger());

        loadConfigs();
        loadLang();
        config = new CommandsConfig(configHandler.get(ConfigName.CONFIG));
        config.load();

        setupCommands();

        Hooks.load(this);


        YLogger.info("Plugin successfully <green>enabled<white>!");
    }

    @Override
    public void onDisable() {
        YLogger.info("Plugin successfully <red>disabled<white>!");
    }

    private void setupCommands() {
        HashMap<String, CommandExecutor> commands = new HashMap<>() {{
            put("ycolorfulitems", new MainCommand(instance, "main"));
            put("itemname", new ItemnameCommand(instance, "itemname"));
            put("itemlore", new ItemloreCommand(instance, "itemlore"));
            put("editsign", new EditsignCommand(instance, "editsign"));
        }};

        for (String name : commands.keySet()) {
            CommandExecutor cmd = commands.get(name);

            getCommand(name).setExecutor(cmd);
            getCommand(name).setTabCompleter((TabCompleter) cmd);
        }
    }


    private void loadLang() {
        // Get lang config
        FileConfiguration config = configHandler.getConfig(ConfigName.LANG);

        // Reload lang
        Lang.loadLang(config);
    }

    private void loadConfigs() {
        configHandler.load(ConfigName.CONFIG, true, false, List.of("commands.entity.fields"));
        configHandler.load(ConfigName.LANG, true, true);
    }

    public boolean reloadPlugin() {
        reloading = true;

        // Reload all configs
        configHandler.reloadAll();

        config.load();

        // Reload lang
        instance.loadLang();

        reloading = false;
        return true;
    }

    public boolean isReloading() {
        return reloading;
    }

    public static YColorfulItems getInstance() {
        return instance;
    }
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
    public CommandsConfig getCommandsConfig() {
        return config;
    }
}
