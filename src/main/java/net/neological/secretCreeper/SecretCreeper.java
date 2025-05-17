package net.neological.secretCreeper;

import net.neological.secretCreeper.commands.*;
import net.neological.secretCreeper.game.SecretCreeperGame;
import net.neological.secretCreeper.game.SecretCreeperPlayer;
import net.neological.secretCreeper.listener.InventoryClickListener;
import net.neological.secretCreeper.listener.PlayerInteractListener;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class SecretCreeper extends JavaPlugin {
    public static SecretCreeper instance;
    public SecretCreeperGame currentGame;
    public List<SecretCreeperPlayer> tempPlayers;
    public List<Block> creeperBlocks;
    public List<Block> playerBlocks;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        tempPlayers = new ArrayList<>();
        creeperBlocks = new ArrayList<>();
        playerBlocks = new ArrayList<>();
        getCommand("start").setExecutor(new StartCommand());
        getCommand("addplayer").setExecutor(new AddPlayer());
        getCommand("displayplayerlist").setExecutor(new DisplayPlayerList());
        getCommand("removeplayer").setExecutor(new RemovePlayer());
        getCommand("giveitems").setExecutor(new GiveItems());
        getCommand("changepresident").setExecutor(new ChangePresident());
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
