// src/main/java/net/neological/secretCreeper/commands/ChangePresident.java
package net.neological.secretCreeper.commands;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.neological.secretCreeper.SecretCreeper;
import net.neological.secretCreeper.game.SecretCreeperGame;
import net.neological.secretCreeper.game.SecretCreeperPlayer;
import net.neological.secretCreeper.game.enums.Position;
import net.neological.secretCreeper.items.PlayerItems;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


public class ChangePresident implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /changepresident <player_id>").color(TextColor.color(0xFF5555)));
            return false;
        }

        if (!(sender instanceof Player) || !sender.hasPermission("secretcreeper.admin")) {
            sender.sendMessage(Component.text("You do not have permission to use this command.").color(TextColor.color(0xFF5555)));
            return true;
        }

        SecretCreeperGame game = SecretCreeper.instance.currentGame;
        if (game == null) {
            sender.sendMessage(Component.text("No active game found!").color(TextColor.color(0xFF5555)));
            return false;
        }

        int newPresidentId;
        try {
            newPresidentId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Player ID must be a number!").color(TextColor.color(0xFF5555)));
            return false;
        }

        SecretCreeperPlayer currentPresident = game.getPresident();
        if (currentPresident == null) {
            sender.sendMessage(Component.text("There is no current president!").color(TextColor.color(0xFF5555)));
            return false;
        }

        // Check if current president has the nominate chancellor button
        Player currentPresidentPlayer = Bukkit.getPlayer(currentPresident.getName());
        if (currentPresidentPlayer == null || !hasNominateChancellorButton(currentPresidentPlayer)) {
            sender.sendMessage(Component.text("Current president doesn't have the nominate chancellor button!").color(TextColor.color(0xFF5555)));
            return false;
        }

        // Find the new president
        SecretCreeperPlayer newPresident = null;
        for (SecretCreeperPlayer player : game.getPlayers()) {
            if (player.getId() == newPresidentId) {
                newPresident = player;
                break;
            }
        }

        if (newPresident == null) {
            sender.sendMessage(Component.text("Player with ID " + newPresidentId + " not found!").color(TextColor.color(0xFF5555)));
            return false;
        }

        // Change the president
        Player newPresidentPlayer = Bukkit.getPlayer(newPresident.getName());
        if (newPresidentPlayer == null) {
            sender.sendMessage(Component.text("New president player is not online!").color(TextColor.color(0xFF5555)));
            return false;
        }

        // Update positions
        game.setPresident(newPresidentId);

        // Remove nominate chancellor button from current president
        removeNominateChancellorButton(currentPresidentPlayer);

        // Give nominate chancellor button to new president
        PlayerItems items = new PlayerItems();
        newPresidentPlayer.getInventory().addItem(items.nominateChancellorButton());

        // Announce the change
        Component announcement = Component.text("ADMIN ACTION: The presidency has been manually changed from ")
                .color(TextColor.color(0xFFAA00))
                .append(Component.text(currentPresident.getName()).color(TextColor.color(0xFF5555)))
                .append(Component.text(" to ").color(TextColor.color(0xFFAA00)))
                .append(Component.text(newPresident.getName()).color(TextColor.color(0x55FF55)));

        Bukkit.broadcast(announcement);

        sender.sendMessage(Component.text("Successfully changed the president to " + newPresident.getName()).color(TextColor.color(0x55FF55)));
        return true;
    }

    private boolean hasNominateChancellorButton(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType().name().equals("LIGHT_BLUE_DYE") &&
                    item.getItemMeta().hasDisplayName() &&
                    item.getItemMeta().displayName().toString().contains("Nominate Chancellor")) {
                return true;
            }
        }
        return false;
    }

    private void removeNominateChancellorButton(Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType().name().equals("LIGHT_BLUE_DYE") &&
                    item.getItemMeta().hasDisplayName() &&
                    item.getItemMeta().displayName().toString().contains("Nominate Chancellor")) {
                player.getInventory().setItem(i, null);
                break;
            }
        }
    }
}