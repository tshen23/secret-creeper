package net.neological.secretCreeper.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.neological.secretCreeper.game.enums.Position;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerItems {
    public ItemStack nominateChancellorButton() {
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

    public ItemStack voteOnGovernmentButton() {
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

    public ItemStack passPresidentButton() {
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

    public ItemStack legislationButton(Position pos) {
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

    public ItemStack executeButton() {
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

    public ItemStack policyPeekButton() {
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

    public ItemStack governmentCollapseButton() {
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

    public ItemStack creeperAnimationStick() {
        ItemStack is = new ItemStack(Material.STICK);

        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Creeper Animation Stick")
                .color(TextColor.color(0x9900FF))
                .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right click to set creeper spawning blocks"));
        lore.add(Component.text("Left click to remove latest creeper spawning block"));
        im.lore(lore);

        is.setItemMeta(im);

        return is;
    }

    public ItemStack playerAnimationStick() {
        ItemStack is = new ItemStack(Material.STICK);

        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Player Animation Stick")
                .color(TextColor.color(0x9900FF))
                .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right click to set player spawning blocks"));
        lore.add(Component.text("Left click to remove latest player spawning block"));
        im.lore(lore);

        is.setItemMeta(im);

        return is;
    }
}
