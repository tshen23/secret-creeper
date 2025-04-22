package net.neological.secretCreeper.commands;

import net.neological.secretCreeper.game.enums.Position;
import net.neological.secretCreeper.items.PlayerItems;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiveItems implements CommandExecutor{
    private final PlayerItems pi = new PlayerItems();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;
        player.getInventory().addItem(new ItemStack(pi.nominateChancellorButton()));
        player.getInventory().addItem(new ItemStack(pi.voteOnGovernmentButton()));
        player.getInventory().addItem(new ItemStack(pi.legislationButton(Position.PRESIDENT)));
        player.getInventory().addItem(new ItemStack(pi.legislationButton(Position.CHANCELLOR)));
        player.getInventory().addItem(new ItemStack(pi.executeButton()));
        player.getInventory().addItem(new ItemStack(pi.policyPeekButton()));
        player.getInventory().addItem(new ItemStack(pi.governmentCollapseButton()));
        player.getInventory().addItem(new ItemStack(pi.passPresidentButton()));
        player.getInventory().addItem(new ItemStack(pi.creeperAnimationStick()));
        player.getInventory().addItem(new ItemStack(pi.playerAnimationStick()));
        return true;
    }
}