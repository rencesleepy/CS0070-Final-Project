import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//ginawa ko to para hindi maghalo yung sql queries at saka sa main gui code nito
// communicates with the SQLite through database connection and this help to organize the program and easy to maintain

public class TransactionDAO {
    public List<Transaction> getAllTransactions()
    throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY id ASC";

        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                String type = result.getString("type");
                String description = result.getString("description");
                String category = result.getString("category");
                double amount = result.getDouble("amount_php");
                
                long id = result.getLong("id");
                Transaction transaction = new Transaction(id, type, description, category, amount);

                transactions.add(transaction);
            }
        }
        return transactions;
    }

    //read
    public void addTransaction(Transaction transaction)
    throws SQLException
    {
        String sql = "INSERT INTO transactions (type, description, category, amount_php) VALUES (?, ?, ?, ?)";
        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, transaction.type);
        statement.setString(2, transaction.description);
        statement.setString(3, transaction.category);
        statement.setDouble(4, transaction.amount);
        
        int rowsInserted = statement.executeUpdate();
        if (rowsInserted > 0) {
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    transaction.id = keys.getLong(1);
                }
            }
        }
        }
    }

    public boolean updateTransaction(Transaction transaction)
    throws SQLException {
        String sql = "UPDATE transactions SET type = ?, description = ?, category = ?, amount_php = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, transaction.type);
            statement.setString(2, transaction.description);
            statement.setString(3, transaction.category);
            statement.setDouble(4, transaction.amount);
            statement.setLong(5, transaction.id);

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    public boolean deleteTransaction(int transactionId)
    throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection(); 
        PreparedStatement statement = connection.prepareStatement(sql)) 
        {
            statement.setInt(1, transactionId);

            int rowsDeleted = statement.executeUpdate();

            return rowsDeleted > 0;
        }
    }
}
