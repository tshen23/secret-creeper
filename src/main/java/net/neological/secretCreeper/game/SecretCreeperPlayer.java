package net.neological.secretCreeper.game;

import net.neological.secretCreeper.game.enums.*;

/**
 * Representation of a player
 */
public class SecretCreeperPlayer {

    private int id;
    private final String name;
    private Alignment alignment;
    private Role role;
    private Position position;

    /**
     * Constructor for Player Class
     * @param id player uid
     * @param name player username
     * @param align player alignment
     * @param role role of player
     */
    public SecretCreeperPlayer(int id, String name, Alignment align, Role role) {
        this.id = id;
        this.name = name;
        this.alignment = align;
        this.role = role;
        this.position = Position.NONE;
    }

    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public Alignment getAlignment() { return this.alignment; }
    public Role getRole() { return this.role; }
    public Position getPosition() { return this.position; }
    public void setPosition(Position pos) { this.position = pos; }
    public void setId(int id) {
        this.id = id;
    }
    public void setAlignment(Alignment align) {
        this.alignment = align;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SecretCreeperPlayer player)) {
            return false;
        }

        return player.id == this.id;
    }
}
