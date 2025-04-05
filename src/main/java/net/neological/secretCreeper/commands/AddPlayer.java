package net.neological.secretCreeper.commands;

import net.neological.secretCreeper.SecretCreeper;
import net.neological.secretCreeper.game.SecretCreeperPlayer;
import net.neological.secretCreeper.game.enums.Alignment;
import net.neological.secretCreeper.game.enums.Role;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddPlayer implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        Player player = (Player) commandSender;
        int id = SecretCreeper.instance.tempPlayers.size();

        if (strings.length != 1) {
            player.sendMessage("§cUsage: /addplayer <username>");
            return false;
        } else if (Bukkit.getPlayer(strings[0]) == null) {
            player.sendMessage("§c" + strings[0] + " must be a valid player");
            return false;
        }

        SecretCreeper.instance.tempPlayers.add(new SecretCreeperPlayer(id, Bukkit.getPlayer(strings[0]).getName(), Alignment.PLAYER, Role.PLAYER));
        player.sendMessage("§2" + Bukkit.getPlayer(strings[0]).getName() + " with id " + id + " successfully added!");
        return true;
    }
}
