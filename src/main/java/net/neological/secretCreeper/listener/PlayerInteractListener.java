package net.neological.secretCreeper.listener;

import net.neological.secretCreeper.SecretCreeper;
import net.neological.secretCreeper.game.SecretCreeperGame;
import net.neological.secretCreeper.game.enums.Position;
import net.neological.secretCreeper.items.PlayerItems;
import net.neological.secretCreeper.uiux.Animations;
import net.neological.secretCreeper.uiux.CustomInventories;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private final PlayerItems pi = new PlayerItems();
    private final CustomInventories ci = new CustomInventories();
    private final Animations an = new Animations();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        // if right-clicked block
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() == null) {
                return;
            }

            // CREEPER STICK
            if (event.getItem().isSimilar(pi.creeperAnimationStick())) {
                SecretCreeper.instance.creeperBlocks.add(event.getClickedBlock());
                event.getPlayer().sendMessage("§2Creeper Block Set: " + SecretCreeper.instance.creeperBlocks.size());
            }

            // PLAYER STICK
            if (event.getItem().isSimilar(pi.playerAnimationStick())) {
                SecretCreeper.instance.playerBlocks.add(event.getClickedBlock());
                event.getPlayer().sendMessage("§2Player Block Set: " + SecretCreeper.instance.playerBlocks.size());
            }
        }

        // if left-clicked block
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getItem() == null) {
                return;
            }

            // CREEPER STICK
            if (event.getItem().isSimilar(pi.creeperAnimationStick())) {
                event.setCancelled(true);
                SecretCreeper.instance.creeperBlocks.removeLast();
                event.getPlayer().sendMessage("§2Creeper Block Removed: " + SecretCreeper.instance.creeperBlocks.size());
            }

            // PLAYER STICK
            if (event.getItem().isSimilar(pi.playerAnimationStick())) {
                event.setCancelled(true);
                SecretCreeper.instance.playerBlocks.removeLast();
                event.getPlayer().sendMessage("§2Player Block Removed: " + SecretCreeper.instance.playerBlocks.size());
            }
        }

        // if right-clicked something
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() == null) {
                return;
            }
            SecretCreeperGame game = SecretCreeper.instance.currentGame;

            // NOMINATE CHANCELLOR BUTTON
            if (event.getItem().isSimilar(pi.nominateChancellorButton())) {
                ci.nominateChancellorInterface(event.getPlayer());
            }

            // VOTE ON GOVERNMENT BUTTON
            if (event.getItem().isSimilar(pi.voteOnGovernmentButton())) {
                ci.voteOnGovernmentInterface(event.getPlayer());
            }

            // LEGISLATION BUTTON (PRESIDENT)
            if (event.getItem().isSimilar(pi.legislationButton(Position.PRESIDENT))) {
                ci.legislationPresidentInterface(event.getPlayer());
            }

            // LEGISLATION BUTTON (CHANCELLOR)
            if (event.getItem().isSimilar(pi.legislationButton(Position.CHANCELLOR))) {
                ci.legislationChancellorInterface(event.getPlayer());
            }

            // PASS PRESIDENT BUTTON
            if (event.getItem().isSimilar(pi.passPresidentButton())) {
                String prevPresident = game.getPresident().getName();
                game.passPresidency();
                an.passPresidencyAnimation(event.getPlayer(), prevPresident, game.getPresident().getName());
                Bukkit.getPlayer(game.getPresident().getName()).getInventory().addItem(pi.nominateChancellorButton());
            }

            // POLICY PEEK BUTTON
            if (event.getItem().isSimilar(pi.policyPeekButton())) {
                ci.policyPeekInterface(event.getPlayer());
                an.policyPeakAnimation(event.getPlayer());
                event.getPlayer().getPlayer().getInventory().addItem(pi.passPresidentButton());
            }

            // EXECUTION BUTTON
            if (event.getItem().isSimilar(pi.executeButton())) {
                ci.executionInterface(event.getPlayer());
            }

            // GOVERNMENT COLLAPSE BUTTON
            if (event.getItem().isSimilar(pi.governmentCollapseButton())) {
                event.setCancelled(true);

                an.governmentCollapseAnimation(event.getPlayer(), SecretCreeper.instance.currentGame.governmentCollapse());

                if (game.getWinner() == 0) {
                    Bukkit.getPlayer(game.getPresident().getName()).getInventory().addItem(pi.passPresidentButton());
                } else {
                    an.winAnimation(game.getWinner());
                }
            }
        }
    }
}
