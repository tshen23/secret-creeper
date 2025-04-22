package net.neological.secretCreeper.commands;

import net.neological.secretCreeper.SecretCreeper;
import net.neological.secretCreeper.game.SecretCreeperGame;
import net.neological.secretCreeper.game.SecretCreeperPlayer;
import net.neological.secretCreeper.game.enums.Alignment;
import net.neological.secretCreeper.game.enums.Position;
import net.neological.secretCreeper.game.enums.Role;
import net.neological.secretCreeper.items.PlayerItems;
import net.neological.secretCreeper.uiux.Animations;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StartCommand implements CommandExecutor {

    private final PlayerItems pi = new PlayerItems();
    private final Animations an = new Animations();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player cmdSender = (Player) commandSender;
        if (SecretCreeper.instance.tempPlayers.size() != 6) {
            cmdSender.sendMessage("§cNeed to have 6 players added");
            return false;
        }

        // reset all previous roles
        for (SecretCreeperPlayer p: SecretCreeper.instance.tempPlayers) {
            p.setPosition(Position.NONE);
            p.setRole(Role.PLAYER);
            p.setAlignment(Alignment.PLAYER);
        }

        // set president randomly
//        Random r = new Random();
//        SecretCreeperPlayer tempPres = SecretCreeper.instance.tempPlayers.get(r.nextInt(SecretCreeper.instance.tempPlayers.size()));
//        tempPres.setPosition(Position.PRESIDENT);

        SecretCreeperPlayer tempPres = SecretCreeper.instance.tempPlayers.getFirst();
        tempPres.setPosition(Position.PRESIDENT);
        
        // set creeper and charged randomly
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < SecretCreeper.instance.tempPlayers.size(); i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        for (int i = 0; i < 2; i++) {
            SecretCreeper.instance.tempPlayers.get(list.get(i)).setAlignment(Alignment.CREEPER);
            if (i == 0) {
                SecretCreeper.instance.tempPlayers.get(list.get(i)).setRole(Role.CREEPER);
            } else {
                SecretCreeper.instance.tempPlayers.get(list.get(i)).setRole(Role.CHARGED);
            }
        }

        SecretCreeper.instance.currentGame = new SecretCreeperGame(SecretCreeper.instance.tempPlayers);
        an.startAnimation();
        Bukkit.getPlayer(SecretCreeper.instance.currentGame.getPresident().getName()).getInventory().addItem(pi.nominateChancellorButton());
        Bukkit.getPlayer(SecretCreeper.instance.currentGame.getPresident().getName()).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false, false));

        return true;
    }
}
