package net.neological.secretCreeper.uiux;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.neological.secretCreeper.SecretCreeper;
import net.neological.secretCreeper.game.SecretCreeperPlayer;
import net.neological.secretCreeper.game.enums.Alignment;
import net.neological.secretCreeper.game.enums.Position;
import net.neological.secretCreeper.items.InventoryItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CustomInventories {

    private final InventoryItems ii = new InventoryItems();

    public void nominateChancellorInterface(Player player) {
        Inventory inv = Bukkit.createInventory(null, 18, Component.text("Nominate Chancellor"));
        int i = 0;
        for (SecretCreeperPlayer p: SecretCreeper.instance.currentGame.getPlayers()) {
            // check if player not in term limits
            if (SecretCreeper.instance.currentGame.getTermLimits().isEmpty() && p.getPosition() != Position.PRESIDENT) {
                inv.setItem(i, ii.getHead(p.getName(), p.getId(), "nomChanc"));
                i++;
            } else {
                boolean inLimits = false;
                for (SecretCreeperPlayer p1: SecretCreeper.instance.currentGame.getTermLimits()) {
                    if (p.equals(p1)) {
                        inLimits = true;
                        break;
                    }
                }
                if (!inLimits && p.getPosition() != Position.PRESIDENT) {
                    inv.setItem(i, ii.getHead(p.getName(), p.getId(), "nomChanc"));
                    i++;
                }
            }
        }
        player.openInventory(inv);
    }

    public void voteOnGovernmentInterface(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, Component.text("Vote on Government"));

        // set first slot to president head
        for (SecretCreeperPlayer p: SecretCreeper.instance.currentGame.getPlayers()) {
            if (p.getPosition() == Position.PRESIDENT) {
                ItemStack presHead = ii.getHead(p.getName(), p.getId(), "pres");

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
        ItemStack chancHead = ii.getHead(SecretCreeper.instance.currentGame.getChancellor().getName(), SecretCreeper.instance.currentGame.getChancellor().getId(), "chanc");

        ItemMeta chancIm = chancHead.getItemMeta();
        chancIm.displayName(Component.text("Chancellor")
                .color(TextColor.color(0xFFFFFF))
                .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(SecretCreeper.instance.currentGame.getChancellor().getName())
                .color(TextColor.color(0xBBBBBB))
                .decoration(TextDecoration.ITALIC, false));
        chancIm.lore(lore);
        chancHead.setItemMeta(chancIm);

        inv.setItem(1, chancHead);

        // create yes and no buttons
        inv.setItem(4, ii.yesButton());
        inv.setItem(6, ii.noButton());

        player.openInventory(inv);
    }

    public void legislationPresidentInterface(Player player) {
        SecretCreeper.instance.currentGame.legistation();
        Inventory inv = Bukkit.createInventory(null, 9, Component.text("Legislation"));

        int i = 3;
        for (Alignment policy: SecretCreeper.instance.currentGame.getPolicies()) {
            if (policy == Alignment.PLAYER) {
                inv.setItem(i, ii.playerPolicyButton(Position.PRESIDENT));
            } else {
                inv.setItem(i, ii.creeperPolicyButton(Position.PRESIDENT));
            }
            i++;
        }

        player.openInventory(inv);
    }

    public void legislationChancellorInterface(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, Component.text("Legislation"));

        int i = 3;
        for (Alignment policy: SecretCreeper.instance.currentGame.getPolicies()) {
            if (policy == Alignment.PLAYER) {
                inv.setItem(i, ii.playerPolicyButton(Position.CHANCELLOR));
            } else {
                inv.setItem(i, ii.creeperPolicyButton(Position.CHANCELLOR));
            }
            i += 2;
        }

        player.openInventory(inv);
    }

    public void policyPeekInterface(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, Component.text("Policy Peek"));

        int i = 3;
        for (Alignment policy: SecretCreeper.instance.currentGame.policyPeek()) {
            if (policy == Alignment.PLAYER) {
                inv.setItem(i, ii.playerPolicyButton(Position.NONE));
            } else {
                inv.setItem(i, ii.creeperPolicyButton(Position.NONE));
            }
            i += 1;
        }

        player.getInventory().clear();
        player.getPlayer().openInventory(inv);
    }

    public void executionInterface(Player player) {
        Inventory inv = Bukkit.createInventory(null, 18, Component.text("Execution")
                .color(TextColor.color(0xAA0000)));
        int i = 0;
        for (SecretCreeperPlayer p: SecretCreeper.instance.currentGame.getPlayers()) {
            if (p.getPosition() != Position.PRESIDENT) {
                inv.setItem(i, ii.getHead(p.getName(), p.getId(), "exec"));
                i++;
            }
        }

        player.openInventory(inv);
    }
}
