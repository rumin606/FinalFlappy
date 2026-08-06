import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ScoreDatabase {

    private final MongoCollection<Document> scoresCollection;
    private final MongoCollection<Document> playersCollection;

    public ScoreDatabase() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }

        String uri = props.getProperty("mongodb.uri");
        String dbName = props.getProperty("mongodb.database");
        String collName = props.getProperty("mongodb.collection");

        MongoClient client = MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build());

        MongoDatabase db = client.getDatabase(dbName);
        scoresCollection = db.getCollection(collName);
        playersCollection = db.getCollection("players");
    }

    public void savePlayer(String name, String email, String phone, String password) {
        Document existing = playersCollection.find(new Document("email", email)).first();
        if (existing != null) {
            playersCollection.updateOne(
                    new Document("email", email),
                    new Document("$set", new Document("name", name).append("phone", phone).append("password", password))
            );
        } else {
            Document doc = new Document("name", name)
                    .append("email", email)
                    .append("phone", phone)
                    .append("password", password)
                    .append("gamesPlayed", 0)
                    .append("totalScore", 0)
                    .append("bestScore", 0)
                    .append("totalDuration", 0)
                    .append("currentLevel", 1)
                    .append("createdAt", System.currentTimeMillis());
            playersCollection.insertOne(doc);
        }
    }

    public Document authenticatePlayer(String email, String password) {
        return playersCollection.find(
                new Document("email", email).append("password", password)
        ).first();
    }

    public boolean emailExists(String email) {
        return playersCollection.find(new Document("email", email)).first() != null;
    }

    public void saveScore(String playerName, String playerEmail, double score, long durationMs) {
        Document scoreDoc = new Document("playerName", playerName)
                .append("playerEmail", playerEmail)
                .append("score", (int) score)
                .append("durationMs", durationMs)
                .append("timestamp", System.currentTimeMillis());
        scoresCollection.insertOne(scoreDoc);

        playersCollection.updateOne(
                new Document("email", playerEmail),
                new Document("$inc", new Document("gamesPlayed", 1).append("totalScore", (int) score).append("totalDuration", durationMs))
        );

        int finalLevel = (int) score / FlappyBird.SCORE_PER_LEVEL + 1;
        playersCollection.updateOne(
                new Document("email", playerEmail),
                new Document("$max", new Document("currentLevel", Math.max(1, finalLevel)))
        );

        Document player = playersCollection.find(new Document("email", playerEmail)).first();
        if (player != null) {
            int best = player.getInteger("bestScore", 0);
            if ((int) score > best) {
                playersCollection.updateOne(
                        new Document("email", playerEmail),
                        new Document("$set", new Document("bestScore", (int) score))
                );
            }
        }
    }

    public Document getPlayer(String email) {
        return playersCollection.find(new Document("email", email)).first();
    }

    public int getPlayerLevel(String email) {
        Document player = playersCollection.find(new Document("email", email)).first();
        if (player != null) {
            return Math.max(1, player.getInteger("currentLevel", 1));
        }
        return 1;
    }

    public void saveLevel(String email, int level) {
        playersCollection.updateOne(
                new Document("email", email),
                new Document("$max", new Document("currentLevel", Math.max(1, level)))
        );
    }

    public List<Document> getTopScores(int limit) {
        List<Document> topScores = new ArrayList<>();
        scoresCollection.find()
                .sort(new Document("score", -1))
                .limit(limit)
                .into(topScores);
        return topScores;
    }
}
