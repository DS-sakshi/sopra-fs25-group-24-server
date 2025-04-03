import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DatabaseCreationTest {

    @Test
    public void testCreateDatabase() throws SQLException {
        String jdbcUrl = "jdbc:h2:file:./data/testdb";
        String username = "sa";
        String password = "";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            assertNotNull(connection, "Connection should not be null");
        }
    }
}
