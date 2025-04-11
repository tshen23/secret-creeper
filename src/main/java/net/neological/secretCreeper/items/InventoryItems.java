package net.neological.secretCreeper.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.neological.secretCreeper.game.enums.Position;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryItems {
    public ItemStack getHead(String username, int id, String str) {
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

    public ItemStack yesButton() {
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

    public ItemStack noButton() {
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

    public ItemStack playerPolicyButton(Position pos) {
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

    public ItemStack creeperPolicyButton(Position pos) {
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
}
