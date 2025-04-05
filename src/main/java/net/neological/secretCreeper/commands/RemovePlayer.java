package net.neological.secretCreeper.commands;

import net.neological.secretCreeper.SecretCreeper;
import net.neological.secretCreeper.game.SecretCreeperPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RemovePlayer implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player cmdSender = (Player) commandSender;
        if (strings.length != 1) {
            cmdSender.sendMessage("§cUsage: /removeplayer <SecretCreeperGameId>");
            return false;
        }

        // copied code, check if arg is an int
        int length = strings[0].length();
        if (length == 0) {
            cmdSender.sendMessage("§cGiven id is not an integer");
            return false;
        }
        int i = 0;
        if (strings[0].charAt(0) == '-') {
            if (length == 1) {
                cmdSender.sendMessage("§cGiven id is not an integer");
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = strings[0].charAt(i);
            if (c < '0' || c > '9') {
                cmdSender.sendMessage("§cGiven id is not an integer");
                return false;
            }
        }

        boolean removed = false;
        for (SecretCreeperPlayer p: SecretCreeper.instance.tempPlayers) {
            if (removed) {
                int temp = p.getId() - 1;
                p.setId(temp);
            } else if (p.getId() == Integer.parseInt(strings[0])) {
                cmdSender.sendMessage("§2Player " + p.getName() + " with id " + p.getId() + " successfully removed.");
                removed = true;
            }
        }
        if (removed) {
            SecretCreeper.instance.tempPlayers.remove(Integer.parseInt(strings[0]));
            return true;
        }
        cmdSender.sendMessage("§cNo player with given id found");
        return false;
    }
}
