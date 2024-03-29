package infrastructure;

import domain.model.Player;
import domain.model.Transaction;
import domain.model.TransactionType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The class PlayerRepositoryImpl represents the implementation of the PlayerRepository interface for interacting
 * with player data in the system. Information about players is stored in the playersDB database, based on
 * a Map collection.
 *
 * @see PlayerRepository
 * @see Player
 * @see Transaction
 */
public class PlayerRepositoryImpl implements PlayerRepository {
    private PreparedStatement preparedStatement;
    private final Connection connection;

    public PlayerRepositoryImpl(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void savePlayer(String name, String password) throws SQLException {
        String sqlSavePlayer = "INSERT INTO model.player (name, password, balance) VALUES(?, ?, 0);";
        connection.setAutoCommit(true);
        preparedStatement = connection.prepareStatement(sqlSavePlayer);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, password);
        preparedStatement.executeUpdate();

    }

    @Override
    public boolean exist(String playerName, String playerPassword) throws SQLException {
        Long playerId = getPlayerIdByNameAndPassword(playerName, playerPassword);
        return playerId != null;

    }

    @Override
    public BigDecimal getCurrentBalance(Long playerId) throws SQLException {
        String sqlGetBalance = "SELECT (balance) FROM model.player WHERE id=?";
        preparedStatement = connection.prepareStatement(sqlGetBalance);
        preparedStatement.setLong(1, playerId);

        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getBigDecimal("balance");
        //return new BigDecimal(balance);

    }

    @Override
    public Player getPlayer(Long playerId) throws SQLException {
        String sqlGetPlayer = "SELECT * FROM model.player WHERE id=?";
        preparedStatement = connection.prepareStatement(sqlGetPlayer);
        preparedStatement.setLong(1, playerId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            String name = resultSet.getString("name");
            String password = resultSet.getString("password");
            BigDecimal balance = resultSet.getBigDecimal("balance");
            Player player = new Player(playerId, name, password);
            player.setBalance(balance);
            return player;
        } else
            return null;
    }


    @Override
    public List<Transaction> getPlayerTransactions(Long playerId) throws SQLException {
        List<Transaction> transactionList = new ArrayList<>();
        String sqlGetListTransactionByPlayerId = "SELECT * FROM model.transaction WHERE player_id=?";
        preparedStatement = connection.prepareStatement(sqlGetListTransactionByPlayerId);
        preparedStatement.setLong(1, playerId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String amount = resultSet.getString("amount");
            String type = resultSet.getString("transaction_type");
            String code = resultSet.getString("transaction_code");

            transactionList.add(new Transaction(
                    code,
                    playerId,
                    new BigDecimal(amount),
                    type.equals("CREDIT") ? TransactionType.CREDIT : TransactionType.DEBIT));
        }
        return transactionList;

    }

    @Override
    public int getCountPlayers() throws SQLException {
        String sqlGetCount = "SELECT COUNT(*) FROM model.player";
        preparedStatement = connection.prepareStatement(sqlGetCount);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    @Override
    public Long getPlayerIdByNameAndPassword(String playerName, String playerPassword) throws SQLException {
        String sqlGetId = "SELECT (id) FROM model.player WHERE name=? AND password=?";
        preparedStatement = connection.prepareStatement(sqlGetId);
        preparedStatement.setString(1, playerName);
        preparedStatement.setString(2, playerPassword);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getLong("id");
        }
        return null;
    }

    @Override
    public void updateBalance(Long id, BigDecimal newBalance) throws SQLException {
        String sqlUpdateBalance = "UPDATE model.player SET balance=? WHERE id=?";
        connection.setAutoCommit(true);
        preparedStatement = connection.prepareStatement(sqlUpdateBalance);
        preparedStatement.setBigDecimal(1, newBalance);
        preparedStatement.setLong(2, id);
        preparedStatement.executeUpdate();
    }
}