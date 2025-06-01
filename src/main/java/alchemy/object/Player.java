package alchemy.object;
import java.io.Serializable;
import java.util.Objects;

public class Player implements Serializable {

    private final int id; // Unique identifier for the player
    private final String username; // Player's username
    private final String password; // Player's password
    private final IInventory inventory; // Player's unique inventory
    private final IKnowledgeBook knowledgeBook; // Player's unique knowledge book
    private int level;

    public Player(int id, String username, String password, IInventory inventory, IKnowledgeBook knowledgeBook, int level) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.inventory = inventory;
        this.knowledgeBook = knowledgeBook;
        this.level = level;
    }
    public Player(int id, String username, String password, IInventory inventory, IKnowledgeBook knowledgeBook) {
        this(id, username, password, inventory, knowledgeBook, 1);
    }
    public int getLevel() {
        return level;
    }
    public boolean levelUp() {
        if (level < 10) {
            level++;
            //update player level
            return true;
        }
        return false;  // Already at max level.
    }
    public int getId() {
        return id;
    }


    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    public IInventory getInventory() {
        return inventory;
    }


    public IKnowledgeBook getKnowledgeBook() {
        return knowledgeBook;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", inventory=" + inventory +
                ", knowledgeBook=" + knowledgeBook +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return id == player.id &&
                Objects.equals(username, player.username) &&
                Objects.equals(password, player.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password);
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
