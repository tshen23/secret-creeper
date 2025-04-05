package net.neological.secretCreeper.game;

import net.neological.secretCreeper.game.enums.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Representation of a Secret Creeper Game
 */
public class SecretCreeperGame {

    private final List<Alignment> deck;
    private final List<SecretCreeperPlayer> players;
    private final PolicyEffect[] board;
    private int passedPlayerPolicies;
    private int passedCreeperPolicies;
    private final List<Integer> termLimits;
    private Alignment winner;

    /**
     * Constructor for
     * @param players list of players
     * @throws IllegalArgumentException players length must equal 6 for now
     */
    public SecretCreeperGame(List<SecretCreeperPlayer> players) {
        try {
            if (players.size() != 6) {
                throw new IllegalArgumentException("numPlayers must be 6");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }

        this.deck = new ArrayList<>();
        shuffleDeck();
        this.players = new ArrayList<>(players);
        this.board = new PolicyEffect[]{
                PolicyEffect.NONE,
                PolicyEffect.NONE,
                PolicyEffect.PEEK,
                PolicyEffect.EXECUTION,
                PolicyEffect.EXECUTION
        };
        this.passedCreeperPolicies = 0;
        this.passedPlayerPolicies = 0;
        this.termLimits = new ArrayList<>();
        this.winner = null;
    }

    public Alignment getWinner() { return this.winner; }
    public void setWinner(Alignment winner) { this.winner = winner; }
    public int[] getTermLimits() {
        Integer[] integerArray = this.termLimits.toArray(Integer[]::new);
        int[] intArray = new int[integerArray.length];
        for (int i = 0; i < integerArray.length; i++) {
            intArray[i] = integerArray[i];
        }
        return intArray;
    }
    public PolicyEffect[] getBoard() { return this.board; }
    public List<SecretCreeperPlayer> getPlayers() { return this.players; }
    public int getPassedPlayerPolicies() { return this.passedPlayerPolicies; }
    public int getPassedCreeperPolicies() { return this.passedCreeperPolicies; }

    /**
     * Shuffles deck in SecretCreeperGame
     */
    public final void shuffleDeck() {
        deck.clear();
        for (int i = 0; i < 11; i++) {
            deck.add(Alignment.CREEPER);
        }
        for (int i = 0; i < 6; i++) {
            deck.add(Alignment.PLAYER);
        }
        Collections.shuffle(deck);
    }

    /**
     * Implementation of the (Policy) Peek policy effect of creeper board
     * @return Alignment of top 3 policy cards in deck
     * @throws IllegalArgumentException Deck size must be at least 3
     */
    public Alignment[] policyPeek() {
        try {
            if (deck.size() < 3) {
                throw new IllegalArgumentException("deck size must be at least 3");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return new Alignment[]{ deck.get(0), deck.get(1), deck.get(2) };
    }

    /**
     * Implementation of the Execution policy effect of creeper board
     * @param id Id of player to kill/remove
     * @throws IllegalArgumentException id must be a valid id in players
     */
    public void execution(int id) {
        try {
            boolean isValid = false;
            SecretCreeperPlayer temp = null;
            for (SecretCreeperPlayer player: this.players) {
                if (player.getId() == id) {
                    isValid = true;
                    temp = player;
                    if (player.getRole() == Role.CHARGED) {
                        setWinner(Alignment.PLAYER);
                    }
                }
            }
            if (isValid) {
                this.players.remove(temp);
            } else {
                throw new IllegalArgumentException("id must be a valid id in players");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Changes the president and chancellor
     * @param presId id of player who will become president
     * @param chancId id of player who will become president
     * @throws IllegalArgumentException chancId cannot be in termLimits
     * @throws IllegalArgumentException ids cannot be the same
     * @throws IllegalArgumentException id must be a valid id in players
     */
    public void election(int presId, int chancId) {
        try {
            // future reference: maybe make presId the old president for term limits
            // NEED TO - restore this
            for (int id: termLimits) {
                if (chancId == id) {
                    throw new IllegalArgumentException("chancId cannot be in termLimits");
                }
                if (presId == chancId) {
                    throw new IllegalArgumentException("ids cannot be the same");
                }
            }

            termLimits.clear();
            // termLimits.add(presId);
            termLimits.add(chancId);

            boolean isValid = false;
            for (SecretCreeperPlayer player: this.players) {
                if (player.getPosition() == Position.PRESIDENT || player.getPosition() == Position.CHANCELLOR) {
                    player.setPosition(Position.NONE);
                }
                if (player.getId() == presId) {
                    isValid = true;
                    player.setPosition(Position.PRESIDENT);
                } else if (player.getId() == chancId) {
                    isValid = true;
                    player.setPosition(Position.CHANCELLOR);
                    if (player.getRole() == Role.CHARGED && passedCreeperPolicies >= 3) { setWinner(Alignment.CREEPER); }
                }
            }

            if (!isValid) {
                throw new IllegalArgumentException("id must be a valid id in players");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * If election tracker hits 3, government collapse implementation
     */
    public Alignment governmentCollapse() {
        termLimits.clear();
        Alignment align = deck.getFirst();
        if (align == Alignment.CREEPER) {
            this.passedCreeperPolicies++;
            if (passedCreeperPolicies == 6) {
                setWinner(Alignment.CREEPER);
            }
        } else {
            this.passedPlayerPolicies++;
            if (passedPlayerPolicies == 5) {
                setWinner((Alignment.PLAYER));
            }
        }
        deck.removeFirst();
        if (deck.size() < 3) {
            shuffleDeck();
        }
        return align;
    }

    /**
     * Gets top three policies and remove them from deck
     * @return Top three policies of deck
     */
    public List<Alignment> legistation() {
        List<Alignment> policies = new ArrayList<>(Arrays.asList(new Alignment[]{ deck.get(0), deck.get(1), deck.get(2)}));
        deck.remove(0);
        deck.remove(0);
        deck.remove(0);
        if (deck.size() < 3) { shuffleDeck(); }
        return policies;
    }

    /**
     * Changes passed policy counters / note* creeper board is independent
     * @param policy Alignment of policy being passed
     */
    public void passPolicy(Alignment policy) {
        if (policy == Alignment.PLAYER) {
            this.passedPlayerPolicies++;
            if (this.passedPlayerPolicies >= 5) {
                setWinner(Alignment.PLAYER);
            }
        } else {
            this.passedCreeperPolicies++;
            if (this.passedCreeperPolicies >= 6) {
                setWinner(Alignment.CREEPER);
            }
        }
    }

    /**
     * Passes on the presidency
     **/
    public void passPresidency() {
        int temp = 0;
        int i = 0;
        for (SecretCreeperPlayer p: this.players) {
            if (p.getPosition() == Position.PRESIDENT) {
                temp = i;
                p.setPosition(Position.NONE);
                break;
            }
            i++;
        }
        if (temp == this.players.size() - 1) {
            players.getFirst().setPosition(Position.PRESIDENT);
        } else {
            players.get(temp + 1).setPosition(Position.PRESIDENT);
        }
    }
}
