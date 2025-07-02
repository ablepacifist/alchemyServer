package alchemy.data;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import alchemy.object.Inventory;
import alchemy.object.KnowledgeBook;
import alchemy.object.Effect;
import alchemy.object.IEffect;
import alchemy.object.IIngredient;
import alchemy.object.IInventory;
import alchemy.object.IKnowledgeBook;
import alchemy.object.IPotion;
import alchemy.object.Ingredient;
import alchemy.object.Player;
import alchemy.object.Potion;

@Repository
public class HSQLDatabase implements IStubDatabase {

    private final String dbPath;
    private static HikariDataSource dataSource;

    // Initialize the connection pool in a static block
    static {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver"); // Ensure the driver is loaded

            HikariConfig config = new HikariConfig();
            //
            config.setJdbcUrl("jdbc:hsqldb:hsql://localhost:9002/mydb");
 // my public IP 1

            config.setUsername("SA");
            config.setMaximumPoolSize(12); // Up to 12 simultaneous connections
            config.setInitializationFailTimeout(0);
            config.setConnectionTimeout(15000);
            dataSource = new HikariDataSource(config); // Initialize the connection pool
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // Handle missing driver error
        }
    }

    public HSQLDatabase(String databasePath) throws SQLException, IOException {
        this.dbPath = databasePath;

        createTables();
        seedInitialData();

        // Use the connection pool correctly
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM EFFECTS");
            while (rs.next()) {
                System.out.println("Effect found: " + rs.getString("TITLE"));
            }

            stmt.execute("CHECKPOINT");
            System.out.println("CHECKPOINT executed!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to get a connection from the pool
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void seedInitialData() {
        System.out.println("in seedInitalData");
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            // Before seeding, check if the INGREDIENTS table is empty:
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM INGREDIENTS");
            rs.next();
            int count = rs.getInt("count");
            System.out.println("Ingredient count: " + count);
            if (count == 0) {
                // Insert initial effects.
                System.out.println("putting data in");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (1, 'Paralyze', 'The target’s muscles seize up, preventing any movement for a short time.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (2, 'Light', 'The target begins to glow steadily like a torch, illuminating the area.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (3, 'Resist Poison', 'The target gains temporary resistance to toxins and poisons.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (4, 'Slow', 'The target moves sluggishly, reducing their speed and reflexes.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (5, 'Dream', 'The target is temporarily transported to a surreal dreamworld, leaving them disoriented upon return.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (6, 'Magic', 'Replenishes a spell slot, restoring a fraction of arcane energy without specifying a fixed value.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (7, 'Restore Health', 'Gradually mends wounds and revives vitality over a short period.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (8, 'Invisibility', 'The target becomes unseen, blending into the surroundings for a brief duration.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (9, 'Fire', 'Engulfs the target or weapon in flames, causing burning damage over time.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (10, 'Silence', 'A zone of silence prevents spellcasting and the verbal component of spells.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (11, 'Strength', 'Temporarily increases the target’s physical power and melee damage.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (12, 'Tangler', 'Magical restraints entangle the target, severely limiting movement.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (13, 'Burden', 'An overwhelming weight is placed on the target, hindering progress and agility.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (14, 'Damage Health', 'Drains the target’s life force, causing health loss over time.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (15, 'Wisdom', 'Enhances the target’s insight and perception, aiding in decision-making.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (16, 'Seeking', 'Improves the target’s chances to hit by sharpening focus and aim.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (17, 'Constitution', 'Increases the target’s endurance, helping to resist fatigue and injury.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (18, 'Shield', 'Raises the target’s Armor Class by creating a temporary protective barrier.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (19, 'Dispel', 'Negates certain magical effects or suppresses ongoing spells in the area.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (20, 'Resist Shock', 'Diminishes the effect of electrical energy, reducing shock damage.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (21, 'Charisma', 'Boosts the target’s charm and persuasiveness, enhancing social interactions.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (22, 'Reflect Damage', 'A portion of damage inflicted on the target is returned to the attacker.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (23, 'Night Vision', 'Enables the target to see in darkness as if it were lit.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (24, 'Detect Life', 'Allows the target to sense nearby living creatures, even if hidden.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (25, 'Intelligence', 'Improves the target’s mental acuity and problem solving.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (26, 'Resist Frost', 'Lessens the impact of cold-based attacks and environments.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (27, 'Dexterity', 'Enhances agility and reflexes, making the target nimbler.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (28, 'Climbing', 'Grants the ability to scale surfaces with increased ease.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (29, 'Water Breathing', 'Allows the target to breathe underwater without difficulty.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (30, 'Ethereal', 'Makes the target impervious to damage but unable to inflict harm while active.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (31, 'Cure', 'Cleanses the target of ailments and toxins.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (32, 'Mind Reading', 'Grants limited insight into the thoughts of others.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (33, 'Clairvoyance', 'Enables the target to ask the DM for guidance through a mystical connection.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (34, 'Resist Fire', 'Reduces the damage taken from heat or flames.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (35, 'Water Walking', 'Allows the target to traverse water as if it were solid ground.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (36, 'Shock', 'Delivers a burst of electrical energy that can briefly stun the target.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (37, 'Resist Paralyze', 'Reduces susceptibility to paralysis or immobilizing spells.')");
                stmt.executeUpdate(
                        "INSERT INTO EFFECTS (ID, TITLE, DESCRIPTION) VALUES (38, 'Frost', 'Inflicts a chilling effect that hampers the targets movement.')");
                // stmt.execute("CHECKPOINT");
                conn.commit();
                System.out.println("Effects seeded successfully.");

                // Insert initial ingredients.
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (1, 'Alkanet Flower')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (2, 'Aloe Vera Leaf')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (3, 'Ambrosia')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (4, 'ArrowRoot')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (5, 'Apple')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (6, 'Beef')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (7, 'Bergamot Seeds')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (8, 'Blackberry')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (9, 'Blood Grass')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (10, 'Boar Meat')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (11, 'Bog Beacon Mushroom')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (12, 'Bonemeal')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (13, 'Bread')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (14, 'Cairn Bolete Mushroom')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (15, 'Carrot')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (16, 'Cheese')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (17, 'Cinnabar Polypore Red Cap')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (18, 'Cinnabar Polypore Yellow Cap')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (19, 'Harpy Claws')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (20, 'Clouded Funnel Cap')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (21, 'Columbine Root Pulp')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (22, 'Corn')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (23, 'Crab Meat')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (24, 'Daemon Heart')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (25, 'Piranha Veins')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (26, 'Wolf Tooth')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (27, 'Dragon''s Tongue Flower')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (28, 'Ghoul Wax')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (29, 'Dryad Saddle Mushroom')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (30, 'Ectoplasm')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (31, 'Elf Cup Cap')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (32, 'Emetic Russula Cap')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (33, 'Fennel Seed')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (34, 'Fire Salts')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (35, 'Flax Seeds')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (36, 'Flour')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (37, 'Fly Amanita Cap')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (38, 'Foxglove Nectar')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (39, 'Frost Salts')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (40, 'Garlic')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (41, 'Ginkgo Leaf')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (42, 'Ginseng')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (43, 'Glow Dust')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (44, 'Grapes')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (45, 'Green Stain Cap')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (46, 'Ham')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (47, 'Harrada Root')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (48, 'Imp Gall')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (49, 'Ironwood Nut')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (50, 'Lady''s Mantle Leaves')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (51, 'Lavender Sprig')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (52, 'Lettuce')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (53, 'Lichor Flower')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (54, 'Mandrake Root')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (55, 'Milk Thistle Seeds')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (56, 'Minotaur Horn')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (57, 'Monkshood Root Pulp')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (58, 'Morning Glory Pulp')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (59, 'Mort Flesh')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (60, 'Motherwort Sprig')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (61, 'Mugwort Seeds')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (62, 'Mutton')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (63, 'Nightshade')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (64, 'Ogre Teeth')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (65, 'Onion')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (66, 'Pear')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (67, 'Peony Seeds')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (68, 'Potato')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (69, 'Primrose Leaves')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (70, 'Pumpkin')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (71, 'Purgeblood Salts')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (72, 'Radish')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (73, 'Rat Meat')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (74, 'Redwort Flower')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (75, 'Rice')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (76, 'Root Pulp')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (77, 'Lotus Seeds')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (78, 'Lizard Scales')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (79, 'Snake Skin')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (80, 'Unknown Ingredient')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (81, 'Somnalius Frond')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (82, 'Spindle Stick')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (83, 'Saint Juan''s Wort Nectar')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (84, 'Blue Entoloma Cap')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (85, 'Stinkhorn Cap')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (86, 'Strawberry')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (87, 'Summer Bolete Cap')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (88, 'Taproot')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (89, 'Tiger Lily Nectar')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (90, 'Tinder Polypore Cap')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (91, 'Tomato')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (92, 'Troll Fat')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (93, 'Vampire Dust')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (94, 'Venison')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (95, 'Viper Bugloss Leave')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (96, 'Void Salts')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (97, 'Water Hyacinth Nectar')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (98, 'Watermelon')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (99, 'Wheat Grain')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (100, 'White Seed Pod')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (101, 'Wisp Stalk Cap')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (102, 'Wormwood Leave')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (103, 'Spider Eye')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (104, 'Werewolf Teeth')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (105, 'Unicorn Horn')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (106, 'Green Moss')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (107, 'Snake Venom')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (108, 'Sulfur')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (109, 'Gold Shavings')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (110, 'Beetle Juice')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (111, 'Iron Bass Gills')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (112, 'Sea Urchin Spikes')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (113, 'Snowberries')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (114, 'Butterfly Wings')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (115, 'Torch Bug')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (116, 'Boar Horn')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (117, 'Trout')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (118, 'Zabou Mushrooms')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (119, 'Whiskey')");
                stmt.executeUpdate("INSERT INTO INGREDIENTS (ID, NAME) VALUES (120, 'Butter')");
                System.out.println("inital ingredients seeded");
                conn.commit();
                // stmt.execute("CHECKPOINT");

                // Insert for junction tables, players, etc. as applicable.
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (1, 1)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (1, 2)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (1, 3)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (1, 4)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (2, 5)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (2, 6)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (2, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (2, 8)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (3, 9)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (3, 10)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (3, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (3, 6)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (4, 5)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (4, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (4, 12)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (4, 13)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (5, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (5, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (5, 15)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (5, 16)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (6, 5)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (6, 17)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (6, 18)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (6, 19)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (7, 10)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (7, 19)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (7, 3)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (8, 5)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (8, 17)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (8, 6)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (8, 20)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (9, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (9, 13)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (9, 21)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (9, 37)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (10, 6)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (10, 4)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (10, 17)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (10, 7)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (11, 19)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (11, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (11, 18)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (11, 21)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (12, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (12, 34)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (12, 27)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (12, 23)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (13, 5)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (13, 27)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (13, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (13, 24)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (14, 36)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (14, 37)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (14, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (14, 25)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (15, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (15, 25)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (15, 18)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (15, 23)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (16, 34)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (16, 15)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (16, 27)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (16, 12)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (17, 18)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (17, 9)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (17, 21)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (17, 19)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (18, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (18, 6)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (18, 21)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (18, 22)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (19, 31)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (19, 1)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (19, 3)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (19, 14)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (20, 25)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (20, 23)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (20, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (20, 2)");
                // 21-40
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (21, 26)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (21, 1)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (21, 25)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (21, 6)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (22, 27)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (22, 12)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (22, 20)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (23, 20)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (23, 34)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (23, 18)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (23, 7)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (24, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (24, 10)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (24, 20)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (24, 6)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (25, 1)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (25, 22)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (25, 14)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (26, 23)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (26, 13)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (26, 26)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (26, 2)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (27, 34)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (27, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (27, 14)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (28, 3)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (28, 29)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (28, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (28, 6)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (29, 26)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (29, 38)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (29, 27)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (29, 28)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (30, 36)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (30, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (30, 19)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (30, 5)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (31, 15)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (31, 25)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (31, 31)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (31, 11)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (32, 18)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (32, 32)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (32, 6)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (33, 25)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (33, 28)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (33, 1)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (33, 32)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (34, 9)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (34, 34)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (34, 26)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (35, 18)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (35, 22)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (35, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (35, 16)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (36, 21)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (36, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (36, 22)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (37, 13)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (37, 36)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (37, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (37, 15)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (38, 3)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (38, 31)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (38, 37)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (38, 1)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (39, 38)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (39, 10)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (39, 26)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (39, 1)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (40, 3)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (40, 17)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (40, 26)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (40, 1)");
                System.out.println("first 41 ingredients seeded");
                // stmt.execute("CHECKPOINT");

                // 41-60
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (41, 36)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (41, 22)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (41, 25)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (41, 32)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (42, 31)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (42, 25)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (42, 13)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (42, 6)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (43, 2)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (43, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (43, 22)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (44, 35)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (44, 19)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (44, 30)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (45, 22)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (45, 32)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (45, 14)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (46, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (46, 15)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (46, 3)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (47, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (47, 1)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (47, 10)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (47, 28)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (48, 21)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (48, 9)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (48, 31)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (48, 16)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (49, 34)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (49, 15)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (49, 17)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (49, 9)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (50, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (50, 28)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (50, 23)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (50, 1)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (51, 27)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (51, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (51, 21)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (51, 25)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (52, 34)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (52, 38)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (52, 21)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (52, 16)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (53, 30)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (53, 10)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (53, 33)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (53, 6)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (54, 31)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (54, 15)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (54, 3)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (54, 27)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (55, 2)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (55, 31)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (55, 38)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (55, 1)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (56, 30)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (56, 28)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (56, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (56, 38)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (57, 25)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (57, 6)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (57, 30)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (57, 16)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (58, 13)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (58, 26)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (58, 15)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (58, 12)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (59, 17)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (59, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (59, 10)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (59, 12)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (60, 3)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (60, 8)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (60, 30)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (60, 32)");

                System.out.println("first 60 ingredients seeded");
                // stmt.execute("CHECKPOINT");
                // 61-80
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (61, 30)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (61, 28)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (61, 32)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (61, 15)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (62, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (62, 38)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (62, 19)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (63, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (63, 25)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (63, 36)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (63, 1)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (64, 37)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (64, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (64, 36)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (64, 9)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (65, 29)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (65, 24)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (65, 14)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (66, 27)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (66, 35)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (66, 22)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (66, 24)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (67, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (67, 33)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (67, 27)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (67, 9)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (68, 18)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (68, 35)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (68, 26)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (68, 19)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (69, 21)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (69, 5)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (69, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (69, 32)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (70, 21)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (70, 35)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (70, 24)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (70, 28)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (71, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (71, 25)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (71, 19)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (71, 15)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (72, 8)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (72, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (72, 16)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (72, 22)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (73, 24)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (73, 27)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (73, 10)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (73, 16)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (74, 26)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (74, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (74, 31)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (74, 8)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (75, 12)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (75, 27)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (75, 20)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (75, 15)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (76, 31)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (76, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (76, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (76, 36)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (77, 26)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (77, 19)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (77, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (77, 16)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (78, 29)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (78, 35)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (78, 17)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (78, 16)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (79, 20)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (79, 22)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (79, 28)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (79, 38)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (80, 31)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (80, 27)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (80, 18)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (80, 8)");
                // 81-100
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (81, 17)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (81, 16)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (81, 32)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (81, 28)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (82, 9)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (82, 2)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (82, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (82, 24)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (83, 20)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (83, 8)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (83, 31)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (83, 12)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (84, 9)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (84, 13)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (84, 26)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (84, 5)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (85, 38)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (85, 35)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (85, 21)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (85, 28)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (86, 31)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (86, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (86, 25)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (87, 18)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (87, 34)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (87, 21)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (87, 22)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (88, 20)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (88, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (88, 3)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (89, 15)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (89, 35)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (89, 8)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (90, 31)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (90, 8)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (90, 15)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (91, 24)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (91, 29)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (91, 18)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (91, 28)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (92, 21)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (92, 26)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (92, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (92, 7)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (93, 10)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (93, 30)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (93, 8)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (93, 16)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (94, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (94, 16)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (94, 24)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (95, 37)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (95, 31)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (95, 23)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (95, 30)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (96, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (96, 36)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (96, 19)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (96, 16)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (97, 15)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (97, 24)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (97, 11)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (98, 2)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (98, 30)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (98, 34)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (98, 21)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (99, 17)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (99, 30)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (99, 21)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (99, 16)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (100, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (100, 2)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (100, 29)");

                System.out.println("first 100 ingredients seeded");
                // stmt.execute("CHECKPOINT");

                // 101-120:
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (101, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (101, 32)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (101, 27)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (102, 35)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (102, 38)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (102, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (102, 12)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (103, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (103, 28)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (103, 27)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (103, 12)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (104, 30)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (104, 27)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (104, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (104, 36)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (105, 30)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (105, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (105, 2)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (105, 21)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (106, 25)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (106, 28)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (106, 10)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (106, 38)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (107, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (107, 22)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (107, 8)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (107, 16)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (108, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (108, 9)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (108, 18)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (108, 5)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (109, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (109, 21)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (109, 22)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (110, 10)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (110, 30)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (110, 15)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (110, 19)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (111, 29)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (111, 27)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (111, 26)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (111, 6)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (112, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (112, 28)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (112, 29)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (113, 26)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (113, 38)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (113, 5)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (113, 30)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (114, 20)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (114, 36)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (114, 15)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (115, 30)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (115, 34)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (115, 36)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (115, 15)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (116, 18)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (116, 2)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (116, 34)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (116, 12)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (117, 26)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (117, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (117, 29)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (117, 8)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (118, 14)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (118, 12)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (118, 4)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (119, 15)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (119, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (119, 4)");

                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (120, 11)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (120, 7)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (120, 12)");
                stmt.executeUpdate("INSERT INTO INGREDIENT_EFFECTS (INGREDIENT_ID, EFFECT_ID) VALUES (120, 23)");

                System.out.println("first 120 ingredients seeded");
                // stmt.execute("CHECKPOINT");

                // Insert player "alex" into PLAYERS — level defaults to 1.
                int rowsAffected = stmt.executeUpdate(
                        "INSERT INTO PUBLIC.PLAYERS (USERNAME, PASSWORD) VALUES ('alex', 'zx7364pl')");
                System.out.println("Inserted 'alex', rows affected: " + rowsAffected);

                // Retrieve the generated player ID for "alex".
                int alexId = -1;
                try (ResultSet rsPlayer = stmt.executeQuery("SELECT id FROM PUBLIC.PLAYERS WHERE username = 'alex'")) {
                    if (rsPlayer.next()) {
                        alexId = rsPlayer.getInt("id");
                    }
                }
                if (alexId < 0) {
                    System.err.println("Could not retrieve 'alex' ID. Inventory seeding aborted.");
                    return;
                }

                // Seed starting INVENTORY for "alex" using ingredients 1–10:
                stmt.executeUpdate("INSERT INTO PUBLIC.INVENTORY (player_id, ingredient_id, quantity) VALUES (" + alexId
                        + ", 1, 3)");
                stmt.executeUpdate("INSERT INTO PUBLIC.INVENTORY (player_id, ingredient_id, quantity) VALUES (" + alexId
                        + ", 2, 3)");
                stmt.executeUpdate("INSERT INTO PUBLIC.INVENTORY (player_id, ingredient_id, quantity) VALUES (" + alexId
                        + ", 3, 3)");
                stmt.executeUpdate("INSERT INTO PUBLIC.INVENTORY (player_id, ingredient_id, quantity) VALUES (" + alexId
                        + ", 4, 3)");
                stmt.executeUpdate("INSERT INTO PUBLIC.INVENTORY (player_id, ingredient_id, quantity) VALUES (" + alexId
                        + ", 5, 3)");
                stmt.executeUpdate("INSERT INTO PUBLIC.INVENTORY (player_id, ingredient_id, quantity) VALUES (" + alexId
                        + ", 6, 3)");
                stmt.executeUpdate("INSERT INTO PUBLIC.INVENTORY (player_id, ingredient_id, quantity) VALUES (" + alexId
                        + ", 7, 2)");
                stmt.executeUpdate("INSERT INTO PUBLIC.INVENTORY (player_id, ingredient_id, quantity) VALUES (" + alexId
                        + ", 8, 2)");
                stmt.executeUpdate("INSERT INTO PUBLIC.INVENTORY (player_id, ingredient_id, quantity) VALUES (" + alexId
                        + ", 9, 2)");
                stmt.executeUpdate("INSERT INTO PUBLIC.INVENTORY (player_id, ingredient_id, quantity) VALUES (" + alexId
                        + ", 10, 2)");

                System.out.println("Player 'alex' and his starting inventory have been seeded successfully.");

                String distinctQuery = "SELECT DISTINCT ingredient_id FROM INVENTORY WHERE player_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(distinctQuery)) {
                    ps.setInt(1, alexId);
                    try (ResultSet rss = ps.executeQuery()) {
                        while (rss.next()) {
                            int ingredientId = rss.getInt("ingredient_id");
                            // Using real data: fetch the ingredient along with its effects.
                            IIngredient ingredient = getIngredientById(ingredientId);
                            // Call your existing updateKnowledgeBook method.
                            updateKnowledgeBook(alexId, ingredient);
                        }
                    }
                }
                conn.commit();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // old:
    /*
     * private Connection connection() throws SQLException {
     * String url = "jdbc:hsqldb:file:" + dbPath + ";shutdown=true";
     * return DriverManager.getConnection(url, "SA", "");
     * }
     * 
     * // Close the connection when finished.
     * public void close() {
     * try {
     * if (connection != null) {
     * connection.close();
     * }
     * } catch (SQLException e) {
     * e.printStackTrace();
     * }
     * }
     * 
     */

    ////////////////////////////////////////////////////////////////////////////////
    // TABLE CREATION
    ////////////////////////////////////////////////////////////////////////////////

    // Create tables in the database
    private void resetDatabaseFiles() {
        // List of HSQLDB file extensions that may be present.
        String[] extensions = { ".script", ".properties", ".tmp", ".log" };

        for (String ext : extensions) {
            File file = new File(dbPath + ext);
            if (file.exists() && file.delete()) {
                System.out.println("Deleted old DB file: " + file.getName());
            }
        }
    }

    public void createTables() throws SQLException {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            // Create effects table if it doesn't exist.
            String createEffectsTable = "CREATE TABLE IF NOT EXISTS PUBLIC.EFFECTS ("
                    + "id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, "
                    + "title VARCHAR(255) NOT NULL, "
                    + "description VARCHAR(255)"
                    + ")";
            stmt.executeUpdate(createEffectsTable);

            // Create the INGREDIENTS table.
            String createIngredientsTable = "CREATE TABLE IF NOT EXISTS PUBLIC.INGREDIENTS ("
                    + "ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, "
                    + "NAME VARCHAR(255) NOT NULL"
                    + ")";
            stmt.executeUpdate(createIngredientsTable);

            // Create ingredient_effects table.
            String createIngredientEffectsTable = "CREATE TABLE IF NOT EXISTS PUBLIC.INGREDIENT_EFFECTS ("
                    + "ingredient_id INTEGER NOT NULL, "
                    + "effect_id INTEGER NOT NULL, "
                    + "PRIMARY KEY (ingredient_id, effect_id), "
                    + "FOREIGN KEY (ingredient_id) REFERENCES PUBLIC.INGREDIENTS(id), "
                    + "FOREIGN KEY (effect_id) REFERENCES PUBLIC.EFFECTS(id)"
                    + ")";
            stmt.executeUpdate(createIngredientEffectsTable);

            // Create players table.
            String createPlayersTable = "CREATE TABLE IF NOT EXISTS PUBLIC.PLAYERS ("
                    + "id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, "
                    + "username VARCHAR(255) NOT NULL UNIQUE, "
                    + "password VARCHAR(255) NOT NULL, "
                    + "level INTEGER DEFAULT 1"
                    + ")";
            stmt.executeUpdate(createPlayersTable);

            // Create inventory table.
            String createInventoryTable = "CREATE TABLE IF NOT EXISTS PUBLIC.INVENTORY ("
                    + "player_id INTEGER NOT NULL, "
                    + "ingredient_id INTEGER NOT NULL, "
                    + "quantity INTEGER NOT NULL, "
                    + "PRIMARY KEY (player_id, ingredient_id), "
                    + "FOREIGN KEY (player_id) REFERENCES PUBLIC.PLAYERS(id), "
                    + "FOREIGN KEY (ingredient_id) REFERENCES PUBLIC.INGREDIENTS(id)"
                    + ")";
            stmt.executeUpdate(createInventoryTable);

            // Create knowledge_book table.
            String createKnowledgeBookTable = "CREATE TABLE IF NOT EXISTS PUBLIC.KNOWLEDGE_BOOK ("
                    + "player_id INTEGER NOT NULL, "
                    + "ingredient_id INTEGER NOT NULL, "
                    + "effect_id INTEGER NOT NULL, "
                    + "PRIMARY KEY (player_id, ingredient_id, effect_id), "
                    + "FOREIGN KEY (player_id) REFERENCES PUBLIC.PLAYERS(id), "
                    + "FOREIGN KEY (ingredient_id) REFERENCES PUBLIC.INGREDIENTS(id), "
                    + "FOREIGN KEY (effect_id) REFERENCES PUBLIC.EFFECTS(id)"
                    + ")";
            stmt.executeUpdate(createKnowledgeBookTable);

            // Create potion table.
            String createPotionsTable = "CREATE TABLE IF NOT EXISTS PUBLIC.POTIONS ("
                    + "id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, "
                    + "name VARCHAR(255) NOT NULL, "
                    + "ingredient1_id INTEGER, "
                    + "ingredient2_id INTEGER, "
                    + "description VARCHAR(1024), "
                    + "duration DOUBLE, "
                    + "brew_level INTEGER, "
                    + "bonus_dice VARCHAR(20), "
                    + "FOREIGN KEY (ingredient1_id) REFERENCES PUBLIC.INGREDIENTS(id), "
                    + "FOREIGN KEY (ingredient2_id) REFERENCES PUBLIC.INGREDIENTS(id)"
                    + ")";
            stmt.executeUpdate(createPotionsTable);

            // Create player_potions table.
            String createPlayerPotionsTable = "CREATE TABLE IF NOT EXISTS PUBLIC.PLAYER_POTIONS ("
                    + "player_id INTEGER NOT NULL, "
                    + "potion_id INTEGER NOT NULL, "
                    + "quantity INTEGER NOT NULL, "
                    + "PRIMARY KEY (player_id, potion_id), "
                    + "FOREIGN KEY (player_id) REFERENCES PUBLIC.PLAYERS(id), "
                    + "FOREIGN KEY (potion_id) REFERENCES PUBLIC.POTIONS(id)"
                    + ")";
            stmt.executeUpdate(createPlayerPotionsTable);

            // Create potion_effects table.
            String createPotionEffectsTable = "CREATE TABLE IF NOT EXISTS PUBLIC.POTION_EFFECTS ("
                    + "potion_id INTEGER NOT NULL, "
                    + "effect_id INTEGER NOT NULL, "
                    + "PRIMARY KEY (potion_id, effect_id), "
                    + "FOREIGN KEY (potion_id) REFERENCES PUBLIC.POTIONS(id), "
                    + "FOREIGN KEY (effect_id) REFERENCES PUBLIC.EFFECTS(id)"
                    + ")";
            stmt.executeUpdate(createPotionEffectsTable);

        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // NEXT ID METHODS (for players)
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public int getNextPotionId() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 AS nextId FROM potions";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("nextId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public int getNextPlayerId() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 AS nextId FROM players";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("nextId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // INGREDIENTS MANAGEMENT
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public IIngredient getIngredientByName(String name) {
        String sql = "SELECT * FROM ingredients WHERE LOWER(name) = LOWER(?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Ingredient(rs.getInt("id"), rs.getString("name"), getEffectsForIngredient(rs.getInt("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<IEffect> getEffectsForIngredient(int ingredientId) {
        List<IEffect> effects = new ArrayList<>();
        String sql = "SELECT e.id, e.title, e.description FROM effects e "
                + "INNER JOIN ingredient_effects ie ON e.id = ie.effect_id "
                + "WHERE ie.ingredient_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ingredientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                effects.add(new Effect(rs.getInt("id"), rs.getString("title"), rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return effects;
    }

    @Override
    public List<IIngredient> getAllIngredients() {
        List<IIngredient> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM ingredients";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ingredients.add(new Ingredient(rs.getInt("id"), rs.getString("name"),
                        getEffectsForIngredient(rs.getInt("id"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // PLAYERS MANAGEMENT
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public void addPlayer(Player player) {
        String sql = "INSERT INTO PUBLIC.PLAYERS (id, username, password, level) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, player.getId());
            pstmt.setString(2, player.getUsername());
            pstmt.setString(3, player.getPassword());
            pstmt.setInt(4, player.getLevel()); // Level is persisted
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Player getPlayer(int playerId) {
        String sql = "SELECT * FROM PUBLIC.PLAYERS WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int level = rs.getInt("level");
                    return new Player(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            getPlayerInventory(playerId),
                            getKnowledgeBook(playerId),
                            level // Now passing the level from the DB
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching player with ID: " + playerId);
            e.printStackTrace();
        }
        return null;
    }

    public void updatePlayerLevel(int playerId, int newLevel) {
        String sql = "UPDATE PUBLIC.PLAYERS SET level = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newLevel);
            pstmt.setInt(2, playerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating level for player ID: " + playerId);
            e.printStackTrace();
        }
    }

    @Override
    public Player getPlayerByUsername(String username) {
        String sql = "SELECT * FROM PUBLIC.PLAYERS WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int playerId = rs.getInt("id"); // Extract the player's ID
                    return new Player(
                            playerId, // Player ID
                            rs.getString("username"), // Username
                            rs.getString("password"), // Password
                            getPlayerInventory(playerId), // Fetch player's inventory
                            getKnowledgeBook(playerId) // Already an IKnowledgeBook
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching player with username: " + username);
            e.printStackTrace();
        }
        return null; // Return null if the player is not found
    }

    @Override
    public Collection<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int playerId = rs.getInt("id");
                // Fetch player's inventory
                IInventory inventory = getPlayerInventory(playerId);
                // Fetch player's knowledge book (already an IKnowledgeBook)
                IKnowledgeBook knowledgeBook = getKnowledgeBook(playerId);

                // Construct the Player object
                players.add(new Player(
                        playerId,
                        rs.getString("username"),
                        rs.getString("password"),
                        inventory,
                        knowledgeBook));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all players");
            e.printStackTrace();
        }

        return players;
    }

    
@Override
public void deletePlayer(int playerId) throws SQLException {
    Connection conn = null;
    try {
        conn = getConnection();
        conn.setAutoCommit(false);
        try (Statement stmt = conn.createStatement()) {
            // Delete from player-dependent tables.
            // 1. Delete any inventory records for this player.
            stmt.executeUpdate("DELETE FROM PUBLIC.INVENTORY WHERE player_id = " + playerId);
            // 2. Delete any knowledge entries for this player.
            stmt.executeUpdate("DELETE FROM PUBLIC.KNOWLEDGE_BOOK WHERE player_id = " + playerId);
            // 3. Delete any potion entries for this player.
            stmt.executeUpdate("DELETE FROM PUBLIC.PLAYER_POTIONS WHERE player_id = " + playerId);
            // Finally, delete the player record.
            stmt.executeUpdate("DELETE FROM PUBLIC.PLAYERS WHERE id = " + playerId);
        }
        conn.commit();
    } catch (SQLException e) {
        if (conn != null) {
            conn.rollback();
        }
        throw e;
    } finally {
        if (conn != null) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}



    ////////////////////////////////////////////////////////////////////////////////
    // INVENTORY MANAGEMENT
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public IIngredient getIngredientById(int ingredientId) {
        String sql = "SELECT id, name FROM ingredients WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ingredientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    return new Ingredient(id, name, new ArrayList<>()); // Pass an empty effect list for now.
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addIngredientToPlayerInventory(int playerId, IIngredient ingredient, int quantity) {
        String updateSql = "UPDATE inventory SET quantity = quantity + ? WHERE player_id = ? AND ingredient_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, playerId);
            pstmt.setInt(3, ingredient.getId());
            int affected = pstmt.executeUpdate();
            if (affected == 0) {
                System.out.println("Ingredient not in inventory, inserting new row.");
                String insertSql = "INSERT INTO inventory (player_id, ingredient_id, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, playerId);
                    insertStmt.setInt(2, ingredient.getId());
                    insertStmt.setInt(3, quantity);
                    insertStmt.executeUpdate();
                    conn.commit();
                }
            } else {
                System.out.println("Ingredient updated in inventory, new quantity added.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeIngredientFromPlayerInventory(int playerId, IIngredient ingredient, int quantity) {
        String sql = "UPDATE inventory SET quantity = quantity - ? WHERE player_id = ? AND ingredient_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, playerId);
            pstmt.setInt(3, ingredient.getId());
            pstmt.executeUpdate();
            String deleteSql = "DELETE FROM inventory WHERE player_id = ? AND ingredient_id = ? AND quantity <= 0";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, playerId);
                deleteStmt.setInt(2, ingredient.getId());
                deleteStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // KNOWLEDGE BOOK MANAGEMENT
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public void addKnowledgeEntry(int playerId, IIngredient ingredient, IEffect effect) {
        // First, check if the knowledge entry already exists.
        String checkSql = "SELECT COUNT(*) FROM knowledge_book WHERE player_id = ? AND ingredient_id = ? AND effect_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, playerId);
            checkStmt.setInt(2, ingredient.getId());
            checkStmt.setInt(3, effect.getId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Entry already exists, so we simply return.
                    System.out.println("Entry already exists: Player ID=" + playerId +
                            ", Ingredient ID=" + ingredient.getId() +
                            ", Effect ID=" + effect.getId());
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // No entry exists; perform the insert.
        String insertSql = "INSERT INTO knowledge_book (player_id, ingredient_id, effect_id) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, ingredient.getId());
            pstmt.setInt(3, effect.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateKnowledgeBook(int playerId, IIngredient ingredient) {
        String checkSql = "SELECT COUNT(*) FROM knowledge_book WHERE player_id = ? AND ingredient_id = ? AND effect_id = ?";
        String insertSql = "INSERT INTO knowledge_book (player_id, ingredient_id, effect_id) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            for (IEffect effect : ingredient.getEffects()) {
                if (effect != null) {
                    // Check if the effect already exists
                    checkStmt.setInt(1, playerId);
                    checkStmt.setInt(2, ingredient.getId());
                    checkStmt.setInt(3, effect.getId());
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) == 0) {
                            // Insert if not already learned
                            insertStmt.setInt(1, playerId);
                            insertStmt.setInt(2, ingredient.getId());
                            insertStmt.setInt(3, effect.getId());
                            insertStmt.executeUpdate();
                            System.out.println("Inserted effect into knowledge_book: Player ID=" + playerId +
                                    ", Ingredient ID=" + ingredient.getId() + ", Effect ID=" + effect.getId());
                        } else {
                            System.out.println("Effect already learned: Player ID=" + playerId +
                                    ", Ingredient ID=" + ingredient.getId() + ", Effect ID=" + effect.getId());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IKnowledgeBook getKnowledgeBook(int playerId) {
        Map<Integer, List<IEffect>> knowledgeBook = new HashMap<>();
        String sql = "SELECT i.id AS ingredient_id, e.id AS effect_id, e.title AS effect_title, " +
                "e.description AS effect_description " +
                "FROM knowledge_book kb " +
                "INNER JOIN ingredients i ON kb.ingredient_id = i.id " +
                "INNER JOIN effects e ON kb.effect_id = e.id " +
                "WHERE kb.player_id = ?";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int ingredientId = rs.getInt("ingredient_id");

                    IEffect effect = new Effect(
                            rs.getInt("effect_id"),
                            rs.getString("effect_title"),
                            rs.getString("effect_description"));

                    knowledgeBook.computeIfAbsent(ingredientId, k -> new ArrayList<>()).add(effect);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching knowledge book for player ID: " + playerId);
            e.printStackTrace();
        }

        // Wrap the map in a KnowledgeBook instance.
        return new KnowledgeBook(knowledgeBook);
    }

    @Override
    public IInventory getPlayerInventory(int playerId) {
        IInventory inventory = new Inventory();

        // SQL query to load ingredients with their effects.
        String sqlIngredients = "SELECT i.id AS ingredient_id, i.name AS ingredient_name, " +
                "pi.quantity AS quantity, " +
                "e.id AS effect_id, e.title AS effect_title, e.description AS effect_description " +
                "FROM inventory pi " +
                "INNER JOIN ingredients i ON pi.ingredient_id = i.id " +
                "LEFT JOIN ingredient_effects ie ON i.id = ie.ingredient_id " +
                "LEFT JOIN effects e ON ie.effect_id = e.id " +
                "WHERE pi.player_id = ?";

        // SQL query to load potions with their composition and effects.
        String sqlPotions = "SELECT p.id AS potion_id, p.name AS potion_name, pp.quantity AS quantity, "
                + "p.duration AS potion_duration, p.brew_level AS brew_level, p.bonus_dice AS bonus_dice, "
                + "e.id AS effect_id, e.title AS effect_title, e.description AS effect_description, "
                + "i1.id AS ingredient1_id, i1.name AS ingredient1_name, "
                + "i2.id AS ingredient2_id, i2.name AS ingredient2_name, "
                + "p.description AS potion_description "
                + "FROM PUBLIC.PLAYER_POTIONS pp "
                + "INNER JOIN PUBLIC.POTIONS p ON pp.potion_id = p.id "
                + "LEFT JOIN PUBLIC.POTION_EFFECTS pe ON p.id = pe.potion_id "
                + "LEFT JOIN PUBLIC.EFFECTS e ON pe.effect_id = e.id "
                + "LEFT JOIN PUBLIC.INGREDIENTS i1 ON p.ingredient1_id = i1.id "
                + "LEFT JOIN PUBLIC.INGREDIENTS i2 ON p.ingredient2_id = i2.id "
                + "WHERE pp.player_id = ?";

        System.out.println("DEBUG: SQL Query: " + sqlPotions);

        try (Connection conn = getConnection()) {
            // --------------------------------------------------------
            // Process ingredient rows and aggregate them.
            // --------------------------------------------------------
            PreparedStatement pstmtIngredients = conn.prepareStatement(sqlIngredients);
            pstmtIngredients.setInt(1, playerId);
            ResultSet rsIng = pstmtIngredients.executeQuery();

            // Map for grouping ingredient rows by ingredient ID.
            Map<Integer, AggregatedIngredient> ingredientMap = new HashMap<>();

            while (rsIng.next()) {
                int ingredientId = rsIng.getInt("ingredient_id");
                String ingredientName = rsIng.getString("ingredient_name");
                int quantity = rsIng.getInt("quantity"); // Quantity should be the same in each row for the same
                                                         // ingredient

                // Get the existing AggregatedIngredient (if any) or create a new one.
                AggregatedIngredient agg = ingredientMap.get(ingredientId);
                if (agg == null) {
                    agg = new AggregatedIngredient(ingredientId, ingredientName, quantity);
                    ingredientMap.put(ingredientId, agg);
                }
                // Process effect information from the current row.
                int effectId = rsIng.getInt("effect_id");
                if (!rsIng.wasNull()) {
                    String effectTitle = rsIng.getString("effect_title");
                    String effectDescription = rsIng.getString("effect_description");
                    IEffect effect = new Effect(effectId, effectTitle, effectDescription);
                    // Only add if not already present
                    if (!agg.effects.contains(effect)) {
                        agg.effects.add(effect);
                    }
                }
            }
            rsIng.close();
            pstmtIngredients.close();

            // Add each aggregated ingredient to the inventory.
            for (AggregatedIngredient agg : ingredientMap.values()) {
                IIngredient ingredient = new Ingredient(agg.id, agg.name, agg.effects);
                inventory.addIngredient(ingredient, agg.quantity);
            }

            // --------------------------------------------------------
            // Process potion rows and aggregate them.
            // --------------------------------------------------------
            PreparedStatement pstmtPotions = conn.prepareStatement(sqlPotions);
            pstmtPotions.setInt(1, playerId);
            ResultSet rsPot = pstmtPotions.executeQuery();

            // Map for grouping potion rows by potion ID.
            Map<Integer, AggregatedPotion> potionMap = new HashMap<>();
            int potionRowCount = 0; // Debug counter
            while (rsPot.next()) {
                potionRowCount++;

                int potionId = rsPot.getInt("potion_id");
                String potionName = rsPot.getString("potion_name");
                int quantity = rsPot.getInt("quantity");
                // Get or create an aggregation for this potion.
                AggregatedPotion aggPotion = potionMap.get(potionId);
                if (aggPotion == null) {
                    aggPotion = new AggregatedPotion(potionId, potionName, quantity);
                    potionMap.put(potionId, aggPotion);
                }
                // Retrieve & assign the new fields from the result set.
                aggPotion.duration = rsPot.getDouble("potion_duration");
                aggPotion.brewLevel = rsPot.getInt("brew_level");
                aggPotion.description = rsPot.getString("potion_description");
                aggPotion.bonusDice = rsPot.getString("bonus_dice");
                System.out.println("DEBUG: Found potion row - ID: " + potionId + ", Name: " + potionName
                        + ", Quantity: " + quantity + " aggPotionDuration:" + aggPotion.duration + " dice: "
                        + aggPotion.bonusDice);

                // Process effect information for the potion.
                int effectId = rsPot.getInt("effect_id");
                if (!rsPot.wasNull()) {
                    String effectTitle = rsPot.getString("effect_title");
                    String effectDescription = rsPot.getString("effect_description");
                    IEffect effect = new Effect(effectId, effectTitle, effectDescription);
                    if (!aggPotion.effects.contains(effect)) {
                        aggPotion.effects.add(effect);
                    }
                }
                // Process potion ingredient components; assign only once.
                if (aggPotion.ingredient1 == null) {
                    int ing1Id = rsPot.getInt("ingredient1_id");
                    String ing1Name = rsPot.getString("ingredient1_name");
                    aggPotion.ingredient1 = new Ingredient(ing1Id, ing1Name, new ArrayList<>());
                }
                if (aggPotion.ingredient2 == null) {
                    int ing2Id = rsPot.getInt("ingredient2_id");
                    String ing2Name = rsPot.getString("ingredient2_name");
                    aggPotion.ingredient2 = new Ingredient(ing2Id, ing2Name, new ArrayList<>());
                }
            }

            System.out.println("DEBUG: Number of potion rows returned: " + potionRowCount);
            rsPot.close();
            pstmtPotions.close();

            // Add each aggregated potion to the inventory.
            for (AggregatedPotion aggPotion : potionMap.values()) {
                IPotion potion = new Potion(
                        aggPotion.id,
                        aggPotion.name,
                        aggPotion.effects,
                        aggPotion.ingredient1,
                        aggPotion.ingredient2,
                        aggPotion.duration,
                        aggPotion.description,
                        aggPotion.bonusDice);
                potion.setBrewLevel(aggPotion.brewLevel);
                inventory.addPotion(potion, aggPotion.quantity);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Optionally, print out the inventory details for debugging:
        System.out.println("DEBUG: Inventory after aggregation: " + inventory);
        return inventory;
    }

    // potions:
    @Override
    public void addPotionToPlayerInventory(int playerId, Potion potion, int quantity) {
        String sql = "INSERT INTO PUBLIC.PLAYER_POTIONS (player_id, potion_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, potion.getId());
            pstmt.setInt(3, quantity);
            int rowsAffected = pstmt.executeUpdate();
            conn.commit(); // Commit the transaction explicitly if auto-commit is off
            System.out.println("DEBUG: Inserted potion row for player " + playerId + ", potion ID " + potion.getId()
                    + ", rows affected: " + rowsAffected + "  with brewlevel: " + potion.getBrewLevel());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPotion(Potion potion) {
        // Updated SQL statement to include duration and brew_level.
        String sqlPotion = "INSERT INTO PUBLIC.POTIONS (id, name, ingredient1_id, ingredient2_id, description, duration, brew_level, bonus_dice) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sqlPotion)) {
            pstmt.setInt(1, potion.getId());
            pstmt.setString(2, potion.getName());
            if (potion.getIngredient1() != null) {
                pstmt.setInt(3, potion.getIngredient1().getId());
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
            if (potion.getIngredient2() != null) {
                pstmt.setInt(4, potion.getIngredient2().getId());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            if (potion.getDescription() != null) {
                pstmt.setString(5, potion.getDescription());
            } else {
                pstmt.setNull(5, java.sql.Types.VARCHAR);
            }
            pstmt.setDouble(6, potion.getDuration()); // Duration value
            pstmt.setInt(7, potion.getBrewLevel()); // Brew level
            pstmt.setString(8, potion.getBonusDice()); // Bonus dice

            pstmt.executeUpdate();
            conn.commit();
            System.out.println("DEBUG: Potion inserted into POTIONS table: " + potion.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Insert each effect into the POTION_EFFECTS table.
        String sqlEffects = "INSERT INTO PUBLIC.POTION_EFFECTS (potion_id, effect_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmtEffects = conn.prepareStatement(sqlEffects)) {
            for (IEffect effect : potion.getEffects()) {
                pstmtEffects.setInt(1, potion.getId());
                pstmtEffects.setInt(2, effect.getId());
                pstmtEffects.executeUpdate();
            }
            conn.commit();
            System.out.println(
                    "DEBUG: Inserted " + potion.getEffects().size() + " effect(s) for potion: " + potion.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void debugPlayerPotions(int playerId) {
        String sql = "SELECT * FROM PUBLIC.PLAYER_POTIONS WHERE player_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("DEBUG: PLAYER_POTIONS rows for player ID " + playerId + ":");
                while (rs.next()) {
                    int potionId = rs.getInt("potion_id");
                    int quantity = rs.getInt("quantity");
                    System.out.println("DEBUG: potion_id = " + potionId + ", quantity = " + quantity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePotionFromPlayerInventory(int playerId, Potion potion, int quantity) {
        // First, attempt to update the quantity.
        String sqlUpdate = "UPDATE player_potions SET quantity = quantity - ? "
                + "WHERE player_id = ? AND potion_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, playerId);
            pstmt.setInt(3, potion.getId());
            int rowsAffected = pstmt.executeUpdate();

            // If no rows were updated, it may be that the player doesn't yet have this
            // potion,
            // or the quantity is insufficient. Handle accordingly.
            if (rowsAffected == 0) {
                System.err.println("No potion entry found for removal, or insufficient quantity.");
            }
        } catch (SQLException e) {
            System.err.println("Error removing potion from inventory for player ID: " + playerId);
            e.printStackTrace();
        }

        // Optionally, you might want to delete the row if quantity has reached zero.
        String sqlDelete = "DELETE FROM player_potions WHERE player_id = ? AND potion_id = ? AND quantity <= 0";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, potion.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error cleaning up potion inventory for player ID: " + playerId);
            e.printStackTrace();
        }
    }

    public void deleteAllData() throws SQLException {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM knowledge_book");
            stmt.executeUpdate("DELETE FROM inventory");
            stmt.executeUpdate("DELETE FROM potions");
            stmt.executeUpdate("DELETE FROM ingredient_effects");
            stmt.executeUpdate("DELETE FROM ingredients");
            stmt.executeUpdate("DELETE FROM effects");
            stmt.executeUpdate("DELETE FROM players");
        }
    }

    public void logAllPlayers() {
        String sql = "SELECT * FROM players";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                // Use System.out or a custom logger for compatibility with non-Android
                // environments
                System.out.println("Player found: id=" + id + ", username=" + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////
    // dummies for testing
    ///////////////////
    @Override
    public void addIngredient(IIngredient ingWithShared1) {

    }

    @Override
    public void addEffect(IEffect uniqueEffect2) {

    }

    // Helper class to aggregate ingredient rows.
    private static class AggregatedIngredient {
        int id;
        String name;
        int quantity;
        List<IEffect> effects = new ArrayList<>();

        AggregatedIngredient(int id, String name, int quantity) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
        }
    }

    // Helper class to aggregate potion rows.
    private static class AggregatedPotion {
        public double duration;
        public int brewLevel;
        public String description;
        public int id;
        public String name;
        public int quantity;
        public String bonusDice; // New field for bonus dice
        public List<IEffect> effects = new ArrayList<>();
        public IIngredient ingredient1;
        public IIngredient ingredient2;

        AggregatedPotion(int id, String name, int quantity) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.duration = 0.0;
            this.brewLevel = 0;
            this.bonusDice = null;
        }
    }
}
