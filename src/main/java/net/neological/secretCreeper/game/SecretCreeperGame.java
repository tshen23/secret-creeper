package net.neological.secretCreeper.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.neological.secretCreeper.SecretCreeper;
import net.neological.secretCreeper.game.enums.*;
import org.bukkit.Bukkit;

import java.util.*;

/**
 * Representation of a Secret Creeper Game
 */
public class SecretCreeperGame {

    private final List<Alignment> deck;
    private final List<SecretCreeperPlayer> players;
    private final PolicyEffect[] board;
    private int passedPlayerPolicies;
    private int passedCreeperPolicies;
    private final List<SecretCreeperPlayer> termLimits;
    private Alignment winner;
    private SecretCreeperPlayer president;
    private SecretCreeperPlayer chancellor;
    private List<Alignment> policies;
    private List<Map.Entry<String, Boolean>> votes;
    private int electionTracker;
    private int currentIndex;

    /**
     * Constructor for
     * @param players list of players
     * @throws IllegalArgumentException players length must equal 6 for now
     */
    public SecretCreeperGame(List<SecretCreeperPlayer> players) {
        if (players.size() != 6) {
            throw new IllegalArgumentException("numPlayers must be 6");
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
        this.votes = new ArrayList<>();
        this.electionTracker = 0;

        for (SecretCreeperPlayer p: this.players) {
            if (p.getPosition() == Position.PRESIDENT) {
                president = p;
                this.currentIndex = p.getId();
            }
        }
    }

    /** 0: no winner; 1: player policy win; 2: player execution win;
     * 3: creeper policy win; 4: creeper chancellor win
     * */
    public int getWinner() {
        if (winner == null) {
            return 0;
        } else if (winner == Alignment.PLAYER) {
            if (SecretCreeper.instance.currentGame.getPassedPlayerPolicies() >= 5) {
                return 1;
            } else {
                return 2;
            }
        } else {
            if (SecretCreeper.instance.currentGame.getPassedCreeperPolicies() >= 6) {
                return 3;
            } else {
                return 4;
            }
        }
    }

    public List<SecretCreeperPlayer> getTermLimits() {
        return termLimits;
    }

    public PolicyEffect[] getBoard() {
        return this.board;
    }

    public List<SecretCreeperPlayer> getPlayers() {
        return this.players;
    }

    public int getPassedPlayerPolicies() {
        return this.passedPlayerPolicies;
    }

    public int getPassedCreeperPolicies() {
        return this.passedCreeperPolicies;
    }

    public SecretCreeperPlayer getPresident() {
        return this.president;
    }

    public SecretCreeperPlayer getChancellor() {
        return this.chancellor;
    }

    public List<Alignment> getPolicies() {
        return this.policies;
    }

    public void removePolicy(int i) {
        if (i >= policies.size() || i < 0) {
            throw new IllegalArgumentException("index is not valid");
        }
        policies.remove(i);
    }

    public List<Map.Entry<String, Boolean>> getVotes() {
        return this.votes;
    }

    public void addVote(String name, Boolean vote) {
        votes.add(new AbstractMap.SimpleEntry<>(name, vote));
    }

    public void resetVotes() {
        votes = new ArrayList<>();
    }

    public int getElectionTracker() {
        return this.electionTracker;
    }

    public void incrementElectionTracker() {
        electionTracker++;
    }

    public void resetElectionTracker() {
        electionTracker = 0;
    }

    /**
     * Sets the chancellor
     * @throws IllegalArgumentException p cannot be current president
     * @throws IllegalArgumentException p cannot be in term limits
     * */
    public void setChancellor(SecretCreeperPlayer p) {
        if (p.equals(president)) {
            throw new IllegalArgumentException("p cannot be current president");
        }
        for (SecretCreeperPlayer player: termLimits) {
            if (p.equals(player)) {
                throw new IllegalArgumentException("p cannot be in term limits");
            }
        }
        if (chancellor != null) {
            chancellor.setPosition(Position.NONE);
        }
        p.setPosition(Position.CHANCELLOR);
        chancellor = p;
    }

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
        if (deck.size() < 3) {
            throw new IllegalArgumentException("deck size must be at least 3");
        }
        return new Alignment[]{ deck.get(0), deck.get(1), deck.get(2) };
    }

    /**
     * Implementation of the Execution policy effect of creeper board
     * @param id Id of player to kill/remove
     * @throws IllegalArgumentException id must be a valid id in players
     */
    public void execution(int id) {
        boolean isValid = false;
        SecretCreeperPlayer temp = null;
        for (SecretCreeperPlayer player: this.players) {
            if (player.getId() == id) {
                isValid = true;
                temp = player;
                if (player.getRole() == Role.CHARGED) {
                    winner = Alignment.PLAYER;
                }
            }
        }
        if (isValid) {
            this.players.remove(temp);
        } else {
            throw new IllegalArgumentException("id must be a valid id in players");
        }
    }

    /**
     * Changes the president and chancellor, requires chancellor to have been set
     * @throws IllegalArgumentException chancellor cannot be null
     */
    public void election() {
        if (chancellor == null) {
            throw new IllegalArgumentException("chancellor cannot be null");
        }

        termLimits.clear();
        // termLimits.add(president);
        termLimits.add(chancellor);

        if (chancellor.getRole() == Role.CHARGED && passedCreeperPolicies >= 3) {
            winner = Alignment.CREEPER;
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
                winner = Alignment.CREEPER;
            }
        } else {
            this.passedPlayerPolicies++;
            if (passedPlayerPolicies == 5) {
                winner = Alignment.PLAYER;
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
    public void legistation() {
        policies = new ArrayList<>(Arrays.asList(new Alignment[]{ deck.get(0), deck.get(1), deck.get(2)}));
        deck.remove(0);
        deck.remove(0);
        deck.remove(0);
        if (deck.size() < 3) { shuffleDeck(); }
    }

    /**
     * Changes passed policy counters / note* creeper board is independent
     * @param policy Alignment of policy being passed
     */
    public void passPolicy(Alignment policy) {
        if (policy == Alignment.PLAYER) {
            this.passedPlayerPolicies++;
            if (this.passedPlayerPolicies >= 5) {
                winner = Alignment.PLAYER;
            }
        } else {
            this.passedCreeperPolicies++;
            if (this.passedCreeperPolicies >= 6) {
                winner = Alignment.CREEPER;
            }
        }
    }

    /**
     * Passes on the presidency
     **/
    public void passPresidency() {
        int newPresident = (currentIndex + 1) % players.size();

        for(SecretCreeperPlayer p : players) {
            p.setPosition(Position.NONE);
        }

        currentIndex = newPresident;
        players.get(newPresident).setPosition(Position.PRESIDENT);
        president = players.get(newPresident);
    }
}
