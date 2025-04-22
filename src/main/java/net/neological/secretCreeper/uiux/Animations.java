package net.neological.secretCreeper.uiux;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.neological.secretCreeper.SecretCreeper;
import net.neological.secretCreeper.game.SecretCreeperGame;
import net.neological.secretCreeper.game.SecretCreeperPlayer;
import net.neological.secretCreeper.game.enums.Alignment;
import net.neological.secretCreeper.game.enums.PolicyEffect;
import net.neological.secretCreeper.game.enums.Role;
import net.neological.secretCreeper.items.PlayerItems;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Animations {

    /** 1: player policy win; 2: player execution win;
     * 3: creeper policy win; 4: creeper chancellor win
     * */
    public void winAnimation(int i) {
        if (i > 4 || i < 1) {
            throw new IllegalArgumentException("Need to be a valid win");
        }

        if (i == 1 || i == 2) {
            Bukkit.broadcast(Component.text("-----------------------------------------------------")
                    .color(TextColor.color(0x555555)));
            Bukkit.broadcast(Component.text("                                  Players Win!")
                    .color(TextColor.color(0x55FFFF)));
            Bukkit.broadcast(Component.text("-----------------------------------------------------")
                    .color(TextColor.color(0x555555)));
            if (i == 1) {
                Bukkit.broadcast(Component.text("Five player policies were passed!")
                        .color(TextColor.color(0x55FFFF)));
            } else {
                Bukkit.broadcast(Component.text("The charged creeper has been executed!")
                        .color(TextColor.color(0x55FFFF)));
            }
            for (SecretCreeperPlayer p: SecretCreeper.instance.currentGame.getPlayers()) {
                Bukkit.getPlayer(p.getName()).clearActivePotionEffects();
                if (p.getAlignment() == Alignment.CREEPER) {
                    Bukkit.getPlayer(p.getName()).setGameMode(GameMode.SPECTATOR);
                    Location loc = Bukkit.getPlayer(p.getName()).getLocation();
                    Bukkit.getPlayer(p.getName()).getWorld().strikeLightningEffect(loc);
                } else {
                    Bukkit.getPlayer(p.getName()).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false, false));
                }
            }
        } else {
            Bukkit.broadcast(Component.text("-----------------------------------------------------")
                    .color(TextColor.color(0x555555)));
            Bukkit.broadcast(Component.text("                                 Creepers Win!")
                    .color(TextColor.color(0x00AA00)));
            Bukkit.broadcast(Component.text("-----------------------------------------------------")
                    .color(TextColor.color(0x555555)));
            if (i == 3) {
                Bukkit.broadcast(Component.text("Six creeper policies were passed!")
                        .color(TextColor.color(0x00AA00)));
            } else {
                Bukkit.broadcast(Component.text("The charged creeper has seized power!")
                        .color(TextColor.color(0x00AA00)));
            }
            for (SecretCreeperPlayer p: SecretCreeper.instance.currentGame.getPlayers()) {
                Bukkit.getPlayer(p.getName()).clearActivePotionEffects();
                if (p.getAlignment() == Alignment.PLAYER) {
                    Bukkit.getPlayer(p.getName()).setGameMode(GameMode.SPECTATOR);
                    Location loc = Bukkit.getPlayer(p.getName()).getLocation();
                    Bukkit.getPlayer(p.getName()).getWorld().strikeLightningEffect(loc);
                } else {
                    Bukkit.getPlayer(p.getName()).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false, false));
                }
            }
        }
    }

    public void passPresidencyAnimation(Player player, String prevPresident, String currPresident) {
        for (SecretCreeperPlayer p: SecretCreeper.instance.currentGame.getPlayers()) {
            Bukkit.getPlayer(p.getName()).clearActivePotionEffects();
        }
        Bukkit.broadcast(Component.text("Presidency Passed!")
                .color(TextColor.color(0x55FF55)));
        Bukkit.broadcast(Component.text(prevPresident + " → " + currPresident));

        player.getInventory().clear();
        Bukkit.getPlayer(currPresident).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false, false));
    }

    public void policyPeakAnimation(Player player) {
        Bukkit.broadcast(Component.text("President " + player.getName() + " has peeked at the top 3 cards of the deck")
                .color(TextColor.color(0xFF55FF)));
    }

    public void governmentCollapseAnimation(Player player, Alignment policy) {
        Bukkit.broadcast(Component.text("-----------------------------------------------------")
                .color(TextColor.color(0x555555)));
        if (policy == Alignment.PLAYER) {
            Bukkit.broadcast(Component.text("                           Player Policy Passed!")
                    .color(TextColor.color(0x55FFFF)));
        } else {
            Bukkit.broadcast(Component.text("                           Creeper Policy Passed!")
                    .color(TextColor.color(0x00AA00)));
        }
        Bukkit.broadcast(Component.text("-----------------------------------------------------")
                .color(TextColor.color(0x555555)));
        Bukkit.broadcast(Component.text("Passed By:")
                .color(TextColor.color(0xFFFFFF)));
        Bukkit.broadcast(Component.text("Government Collapse")
                .color(TextColor.color(0xAA0000)));

        Bukkit.broadcast(Component.text(""));
        if (policy == Alignment.PLAYER) {
            Bukkit.broadcast(Component.text("Player Polices Passed: " + SecretCreeper.instance.currentGame.getPassedPlayerPolicies())
                    .color(TextColor.color(0x55FFFF)));
        } else {
            Bukkit.broadcast(Component.text("Creeper Polices Passed: " + SecretCreeper.instance.currentGame.getPassedCreeperPolicies())
                    .color(TextColor.color(0x00AA00)));
        }
        player.getInventory().clear();
    }

    public void electionAnimation(Player player) {
        SecretCreeperGame game = SecretCreeper.instance.currentGame;

        Bukkit.getPlayer(game.getChancellor().getName())
                .addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false, false));
        player.closeInventory();
        player.getInventory().clear();

        Bukkit.broadcast(Component.text("-----------------------------------------------------")
                .color(TextColor.color(0x555555)));
        Bukkit.broadcast(Component.text("                                 New Election!")
                .color(TextColor.color(0x55FF55)));
        Bukkit.broadcast(Component.text("-----------------------------------------------------")
                .color(TextColor.color(0x555555)));
        Bukkit.broadcast(Component.text("President: " + game.getPresident().getName()));
        Bukkit.broadcast(Component.text("Chancellor: " + game.getChancellor().getName()));
    }

    public void executionAnimation(Player player, SecretCreeperPlayer p) {
        Bukkit.getPlayer(p.getName()).setGameMode(GameMode.SPECTATOR);
        Location loc = Bukkit.getPlayer(p.getName()).getLocation();
        Bukkit.getPlayer(p.getName()).getWorld().strikeLightningEffect(loc);
        Bukkit.broadcast(Component.text("President " + player.getName() + " has executed " + p.getName())
                .color(TextColor.color(0xAA0000)));
        player.closeInventory();
        player.getInventory().clear();
        Bukkit.getPlayer(p.getName()).clearActivePotionEffects();
    }

    public void electionResultsAnimation(Boolean passed, List<String> votedYes, List<String> votedNo) {

        Bukkit.broadcast(Component.text("-----------------------------------------------------")
                .color(TextColor.color(0x555555)));
        if (passed) {
            Bukkit.broadcast(Component.text("                                 Vote Passed!")
                    .color(TextColor.color(0xFFFFFF)));
        } else if (SecretCreeper.instance.currentGame.getElectionTracker() >= 3) {
            Bukkit.broadcast(Component.text("                           Government Collapse!")
                    .color(TextColor.color(0xAA0000)));
        } else {
            Bukkit.broadcast(Component.text("                                  Vote Failed!")
                    .color(TextColor.color(0xAAAAAA)));
        }
        Bukkit.broadcast(Component.text("-----------------------------------------------------")
                .color(TextColor.color(0x555555)));
        Bukkit.broadcast(Component.text("Voted Yes:")
                .color(TextColor.color(0xFFFFFF)));
        StringBuilder yes = new StringBuilder();
        for (String name: votedYes) {
            yes.append(name);
            yes.append(", ");
        }
        if (!yes.isEmpty()) {
            yes.delete(yes.length() - 2, yes.length() - 1);
        }
        Bukkit.broadcast(Component.text(yes.toString())
                .color(TextColor.color(0xFFFFFF)));
        Bukkit.broadcast(Component.text("Voted No:")
                .color(TextColor.color(0xAAAAAA)));
        StringBuilder no = new StringBuilder();
        for (String name: votedNo) {
            no.append(name);
            no.append(", ");
        }
        if (!no.isEmpty()) {
            no.delete(no.length() - 2, no.length() - 1);
        }
        Bukkit.broadcast(Component.text(no.toString())
                .color(TextColor.color(0xAAAAAA)));

        if (passed) {
            return;
        }

        // election tracker
        if (SecretCreeper.instance.currentGame.getElectionTracker() == 1) {
            Bukkit.broadcast(Component.text(""));
            Bukkit.broadcast(Component.text("Election Tracker: ●")
                    .color(TextColor.color(0x55FF55)));
        } else if (SecretCreeper.instance.currentGame.getElectionTracker() == 2) {
            Bukkit.broadcast(Component.text(""));
            Bukkit.broadcast(Component.text("Election Tracker: ●●")
                    .color(TextColor.color(0xFFAA00)));
        } else if (SecretCreeper.instance.currentGame.getElectionTracker() == 3) {
            Bukkit.broadcast(Component.text(""));
            Bukkit.broadcast(Component.text("Election Tracker: ●●●")
                    .color(TextColor.color(0xAA0000)));
        }
    }

    public void legislationAnimation(Alignment policy) {
        Bukkit.broadcast(Component.text("-----------------------------------------------------")
                .color(TextColor.color(0x555555)));
        if (policy == Alignment.PLAYER) {
            Bukkit.broadcast(Component.text("                           Player Policy Passed!")
                    .color(TextColor.color(0x55FFFF)));
        } else {
            Bukkit.broadcast(Component.text("                           Creeper Policy Passed!")
                    .color(TextColor.color(0x00AA00)));
        }
        Bukkit.broadcast(Component.text("-----------------------------------------------------")
                .color(TextColor.color(0x555555)));
        Bukkit.broadcast(Component.text("Passed By:")
                .color(TextColor.color(0xFFFFFF)));
        Bukkit.broadcast(Component.text("President " + SecretCreeper.instance.currentGame.getPresident().getName() + ", Chancellor " + SecretCreeper.instance.currentGame.getChancellor().getName())
                .color(TextColor.color(0xFFFFFF)));
        Bukkit.broadcast(Component.text(""));

        if (policy == Alignment.PLAYER) {
            Bukkit.broadcast(Component.text("Player Polices Passed: " + SecretCreeper.instance.currentGame.getPassedPlayerPolicies())
                    .color(TextColor.color(0x55FFFF)));
            playerBlockAnimation(SecretCreeper.instance.currentGame.getPassedPlayerPolicies() - 1);
        } else {
            Bukkit.broadcast(Component.text("Creeper Polices Passed: " + SecretCreeper.instance.currentGame.getPassedCreeperPolicies())
                    .color(TextColor.color(0x00AA00)));
            creeperBlockAnimation(SecretCreeper.instance.currentGame.getPassedCreeperPolicies() - 1);
        }
    }

    public void creeperBoardAnimation(PolicyEffect e) {
        if (e == PolicyEffect.PEEK) {
            Bukkit.broadcast(Component.text("Policy Peek Triggered")
                    .color(TextColor.color(0xFF55FF)));
        } else if (e == PolicyEffect.EXECUTION) {
            Bukkit.broadcast(Component.text("Execution Triggered")
                    .color(TextColor.color(0xAA0000)));
        }
    }

    public void startAnimation() {
        String charged = "";
        String creeper = "";
        for (SecretCreeperPlayer p: SecretCreeper.instance.currentGame.getPlayers()) {
            if (p.getRole() == Role.CHARGED) {
                charged = p.getName();
            } else if (p.getRole() == Role.CREEPER) {
                creeper = p.getName();
            }
            Bukkit.getPlayer(p.getName()).clearActivePotionEffects();
            Bukkit.getPlayer(p.getName()).setGameMode(GameMode.ADVENTURE);
        }

        World w = Bukkit.getPlayer(SecretCreeper.instance.currentGame.getPlayers().getFirst().getName()).getWorld();

        for (Entity e: w.getEntities()) {
            if (e instanceof Creeper) {
                e.remove();
            } else if (e instanceof Ocelot) {
                e.remove();
            }
        }

        for (SecretCreeperPlayer p: SecretCreeper.instance.currentGame.getPlayers()) {
            if (p.getRole() == Role.PLAYER) {
                Bukkit.getPlayer(p.getName()).sendMessage(Component.text("You are a Player")
                        .color(TextColor.color(0x55FFFF)));
                Bukkit.getPlayer(p.getName()).sendMessage(Component.text("Win by:"));
                Bukkit.getPlayer(p.getName()).sendMessage(Component.text("• Passing 5 player policies"));
                Bukkit.getPlayer(p.getName()).sendMessage(Component.text("• Executing the charged creeper"));
            } else if (p.getRole() == Role.CREEPER) {
                Bukkit.getPlayer(p.getName()).sendMessage(Component.text("You are a Creeper")
                        .color(TextColor.color(0x00AA00)));
                Bukkit.getPlayer(p.getName()).sendMessage(Component.text("Win by:"));
                Bukkit.getPlayer(p.getName()).sendMessage(Component.text("• Passing 6 creeper policies"));
                Bukkit.getPlayer(p.getName()).sendMessage(Component.text("• Getting your charged creeper elected as chancellor (after 3 creeper policies have been passed)"));
                Bukkit.getPlayer(p.getName()).sendMessage(Component.text(""));
                Bukkit.getPlayer(p.getName()).sendMessage(Component.text("Your charged creeper: " + charged)
                        .color(TextColor.color(0x00AA00)));
            } else {
                Bukkit.getPlayer(p.getName()).sendMessage(Component.text("You are the Charged Creeper")
                        .color(TextColor.color(0x00AA00)));
                Bukkit.getPlayer(p.getName()).sendMessage(Component.text("• Passing 6 creeper policies"));
                Bukkit.getPlayer(p.getName()).sendMessage(Component.text("• Getting elected as chancellor (after 3 creeper policies have been passed)"));
                Bukkit.getPlayer(p.getName()).sendMessage(Component.text(""));
                Bukkit.getPlayer(p.getName()).sendMessage(Component.text("Your fellow creeper: " + creeper)
                        .color(TextColor.color(0x00AA00)));
            }
        }

        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(Component.text("Current President:")
                .color(TextColor.color(0x55FF55)));
        Bukkit.broadcast(Component.text(SecretCreeper.instance.currentGame.getPresident().getName()));
    }

    public void creeperBlockAnimation(int i) {
        if (i >= SecretCreeper.instance.creeperBlocks.size()) {
            return;
        }
        Location loc = SecretCreeper.instance.creeperBlocks.get(i).getLocation();
        loc.set(loc.getX() + 0.5, loc.getY() + 1, loc.getZ() + 0.5);
        loc.getWorld().strikeLightningEffect(loc);
        LivingEntity creeper = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.CREEPER);
        creeper.customName(Component.text("Creeper Policy"));
    }

    public void playerBlockAnimation(int i) {
        if (i >= SecretCreeper.instance.playerBlocks.size()) {
            return;
        }
        Location loc = SecretCreeper.instance.playerBlocks.get(i).getLocation();
        loc.set(loc.getX() + 0.5, loc.getY() + 1, loc.getZ() + 0.5);
        loc.getWorld().strikeLightningEffect(loc);
        LivingEntity ocelot = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.OCELOT);
        ocelot.customName(Component.text("Player Policy"));
    }
}
