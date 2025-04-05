package net.neological.secretCreeper.commands;

import net.kyori.adventure.text.BlockNBTComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.neological.secretCreeper.SecretCreeper;
import net.neological.secretCreeper.game.SecretCreeperGame;
import net.neological.secretCreeper.game.SecretCreeperPlayer;
import net.neological.secretCreeper.game.enums.Alignment;
import net.neological.secretCreeper.game.enums.PolicyEffect;
import net.neological.secretCreeper.game.enums.Position;
import net.neological.secretCreeper.game.enums.Role;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StartCommand implements CommandExecutor, Listener {

    private List<Map.Entry<String, Boolean>> votes = new ArrayList<>();
    private int electionTracker;
    private SecretCreeperPlayer nomChancellor;
    private SecretCreeperPlayer currPresident;
    private List<Alignment> legPolicies;

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
        Random r = new Random();
        currPresident = SecretCreeper.instance.tempPlayers.get(r.nextInt(SecretCreeper.instance.tempPlayers.size()));
        currPresident.setPosition(Position.PRESIDENT);

        // set creeper and charged randomly
        String creeper = "";
        String charged = "";
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < SecretCreeper.instance.tempPlayers.size(); i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        for (int i = 0; i < 2; i++) {
            SecretCreeper.instance.tempPlayers.get(list.get(i)).setAlignment(Alignment.CREEPER);
            if (i == 0) {
                SecretCreeper.instance.tempPlayers.get(list.get(i)).setRole(Role.CREEPER);
                creeper = SecretCreeper.instance.tempPlayers.get(list.get(i)).getName();
            } else {
                SecretCreeper.instance.tempPlayers.get(list.get(i)).setRole(Role.CHARGED);
                charged = SecretCreeper.instance.tempPlayers.get(list.get(i)).getName();
            }
        }

        SecretCreeper.instance.currentGame = new SecretCreeperGame(SecretCreeper.instance.tempPlayers);
        votes = new ArrayList<>();
        electionTracker = 0;

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
        Bukkit.broadcast(Component.text(currPresident.getName()));
        Bukkit.getPlayer(currPresident.getName()).getInventory().addItem(nominateChancellorButton());
        Bukkit.getPlayer(currPresident.getName()).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false, false));
        return true;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // right-clicked something
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getItem() == null) {
                return;
            }

            // NOMINATE CHANCELLOR BUTTON
            if (event.getItem().isSimilar(nominateChancellorButton())) {
                Inventory inv = Bukkit.createInventory(null, 18, Component.text("Nominate Chancellor"));
                int i = 0;
                for (SecretCreeperPlayer p: SecretCreeper.instance.currentGame.getPlayers()) {
                    // check if player not in term limits
                    if (SecretCreeper.instance.currentGame.getTermLimits().length == 0 && p.getPosition() != Position.PRESIDENT) {
                        inv.setItem(i, getHead(p.getName(), p.getId(), "nomChanc"));
                        i++;
                    } else {
                        boolean inLimits = false;
                        for (int id: SecretCreeper.instance.currentGame.getTermLimits()) {
                            if (p.getId() == id) {
                                inLimits = true;
                                break;
                            }
                        }
                        if (!inLimits && p.getPosition() != Position.PRESIDENT) {
                            inv.setItem(i, getHead(p.getName(), p.getId(), "nomChanc"));
                            i++;
                        }
                    }
                }
                event.getPlayer().openInventory(inv);
            }

            // VOTE ON GOVERNMENT BUTTON
            if (event.getItem().isSimilar(voteOnGovernmentButton())) {
                Inventory inv = Bukkit.createInventory(null, 9, Component.text("Vote on Government"));

                // set first slot to president head
                for (SecretCreeperPlayer p: SecretCreeper.instance.currentGame.getPlayers()) {
                    if (p.getPosition() == Position.PRESIDENT) {
                        ItemStack presHead = getHead(p.getName(), p.getId(), "pres");

                        ItemMeta presIm = presHead.getItemMeta();
                        presIm.displayName(Component.text("President")
                                .color(TextColor.color(0xFFFFFF))
                                .decoration(TextDecoration.ITALIC, false)
                        );

                        List<Component> lore = new ArrayList<>();
                        lore.add(Component.text(p.getName())
                                .color(TextColor.color(0xBBBBBB))
                                .decoration(TextDecoration.ITALIC, false));
                        presIm.lore(lore);
                        presHead.setItemMeta(presIm);

                        inv.setItem(0, presHead);
                    }
                }

                // set second slot to chancellor head
                ItemStack chancHead = getHead(nomChancellor.getName(), nomChancellor.getId(), "chanc");

                ItemMeta chancIm = chancHead.getItemMeta();
                chancIm.displayName(Component.text("Chancellor")
                        .color(TextColor.color(0xFFFFFF))
                        .decoration(TextDecoration.ITALIC, false)
                );

                List<Component> lore = new ArrayList<>();
                lore.add(Component.text(nomChancellor.getName())
                        .color(TextColor.color(0xBBBBBB))
                        .decoration(TextDecoration.ITALIC, false));
                chancIm.lore(lore);
                chancHead.setItemMeta(chancIm);

                inv.setItem(1, chancHead);

                // create yes and no buttons
                inv.setItem(4, yesButton());
                inv.setItem(6, noButton());

                event.getPlayer().openInventory(inv);
            }

            // GOVERNMENT COLLAPSE BUTTON
            if (event.getItem().isSimilar(governmentCollapseButton())) {
                event.setCancelled(true);

                Alignment policy = SecretCreeper.instance.currentGame.governmentCollapse();

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
                event.getPlayer().getInventory().clear();
                if (!checkWinner()) {
                    Bukkit.getPlayer(currPresident.getName()).getInventory().addItem(passPresidentButton());
                }
            }

            // LEGISLATION BUTTON (PRESIDENT)
            if (event.getItem().isSimilar(legislationButton(Position.PRESIDENT))) {
                legPolicies = SecretCreeper.instance.currentGame.legistation();
                Inventory inv = Bukkit.createInventory(null, 9, Component.text("Legislation"));

                int i = 3;
                for (Alignment policy: legPolicies) {
                    if (policy == Alignment.PLAYER) {
                        inv.setItem(i, playerPolicyButton(Position.PRESIDENT));
                    } else {
                        inv.setItem(i, creeperPolicyButton(Position.PRESIDENT));
                    }
                    i++;
                }

                event.getPlayer().openInventory(inv);
            }

            // LEGISLATION BUTTON (CHANCELLOR)
            if (event.getItem().isSimilar(legislationButton(Position.CHANCELLOR))) {
                Inventory inv = Bukkit.createInventory(null, 9, Component.text("Legislation"));

                int i = 3;
                for (Alignment policy: legPolicies) {
                    if (policy == Alignment.PLAYER) {
                        inv.setItem(i, playerPolicyButton(Position.CHANCELLOR));
                    } else {
                        inv.setItem(i, creeperPolicyButton(Position.CHANCELLOR));
                    }
                    i += 2;
                }

                event.getPlayer().openInventory(inv);
            }

            // PASS PRESIDENT BUTTON
            if (event.getItem().isSimilar(passPresidentButton())) {
                SecretCreeperPlayer prevPresident = currPresident;
                SecretCreeper.instance.currentGame.passPresidency();
                for (SecretCreeperPlayer p: SecretCreeper.instance.currentGame.getPlayers()) {
                    if (p.getPosition() == Position.PRESIDENT) {
                        currPresident = p;
                    }
                    Bukkit.getPlayer(p.getName()).clearActivePotionEffects();
                }
                Bukkit.broadcast(Component.text("Presidency Passed!")
                        .color(TextColor.color(0x55FF55)));
                Bukkit.broadcast(Component.text(prevPresident.getName() + " → " + currPresident.getName()));

                event.getPlayer().getInventory().clear();
                Bukkit.getPlayer(currPresident.getName()).getInventory().addItem(nominateChancellorButton());
                Bukkit.getPlayer(currPresident.getName()).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false, false));
            }

            // EXECUTION BUTTON
            if (event.getItem().isSimilar(executeButton())) {
                Inventory inv = Bukkit.createInventory(null, 18, Component.text("Execution")
                        .color(TextColor.color(0xAA0000)));
                int i = 0;
                for (SecretCreeperPlayer p: SecretCreeper.instance.currentGame.getPlayers()) {
                    if (p.getPosition() != Position.PRESIDENT) {
                        inv.setItem(i, getHead(p.getName(), p.getId(), "exec"));
                        i++;
                    }
                }

                event.getPlayer().openInventory(inv);
            }

            // POLICY PEEK BUTTON
            if (event.getItem().isSimilar(policyPeekButton())) {
                Inventory inv = Bukkit.createInventory(null, 9, Component.text("Policy Peek"));

                int i = 3;
                for (Alignment policy: SecretCreeper.instance.currentGame.policyPeek()) {
                    if (policy == Alignment.PLAYER) {
                        inv.setItem(i, playerPolicyButton(Position.NONE));
                    } else {
                        inv.setItem(i, creeperPolicyButton(Position.NONE));
                    }
                    i += 1;
                }

                event.getPlayer().getInventory().clear();
                event.getPlayer().openInventory(inv);
                event.getPlayer().getInventory().addItem(passPresidentButton());

                Bukkit.broadcast(Component.text("President " + event.getPlayer().getName() + " has peeked at the top 3 cards of the deck")
                        .color(TextColor.color(0xFF55FF)));
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        Player cmdSender = (Player) event.getWhoClicked();

        int executionOccured = -1;
        for (SecretCreeperPlayer p: SecretCreeper.instance.currentGame.getPlayers()) {
            // NOMINATE CHANCELLOR
            if (event.getCurrentItem().isSimilar(getHead(p.getName(), p.getId(), "nomChanc"))) {
                event.setCancelled(true);
                Player chanc = Bukkit.getPlayer(p.getName());
                if (chanc == null) {
                    return;
                }
                chanc.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false, false));
                nomChancellor = p;
                cmdSender.closeInventory();
                cmdSender.getInventory().clear();

                // give everyone button
                for (SecretCreeperPlayer p1: SecretCreeper.instance.currentGame.getPlayers()) {
                    if (p1.getPosition() == Position.PRESIDENT) {
                        currPresident = p1;
                    }
                    Bukkit.getPlayer(p1.getName()).getInventory().addItem(voteOnGovernmentButton());
                }

                Bukkit.broadcast(Component.text("-----------------------------------------------------")
                        .color(TextColor.color(0x555555)));
                Bukkit.broadcast(Component.text("                                 New Election!")
                        .color(TextColor.color(0x55FF55)));
                Bukkit.broadcast(Component.text("-----------------------------------------------------")
                        .color(TextColor.color(0x555555)));
                Bukkit.broadcast(Component.text("President: " + currPresident.getName()));
                Bukkit.broadcast(Component.text("Chancellor: " + nomChancellor.getName()));
            }

            // EXECUTION
            if (event.getCurrentItem().isSimilar(getHead(p.getName(), p.getId(), "exec"))) {
                Bukkit.getPlayer(p.getName()).setGameMode(GameMode.SPECTATOR);
                Location loc = Bukkit.getPlayer(p.getName()).getLocation();
                Bukkit.getPlayer(p.getName()).getWorld().strikeLightningEffect(loc);
                Bukkit.broadcast(Component.text("President " + cmdSender.getName() + " has executed " + p.getName())
                        .color(TextColor.color(0xAA0000)));
                cmdSender.closeInventory();
                cmdSender.getInventory().clear();
                executionOccured = p.getId();
                Bukkit.getPlayer(p.getName()).clearActivePotionEffects();
            }
        }
        if (executionOccured != -1) {
            SecretCreeper.instance.currentGame.execution(executionOccured);
            if (!checkWinner()) {
                cmdSender.getInventory().addItem(passPresidentButton());
            }
        }


        // VOTE ON GOVT
        if (event.getCurrentItem().isSimilar(yesButton()) || event.getCurrentItem().isSimilar(noButton())) {
            event.setCancelled(true);
            cmdSender.closeInventory();
            cmdSender.getInventory().clear();
            if (event.getCurrentItem().isSimilar(yesButton())) {
                votes.add(new AbstractMap.SimpleEntry<>(cmdSender.getName(), true));
            } else {
                votes.add(new AbstractMap.SimpleEntry<>(cmdSender.getName(), false));
            }

            if (votes.size() >= SecretCreeper.instance.currentGame.getPlayers().size()) {
                List<String> votedYes = new ArrayList<>();
                List<String> votedNo = new ArrayList<>();

                for (Map.Entry<String, Boolean> p: votes) {
                    if (p.getValue()) {
                        votedYes.add(p.getKey());
                    } else {
                        votedNo.add(p.getKey());
                    }
                }

                electionTracker++;
                votes = new ArrayList<>();

                boolean passed = votedYes.size() > votedNo.size();
                Bukkit.broadcast(Component.text("-----------------------------------------------------")
                        .color(TextColor.color(0x555555)));
                if (passed) {
                    Bukkit.broadcast(Component.text("                                 Vote Passed!")
                            .color(TextColor.color(0xFFFFFF)));
                } else if (electionTracker >= 3) {
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
                    electionTracker = 0;
                    SecretCreeper.instance.currentGame.election(currPresident.getId(), nomChancellor.getId());
                    if (!checkWinner()) {
                        Bukkit.getPlayer(currPresident.getName()).getInventory().addItem(legislationButton(Position.PRESIDENT));
                    }
                } else if (electionTracker == 1) {
                    Bukkit.broadcast(Component.text(""));
                    Bukkit.broadcast(Component.text("Election Tracker: ●")
                            .color(TextColor.color(0x55FF55)));
                    Bukkit.getPlayer(currPresident.getName()).getInventory().addItem(passPresidentButton());
                } else if (electionTracker == 2) {
                    Bukkit.broadcast(Component.text(""));
                    Bukkit.broadcast(Component.text("Election Tracker: ●●")
                            .color(TextColor.color(0xFFAA00)));
                    Bukkit.getPlayer(currPresident.getName()).getInventory().addItem(passPresidentButton());
                } else {
                    Bukkit.broadcast(Component.text(""));
                    Bukkit.broadcast(Component.text("Election Tracker: ●●●")
                            .color(TextColor.color(0xAA0000)));
                    electionTracker = 0;
                    Bukkit.getPlayer(currPresident.getName()).getInventory().addItem(governmentCollapseButton());
                }
            }
        }

        // PLAYER/CREEPER POLICY BUTTON (PRESIDENT)
        if (event.getCurrentItem().isSimilar(playerPolicyButton(Position.PRESIDENT)) || event.getCurrentItem().isSimilar(creeperPolicyButton(Position.PRESIDENT))) {
            legPolicies.remove(event.getSlot() - 3);
            cmdSender.closeInventory();
            cmdSender.getInventory().clear();
            Bukkit.getPlayer(nomChancellor.getName()).getInventory().addItem(legislationButton(Position.CHANCELLOR));
        }

        // PLAYER/CREEPER POLICY BUTTON (CHANCELLOR)
        if (event.getCurrentItem().isSimilar(playerPolicyButton(Position.CHANCELLOR)) || event.getCurrentItem().isSimilar(creeperPolicyButton(Position.CHANCELLOR))) {
            Material mat = event.getCurrentItem().getType();

            Bukkit.broadcast(Component.text("-----------------------------------------------------")
                    .color(TextColor.color(0x555555)));
            if (mat == Material.PLAYER_HEAD) {
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
            Bukkit.broadcast(Component.text("President " + currPresident.getName() + ", Chancellor " + nomChancellor.getName())
                    .color(TextColor.color(0xFFFFFF)));

            Bukkit.broadcast(Component.text(""));
            if (mat == Material.PLAYER_HEAD) {
                SecretCreeper.instance.currentGame.passPolicy(Alignment.PLAYER);
                Bukkit.broadcast(Component.text("Player Polices Passed: " + SecretCreeper.instance.currentGame.getPassedPlayerPolicies())
                        .color(TextColor.color(0x55FFFF)));
            } else {
                SecretCreeper.instance.currentGame.passPolicy(Alignment.CREEPER);
                Bukkit.broadcast(Component.text("Creeper Polices Passed: " + SecretCreeper.instance.currentGame.getPassedCreeperPolicies())
                        .color(TextColor.color(0x00AA00)));
            }

            cmdSender.closeInventory();
            cmdSender.getInventory().clear();
            if (!checkWinner()) {
                creeperBoard(SecretCreeper.instance.currentGame.getBoard()[SecretCreeper.instance.currentGame.getPassedCreeperPolicies() - 1]);
            }
        }
    }

    private boolean checkWinner() {
        if (SecretCreeper.instance.currentGame.getWinner() == null) {
            return false;
        } else {
            if (SecretCreeper.instance.currentGame.getWinner() == Alignment.PLAYER) {
                Bukkit.broadcast(Component.text("-----------------------------------------------------")
                        .color(TextColor.color(0x555555)));
                Bukkit.broadcast(Component.text("                                  Players Win!")
                        .color(TextColor.color(0x55FFFF)));
                Bukkit.broadcast(Component.text("-----------------------------------------------------")
                        .color(TextColor.color(0x555555)));
                if (SecretCreeper.instance.currentGame.getPassedPlayerPolicies() >= 5) {
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
                if (SecretCreeper.instance.currentGame.getPassedCreeperPolicies() >= 6) {
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
            return true;
        }
    }

    private void creeperBoard(PolicyEffect e) {
        if (e == PolicyEffect.PEEK) {
            Bukkit.getPlayer(currPresident.getName()).getInventory().addItem(policyPeekButton());
            Bukkit.broadcast(Component.text("Policy Peek Triggered")
                    .color(TextColor.color(0xFF55FF)));
        } else if (e == PolicyEffect.EXECUTION) {
            Bukkit.getPlayer(currPresident.getName()).getInventory().addItem(executeButton());
            Bukkit.broadcast(Component.text("Execution Triggered")
                    .color(TextColor.color(0xAA0000)));
        } else {
            Bukkit.getPlayer(currPresident.getName()).getInventory().addItem(passPresidentButton());
        }
    }

    private ItemStack passPresidentButton() {
        ItemStack is = new ItemStack(Material.ARROW);

        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Pass Presidency")
                .color(TextColor.color(0x9900FF))
                .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right click to pass the presidency"));
        im.lore(lore);

        is.setItemMeta(im);

        return is;
    }

    private ItemStack governmentCollapseButton() {
        ItemStack is = new ItemStack(Material.FIRE_CHARGE);

        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Government Collapse")
                .color(TextColor.color(0x9900FF))
                .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right click to pass random policy"));
        im.lore(lore);

        is.setItemMeta(im);

        return is;
    }

    private ItemStack nominateChancellorButton() {
        ItemStack is = new ItemStack(Material.LIGHT_BLUE_DYE);
        is.setAmount(1);

        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Nominate Chancellor")
                .color(TextColor.color(0x9900FF))
                .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right click to nominate chancellor"));
        im.lore(lore);

        is.setItemMeta(im);

        return is;
    }

    private ItemStack getHead(String username, int id, String str) {
        ItemStack is = new ItemStack(Material.PLAYER_HEAD);
        is.setAmount(1);

        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text(username)
                .color(TextColor.color(0xFFFFFF))
                .decoration(TextDecoration.ITALIC, false)
        );
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Id: " + id)
                .color(TextColor.color(0x505050))
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(str)
                .color(TextColor.color(0x555555)));
        im.lore(lore);
        is.setItemMeta(im);

        SkullMeta skm = (SkullMeta) is.getItemMeta();
        skm.setOwningPlayer(Bukkit.getOfflinePlayer(username));
        is.setItemMeta(skm);

        return is;
    }

    private ItemStack voteOnGovernmentButton() {
        ItemStack is = new ItemStack(Material.GRAY_DYE);
        is.setAmount(1);

        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Vote on Government")
                .color(TextColor.color(0x9900FF))
                .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right click to vote on government"));
        im.lore(lore);

        is.setItemMeta(im);

        return is;
    }

    private ItemStack yesButton() {
        ItemStack is = new ItemStack(Material.WHITE_CANDLE);
        is.setAmount(1);

        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Vote Yes")
                .color(TextColor.color(0x9900FF))
                .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Click to vote yes"));
        im.lore(lore);

        is.setItemMeta(im);

        return is;
    }

    private ItemStack noButton() {
        ItemStack is = new ItemStack(Material.BLACK_CANDLE);
        is.setAmount(1);

        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Vote No")
                .color(TextColor.color(0x9900FF))
                .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Click to vote no"));
        im.lore(lore);

        is.setItemMeta(im);

        return is;
    }

    private ItemStack legislationButton(Position pos) {
        ItemStack is = new ItemStack(Material.CREEPER_BANNER_PATTERN);
        is.setAmount(1);

        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Legislation")
                .color(TextColor.color(0x9900FF))
                .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        if (pos == Position.PRESIDENT) {
            lore.add(Component.text("Right click to legislate (President)"));
        } else {
            lore.add(Component.text("Right click to legislate (Chancellor)"));
        }
        im.lore(lore);

        is.setItemMeta(im);

        return is;
    }

    private ItemStack playerPolicyButton(Position pos) {
        ItemStack is = new ItemStack(Material.PLAYER_HEAD);
        is.setAmount(1);

        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Player Policy")
                .color(TextColor.color(0x55FFFF))
                .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        if (pos == Position.PRESIDENT) {
            lore.add(Component.text("Click to remove from legislation")
                    .color(TextColor.color(0xFFFFFF))
                    .decoration(TextDecoration.ITALIC, false));
        } else if (pos == Position.NONE) {
            lore.add(Component.text("This is a Player Policy :)")
                    .color(TextColor.color(0xFFFFFF))
                    .decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(Component.text("Click to pass policy")
                    .color(TextColor.color(0xFFFFFF))
                    .decoration(TextDecoration.ITALIC, false));
        }
        im.lore(lore);

        is.setItemMeta(im);

        return is;
    }

    private ItemStack creeperPolicyButton(Position pos) {
        ItemStack is = new ItemStack(Material.CREEPER_HEAD);
        is.setAmount(1);

        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Creeper Policy")
                .color(TextColor.color(0x00AA00))
                .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        if (pos == Position.PRESIDENT) {
            lore.add(Component.text("Click to remove from legislation")
                    .color(TextColor.color(0xFFFFFF))
                    .decoration(TextDecoration.ITALIC, false));
        } else if (pos == Position.NONE) {
            lore.add(Component.text("This is a Creeper Policy :)")
                    .color(TextColor.color(0xFFFFFF))
                    .decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(Component.text("Click to pass policy")
                    .color(TextColor.color(0xFFFFFF))
                    .decoration(TextDecoration.ITALIC, false));
        }
        im.lore(lore);

        is.setItemMeta(im);

        return is;
    }

    private ItemStack executeButton() {
        ItemStack is = new ItemStack(Material.IRON_SWORD);
        is.setAmount(1);

        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Execution")
                .color(TextColor.color(0x9900FF))
                .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right click to execute someone"));
        im.lore(lore);

        is.setItemMeta(im);

        return is;
    }

    private ItemStack policyPeekButton() {
        ItemStack is = new ItemStack(Material.ENDER_EYE);
        is.setAmount(1);

        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Policy Peek")
                .color(TextColor.color(0x9900FF))
                .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right click to peek at the next three policies"));
        im.lore(lore);

        is.setItemMeta(im);

        return is;
    }
}
