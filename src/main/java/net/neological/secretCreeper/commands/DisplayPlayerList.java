package net.neological.secretCreeper.commands;

import net.neological.secretCreeper.SecretCreeper;
import net.neological.secretCreeper.game.SecretCreeperPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DisplayPlayerList implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player cmdSender = (Player) commandSender;
        if (SecretCreeper.instance.tempPlayers.isEmpty()) {
            cmdSender.sendMessage("§aNo players added");
            return true;
        }
        StringBuilder sb = new StringBuilder("§a");
        for (SecretCreeperPlayer p: SecretCreeper.instance.tempPlayers) {
            sb.append(p.getName());
            sb.append("(");
            sb.append(p.getId());
            sb.append(")");
            sb.append(", ");
        }
        cmdSender.sendMessage(sb.toString());
        return true;
    }
}
