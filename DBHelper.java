package dao;

import java.sql.*;

public class DBHelper {
    private static final String DB_URL = "jdbc:sqlite:magazine_system.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到 SQLite JDBC 驱动，请确认 lib/sqlite-jdbc.jar 已放入 classpath", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initTables() {
        String createMagazine =
            "CREATE TABLE IF NOT EXISTS magazine (" +
            "id          INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name        TEXT NOT NULL," +
            "category    TEXT," +
            "publisher   TEXT," +
            "price       REAL NOT NULL DEFAULT 0," +
            "issn        TEXT," +
            "description TEXT" +
            ");";

        String createSubscriber =
            "CREATE TABLE IF NOT EXISTS subscriber (" +
            "id      INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name    TEXT NOT NULL," +
            "phone   TEXT," +
            "email   TEXT," +
            "address TEXT" +
            ");";

        String createSubscription =
            "CREATE TABLE IF NOT EXISTS subscription (" +
            "id            INTEGER PRIMARY KEY AUTOINCREMENT," +
            "subscriber_id INTEGER NOT NULL," +
            "magazine_id   INTEGER NOT NULL," +
            "start_date    TEXT NOT NULL," +
            "end_date      TEXT NOT NULL," +
            "months        INTEGER NOT NULL DEFAULT 1," +
            "total_price   REAL NOT NULL DEFAULT 0," +
            "status        TEXT NOT NULL DEFAULT '有效'," +
            "FOREIGN KEY (subscriber_id) REFERENCES subscriber(id)," +
            "FOREIGN KEY (magazine_id)   REFERENCES magazine(id)" +
            ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createMagazine);
            stmt.execute(createSubscriber);
            stmt.execute(createSubscription);
        } catch (SQLException e) {
            throw new RuntimeException("数据库初始化失败: " + e.getMessage(), e);
        }
    }
}
