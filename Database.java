import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class Database {
    private static final String DATABASE_URL = IronInventoryManagementApp.DATABASE_URL;

    public static Connection connect() {
        try {
            return DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }

       public static void createTables() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
    
            String[] materials = {"ms", "polished mild steel", "ohns", "wps", "en-31"};
            String[] shapes = {"square", "rectangle", "round"};
    
            for (String material : materials) {
                for (String shape : shapes) {
                    String sanitizedMaterial = sanitizeName(material);
                    String sanitizedShape = sanitizeName(shape);
                    stmt.execute("CREATE TABLE IF NOT EXISTS " + sanitizedMaterial + "_" + sanitizedShape + "(" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "quantity INTEGER," +
                            "length INTEGER," +
                            "width INTEGER," +
                            "thickness INTEGER," +
                            "diameter INTEGER," +
                            "cost_price_per_inch REAL," +
                            "selling_price_per_inch REAL," +
                            "stock_added_date TEXT)");
                }
            }
    
            stmt.execute("CREATE TABLE IF NOT EXISTS stockSoldTable(" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "material TEXT," +
                            "shape TEXT," +
                            "quantity INTEGER," +
                            "length INTEGER," +
                            "width INTEGER," +
                            "thickness INTEGER," +
                            "diameter INTEGER," +
                            "cost_price_per_inch REAL," +
                            "selling_price_per_inch REAL," +
                            "stock_added_date TEXT," +
                            "sold_date TEXT)");
    
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private static String sanitizeName(String name) {
        // Replace spaces and hyphens with underscores
        return name.replaceAll("[\\s-]+", "_");
    }


}