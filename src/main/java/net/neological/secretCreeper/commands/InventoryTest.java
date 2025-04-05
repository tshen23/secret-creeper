package net.neological.secretCreeper.commands;

import net.neological.secretCreeper.SecretCreeper;
import net.neological.secretCreeper.game.SecretCreeperGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryTest implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;
        Inventory inv = Bukkit.createInventory(null, 27);
        inv.setItem(0, new ItemStack(Material.STONE));
        player.openInventory(inv);
        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem().getType() == Material.STONE) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            Location loc = player.getLocation();
            player.getWorld().createExplosion(loc, 3);
        }
    }
}
