package net.neological.secretCreeper;

import net.neological.secretCreeper.commands.*;
import net.neological.secretCreeper.game.SecretCreeperGame;
import net.neological.secretCreeper.game.SecretCreeperPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class SecretCreeper extends JavaPlugin {
    public static SecretCreeper instance;
    public SecretCreeperGame currentGame;
    public List<SecretCreeperPlayer> tempPlayers;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        tempPlayers = new ArrayList<>();
        getCommand("start").setExecutor(new StartCommand());
        getCommand("inventorytest").setExecutor(new InventoryTest());
        getCommand("addplayer").setExecutor(new AddPlayer());
        getCommand("displayplayerlist").setExecutor(new DisplayPlayerList());
        getCommand("removeplayer").setExecutor(new RemovePlayer());
        // getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        // getServer().getPluginManager().registerEvents(new InventoryTest(), this);
        getServer().getPluginManager().registerEvents(new StartCommand(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
