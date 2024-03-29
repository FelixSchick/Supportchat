package de.illegalaccess.supportchat;

import de.illegalaccess.supportchat.commands.SupportCommand;
import de.illegalaccess.supportchat.listeners.PlayerChatListener;
import de.illegalaccess.supportchat.listeners.PlayerLoginListener;
import de.illegalaccess.supportchat.listeners.PlayerQuitListener;
import de.illegalaccess.supportchat.mysql.MySQL;
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

public final class SupportChat extends Plugin {

    @Getter
    private static SupportChat instance;
    @Getter
    private Configuration config;

    @Getter
    private final String prefix = "§8[§9Support§8] §7";

    @Override
    public void onLoad() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        createFiles();
        MySQL.getInstance().connect();
        MySQL.getInstance().createTables();

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new SupportCommand());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerChatListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerLoginListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerQuitListener());
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
