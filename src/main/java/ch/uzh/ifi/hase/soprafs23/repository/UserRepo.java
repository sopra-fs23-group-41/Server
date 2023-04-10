package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.User;

import java.sql.*;

public class UserRepo{

    private final Connection connection;

    //Constructor
    public UserRepo(String url, String username, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, username, password);
    }


    //CRUD methods
    void add(User user) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO user (name, email, password) VALUES (?, ?, ?)"
        );
        statement.setString(1, user.getName());
        statement.setString(2, user.getEmail());
        statement.setString(3, user.getPassword());
        statement.executeUpdate();
    }

    public void deleteUser(int id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM user WHERE id = ?"
        );
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    public void updateUser(User user) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE user SET name = ?, email = ?, password = ? WHERE id = ?"
        );
        statement.setString(1, user.getName());
        statement.setString(2, user.getEmail());
        statement.setString(3, user.getPassword());
        statement.setLong(4, user.getId());
        statement.executeUpdate();
    }

    public User getUserById(int id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM user WHERE id = ?"
        );
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setName(resultSet.getString("name"));
            user.setEmail(resultSet.getString("email"));
            user.setPassword(resultSet.getString("password"));
            return user;
        } else {
            return null;
        }
    }
}
