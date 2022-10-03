package de.bravemc.supportchat;

import de.bravemc.coreapi.utils.MessageUtil;
import de.bravemc.coreapi.utils.UUIDFetcher;
import de.bravemc.coreapi.utils.enums.Module;
import de.bravemc.supportchat.commands.SupportCommand;
import de.bravemc.supportchat.listeners.PlayerChatListener;
import de.bravemc.supportchat.listeners.PlayerLoginListener;
import de.bravemc.supportchat.listeners.PlayerQuitListener;
import de.bravemc.supportchat.mysql.MySQL;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Getter
public final class SupportChat extends Plugin {

    @Getter
    private static SupportChat instance;
    private Configuration config;

    private final String prefix = MessageUtil.getPrefix(Module.SUPPORT);

    private UUIDFetcher uuidFetcher;

    @Override
    public void onLoad() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        instance = this;
        uuidFetcher = new UUIDFetcher(1);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        createFiles();
        MySQL.getInstance().connect();
        MySQL.getInstance().createTables();

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new SupportCommand());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerChatListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerLoginListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerQuitListener());
    }

    @Override
    public void onDisable() {
    }

    private void createFiles() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
