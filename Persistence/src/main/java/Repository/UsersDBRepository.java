package Repository;

import Domain.Entity;
import Domain.User;
import Domain.TypeOfEmployee;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UsersDBRepository implements IUserRepository {

    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public UsersDBRepository(Properties props) {
        logger.info("Initializing UsersDBRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public void save(User elem) {
        logger.info("Adding User: {}", elem);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement("INSERT INTO User(name, password, email, typeOfEmployee) VALUES (?, ?, ?, ?)")) {
            preStmt.setString(1, elem.getUsername());
            preStmt.setString(2, elem.getPassword());
            preStmt.setString(3, elem.getEmail());
            preStmt.setString(4, elem.getTypeOfEmployee().toString());
            int result = preStmt.executeUpdate();
            logger.trace("Saved user: {}", result);
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error inserting user: " + e);
        }
        logger.traceExit();
    }

    @Override
    public User update(User user, User newUser) {
        logger.info("Updating User: {}", user);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement("UPDATE User SET name = ?, password = ?, email = ?, typeOfEmployee = ? WHERE id = ?")) {
            preStmt.setString(1, newUser.getUsername());
            preStmt.setString(2, newUser.getPassword());
            preStmt.setString(3, newUser.getEmail());
            preStmt.setString(4, newUser.getTypeOfEmployee().toString());
            preStmt.setInt(5, user.getId());
            int result = preStmt.executeUpdate();
            if (result > 0) {
                logger.trace("User updated successfully");
                return newUser;
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error updating user: " + e);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public User delete(Integer id) {
        logger.info("Deleting User with ID: {}", id);
        Connection conn = dbUtils.getConnection();
        User deletedUser = findOne(id);
        if (deletedUser != null) {
            try (PreparedStatement preStmt = conn.prepareStatement("DELETE FROM User WHERE id = ?")) {
                preStmt.setInt(1, id);
                int result = preStmt.executeUpdate();
                if (result > 0) {
                    logger.trace("User deleted successfully");
                    return deletedUser;
                }
            } catch (SQLException e) {
                logger.error(e);
                System.err.println("Error deleting user: " + e);
            }
        }
        logger.traceExit();
        return null;
    }

    @Override
    public User findOne(Integer id) {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement("SELECT * FROM User WHERE id = ?")) {
            preStmt.setInt(1, id);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    int newId = result.getInt("id");
                    String name = result.getString("name");
                    String password = result.getString("password");
                    String email = result.getString("email");
                    String typeStr = result.getString("typeOfEmployee");
                    TypeOfEmployee type = TypeOfEmployee.valueOf(typeStr);

                    User user = new User(name, password, email, type);
                    user.setId(newId);
                    logger.traceExit();
                    return user;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error finding user: " + e);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public List<User> findAll() {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        List<User> users = new ArrayList<>();
        try (PreparedStatement preStmt = conn.prepareStatement("SELECT * FROM User")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    String name = result.getString("name");
                    String password = result.getString("password");
                    String email = result.getString("email");
                    String typeStr = result.getString("typeOfEmployee");
                    TypeOfEmployee type = TypeOfEmployee.valueOf(typeStr);

                    User user = new User(name, password, email, type);
                    user.setId(id);
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error finding users: " + e);
        }
        logger.traceExit();
        return users;
    }

    public User searchByNameAndPassword(String name, String password,TypeOfEmployee typeOfEmployee) {
        List<User> users = findAll();
        for (User user : users) {
            if (user.getUsername().equals(name) && user.getPassword().equals(password) && user.getTypeOfEmployee().equals(typeOfEmployee)) {
                return user;
            }
        }
        return null;
    }
}
