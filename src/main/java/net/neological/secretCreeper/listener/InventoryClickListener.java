package net.neological.secretCreeper.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.neological.secretCreeper.SecretCreeper;
import net.neological.secretCreeper.game.SecretCreeperPlayer;
import net.neological.secretCreeper.game.enums.Alignment;
import net.neological.secretCreeper.game.enums.PolicyEffect;
import net.neological.secretCreeper.game.enums.Position;
import net.neological.secretCreeper.items.InventoryItems;
import net.neological.secretCreeper.items.PlayerItems;
import net.neological.secretCreeper.uiux.Animations;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryClickListener implements Listener {

    private final InventoryItems ii = new InventoryItems();
    private final PlayerItems pi = new PlayerItems();
    private final Animations an = new Animations();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        Player cmdSender = (Player) event.getWhoClicked();

        int executionOccured = -1;
        for (SecretCreeperPlayer p: SecretCreeper.instance.currentGame.getPlayers()) {

            // NOMINATE CHANCELLOR
            if (event.getCurrentItem().isSimilar(ii.getHead(p.getName(), p.getId(), "nomChanc"))) {
                event.setCancelled(true);

                SecretCreeper.instance.currentGame.setChancellor(p);
                an.electionAnimation(cmdSender);

                // give everyone button
                for (SecretCreeperPlayer p1: SecretCreeper.instance.currentGame.getPlayers()) {
                    Bukkit.getPlayer(p1.getName()).getInventory().addItem(pi.voteOnGovernmentButton());
                }
            }

            // EXECUTION
            if (event.getCurrentItem().isSimilar(ii.getHead(p.getName(), p.getId(), "exec"))) {
                an.executionAnimation(cmdSender, p);
                executionOccured = p.getId();
            }
        }

        // EXECUTION (CONTINUED)
        if (executionOccured != -1) {
            SecretCreeper.instance.currentGame.execution(executionOccured);
            if (SecretCreeper.instance.currentGame.getWinner() == 0) {
                cmdSender.getInventory().addItem(pi.passPresidentButton());
            } else {
                an.winAnimation(SecretCreeper.instance.currentGame.getWinner());
            }
        }

        // VOTE ON GOVT
        if (event.getCurrentItem().isSimilar(ii.yesButton()) || event.getCurrentItem().isSimilar(ii.noButton())) {
            event.setCancelled(true);
            cmdSender.closeInventory();
            cmdSender.getInventory().clear();
            if (event.getCurrentItem().isSimilar(ii.yesButton())) {
                SecretCreeper.instance.currentGame.addVote(cmdSender.getName(), true);
            } else {
                SecretCreeper.instance.currentGame.addVote(cmdSender.getName(), false);
            }

            if (SecretCreeper.instance.currentGame.getVotes().size() >= SecretCreeper.instance.currentGame.getPlayers().size()) {
                List<String> votedYes = new ArrayList<>();
                List<String> votedNo = new ArrayList<>();

                for (Map.Entry<String, Boolean> p: SecretCreeper.instance.currentGame.getVotes()) {
                    if (p.getValue()) {
                        votedYes.add(p.getKey());
                    } else {
                        votedNo.add(p.getKey());
                    }
                }

                SecretCreeper.instance.currentGame.incrementElectionTracker();
                SecretCreeper.instance.currentGame.resetVotes();
                boolean passed = votedYes.size() > votedNo.size();

                an.electionResultsAnimation(passed, votedYes, votedNo);

                if (passed) {
                    SecretCreeper.instance.currentGame.resetElectionTracker();
                    SecretCreeper.instance.currentGame.election();
                    if (SecretCreeper.instance.currentGame.getWinner() == 0) {
                        Bukkit.getPlayer(SecretCreeper.instance.currentGame.getPresident().getName()).getInventory().addItem(pi.legislationButton(Position.PRESIDENT));
                    } else {
                        an.winAnimation(SecretCreeper.instance.currentGame.getWinner());
                    }
                } else if (SecretCreeper.instance.currentGame.getElectionTracker() == 1) {
                    Bukkit.getPlayer(SecretCreeper.instance.currentGame.getPresident().getName()).getInventory().addItem(pi.passPresidentButton());
                } else if (SecretCreeper.instance.currentGame.getElectionTracker() == 2) {
                    Bukkit.getPlayer(SecretCreeper.instance.currentGame.getPresident().getName()).getInventory().addItem(pi.passPresidentButton());
                } else {
                    SecretCreeper.instance.currentGame.resetElectionTracker();
                    Bukkit.getPlayer(SecretCreeper.instance.currentGame.getPresident().getName()).getInventory().addItem(pi.governmentCollapseButton());
                }
            }
        }

        // PLAYER/CREEPER POLICY BUTTON (PRESIDENT)
        if (event.getCurrentItem().isSimilar(ii.playerPolicyButton(Position.PRESIDENT)) || event.getCurrentItem().isSimilar(ii.creeperPolicyButton(Position.PRESIDENT))) {
            SecretCreeper.instance.currentGame.removePolicy(event.getSlot() - 3);
            cmdSender.closeInventory();
            cmdSender.getInventory().clear();
            Bukkit.getPlayer(SecretCreeper.instance.currentGame.getChancellor().getName()).getInventory().addItem(pi.legislationButton(Position.CHANCELLOR));
        }

        // PLAYER/CREEPER POLICY BUTTON (CHANCELLOR)
        if (event.getCurrentItem().isSimilar(ii.playerPolicyButton(Position.CHANCELLOR)) || event.getCurrentItem().isSimilar(ii.creeperPolicyButton(Position.CHANCELLOR))) {
            Material mat = event.getCurrentItem().getType();
            cmdSender.closeInventory();
            cmdSender.getInventory().clear();

            if (mat == Material.PLAYER_HEAD) {
                SecretCreeper.instance.currentGame.passPolicy(Alignment.PLAYER);
                an.legislationAnimation(Alignment.PLAYER);
            } else {
                SecretCreeper.instance.currentGame.passPolicy(Alignment.CREEPER);
                an.legislationAnimation(Alignment.CREEPER);
            }

            // creeper board
            if (mat == Material.PLAYER_HEAD) {
                Bukkit.getPlayer(SecretCreeper.instance.currentGame.getPresident().getName()).getInventory().addItem(pi.passPresidentButton());
            } else if (SecretCreeper.instance.currentGame.getWinner() == 0) {
                PolicyEffect e = SecretCreeper.instance.currentGame.getBoard()[Math.max(0, SecretCreeper.instance.currentGame.getPassedCreeperPolicies() - 1)];
                an.creeperBoardAnimation(e);

                if (e == PolicyEffect.PEEK) {
                    Bukkit.getPlayer(SecretCreeper.instance.currentGame.getPresident().getName()).getInventory().addItem(pi.policyPeekButton());
                } else if (e == PolicyEffect.EXECUTION) {
                    Bukkit.getPlayer(SecretCreeper.instance.currentGame.getPresident().getName()).getInventory().addItem(pi.executeButton());
                } else {
                    Bukkit.getPlayer(SecretCreeper.instance.currentGame.getPresident().getName()).getInventory().addItem(pi.passPresidentButton());
                }
            } else {
                an.winAnimation(SecretCreeper.instance.currentGame.getWinner());
            }
        }
    }
}
