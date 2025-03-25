package Repository;

import Domain.Entity;
import Domain.User;

import Utils.JdbcUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
public class UsersDBRepository implements RepositoryInterface<Integer,User> {

        private JdbcUtils dbUtils;



        private static final Logger logger= LogManager.getLogger();

        public UsersDBRepository(Properties props) {
            logger.info("Initializing UsersDBRepository with properties: {} ",props);
            dbUtils=new JdbcUtils(props);
        }
        public
        @Override
         void save(User elem) {
            logger.info("Adding User: {}", elem);
            Connection conn=dbUtils.getConnection();
            try(PreparedStatement preStmt= conn.prepareStatement("insert into User( name, password, email) VALUES (?,?,?)")) {

                preStmt.setString(1, elem.getUsername());
                preStmt.setString(2, elem.getPassword());
                preStmt.setString(3, elem.getEmail());
                int result=preStmt.executeUpdate();
                logger.trace("Saved car: {}", result);
            } catch (SQLException e) {
                logger.error(e);
                System.err.println("Error inserting car"+e);
            }
            logger.traceExit();
        }

    @Override
    public User update(User user, User newUser) {
        logger.info("Updating User: {}", user);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement("UPDATE User SET name = ?, password = ?, email = ? WHERE id = ?")) {
            preStmt.setString(1, newUser.getUsername());
            preStmt.setString(2, newUser.getPassword());
            preStmt.setString(3, newUser.getEmail());
            preStmt.setInt(4, user.getId());
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
        try (PreparedStatement preStmt = conn.prepareStatement("select * from User where id = ?")) {
            preStmt.setInt(1, id);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    int newId = result.getInt(1);
                    String name = result.getString(2);
                    String password = result.getString(3);
                    String email = result.getString(4);
                    User user=new User(name,password,email);
                    user.setId(newId);
                    logger.traceExit();

                    return user;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error inserting car" + e);
        }
        logger.traceExit();

        return null;
    }

    @Override
        public List<User> findAll() {
            logger.traceEntry();
            Connection conn = dbUtils.getConnection();
            List<User> users = new ArrayList<>();
            try (PreparedStatement preStmt = conn.prepareStatement("select * from User")) {
                try (ResultSet result = preStmt.executeQuery()) {
                    while (result.next()) {
                        int id = result.getInt(1);
                        String name = result.getString(2);
                        String password = result.getString(3);
                        String email = result.getString(4);
                        User user=new User(name,password,email);
                        user.setId(id);
                        users.add(user);
                    }
                }
            } catch (SQLException e) {
                logger.error(e);
                System.err.println("Error inserting car" + e);
            }
            logger.traceExit();
            return users;
        }


}



