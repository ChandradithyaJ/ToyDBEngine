import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Date;

public class generateInsertInstancesForcs21b059_table1 {
    public static void main(String[] args) {
        Random random = new Random();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

        try (FileWriter writer = new FileWriter("cs21b059_table1Instances.txt")) {
            for (int i = 0; i < 100; i++) {
                int randomInt = random.nextInt(1000); // Generate a random integer
                String randomString = generateRandomString(); // Generate a random string
                String randomDate = dateFormat.format(generateRandomDate()); // Generate a random date
                String instance = "insert into cs21b059_table1 (" + randomInt + "," + randomString + "," + randomDate
                        + ")";
                writer.write(instance);
                writer.write(System.lineSeparator());
            }

            writer.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }
    }
    
    // Generate a random string (you can customize this method)
    private static String generateRandomString() {
        String[] strings = {"MongoDB", "MariaDB", "MySQL", "SQLite", "PostgreSQL", "MindsDB", "Neo4J", "GraphQL", "Redis", "ElasticSearch", "Firebase"};
        return strings[new Random().nextInt(strings.length)];
    }

    // Generate a random date (you can customize this method)
    private static Date generateRandomDate() {
        long minDate = new Date(0).getTime();
        long maxDate = new Date().getTime();
        long randomTime = minDate + (long) (Math.random() * (maxDate - minDate));
        return new Date(randomTime);
    }
}
