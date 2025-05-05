package Repository;

import Domain.Bug;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BugsDBRepository implements RepositoryInterface<Integer,Bug> {

    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public BugsDBRepository(Properties props) {
        logger.info("Initializing BugsDBRepository with properties: {}", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public void save(Bug bug) {
        logger.info("Adding Bug: {}", bug);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement("INSERT INTO Bug(description, name) VALUES (?, ?)")) {
            preStmt.setString(1, bug.getDescription());
            preStmt.setString(2, bug.getName());
            int result = preStmt.executeUpdate();
            logger.trace("Saved bug: {}", result);
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error inserting bug: " + e);
        }
        logger.traceExit();
    }

    @Override
    public Bug update(Bug bug, Bug newBug) {
        logger.info("Updating Bug: {}", bug);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement("UPDATE Bug SET description = ?, name = ? WHERE id = ?")) {
            preStmt.setString(1, newBug.getDescription());
            preStmt.setString(2, newBug.getName());
            preStmt.setInt(3, bug.getId());
            int result = preStmt.executeUpdate();
            if (result > 0) {
                logger.trace("Bug updated successfully");
                return newBug;
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error updating bug: " + e);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public Bug delete(Integer id) {
        logger.info("Deleting Bug with ID: {}", id);
        Connection conn = dbUtils.getConnection();
        Bug deletedBug = findOne(id);
        if (deletedBug != null) {
            try (PreparedStatement preStmt = conn.prepareStatement("DELETE FROM Bug WHERE id = ?")) {
                preStmt.setInt(1, id);
                int result = preStmt.executeUpdate();
                if (result > 0) {
                    logger.trace("Bug deleted successfully");
                    return deletedBug;
                }
            } catch (SQLException e) {
                logger.error(e);
                System.err.println("Error deleting bug: " + e);
            }
        }
        logger.traceExit();
        return null;
    }

    @Override
    public Bug findOne(Integer id) {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement("SELECT * FROM Bug WHERE id = ?")) {
            preStmt.setInt(1, id);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    int bugId = result.getInt("id");
                    String description = result.getString("description");
                    String name = result.getString("name");
                    Bug bug = new Bug(description, name);
                    bug.setId(bugId);
                    logger.traceExit();
                    return bug;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error finding bug: " + e);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public List<Bug> findAll() {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        List<Bug> bugs = new ArrayList<>();
        try (PreparedStatement preStmt = conn.prepareStatement("SELECT * FROM Bug")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    String description = result.getString("description");
                    String name = result.getString("name");
                    Bug bug = new Bug(description, name);
                    bug.setId(id);
                    bugs.add(bug);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error finding bugs: " + e);
        }
        logger.traceExit();
        return bugs;
    }
}
