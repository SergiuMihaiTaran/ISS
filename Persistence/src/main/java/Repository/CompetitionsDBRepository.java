package Repository;

import Domain.Competition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CompetitionsDBRepository implements RepositoryInterface<Integer, Competition> {

    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public CompetitionsDBRepository(Properties props) {
        logger.info("Initializing CompetitionsDBRepository with properties: {}", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public void save(Competition competition) {
        logger.info("Adding Competition: {}", competition);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement("INSERT INTO Competition( distance, style) VALUES ( ?, ?)")) {

            preStmt.setInt(1, competition.getDistance());
            preStmt.setString(2, competition.getStyle());
            int result = preStmt.executeUpdate();
            logger.trace("Saved competition: {}", result);
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error inserting competition: " + e);
        }
        logger.traceExit();
    }

    @Override
    public Competition update(Competition competition, Competition newCompetition) {
        logger.info("Updating Competition: {}", competition);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement("UPDATE Competition SET distance = ?, style = ? WHERE id = ?")) {
            preStmt.setInt(1, newCompetition.getDistance());
            preStmt.setString(2, newCompetition.getStyle());
            preStmt.setInt(3, competition.getId());
            int result = preStmt.executeUpdate();
            if (result > 0) {
                logger.trace("Competition updated successfully");
                return newCompetition;
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error updating competition: " + e);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public Competition delete(Integer id) {
        logger.info("Deleting Competition with ID: {}", id);
        Connection conn = dbUtils.getConnection();
        Competition deletedCompetition = findOne(id);
        if (deletedCompetition != null) {
            try (PreparedStatement preStmt = conn.prepareStatement("DELETE FROM Competition WHERE id = ?")) {
                preStmt.setInt(1, id);
                int result = preStmt.executeUpdate();
                if (result > 0) {
                    logger.trace("Competition deleted successfully");
                    return deletedCompetition;
                }
            } catch (SQLException e) {
                logger.error(e);
                System.err.println("Error deleting competition: " + e);
            }
        }
        logger.traceExit();
        return null;
    }

    @Override
    public Competition findOne(Integer id) {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement("SELECT * FROM Competition WHERE id = ?")) {
            preStmt.setInt(1, id);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    int newId = result.getInt("id");
                    int distance = result.getInt("distance");
                    String style = result.getString("style");
                    Competition competition = new Competition(distance, style);
                    competition.setId(newId);
                    logger.traceExit();
                    return competition;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error finding competition: " + e);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public List<Competition> findAll() {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        List<Competition> competitions = new ArrayList<>();
        try (PreparedStatement preStmt = conn.prepareStatement("SELECT * FROM Competition")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    int distance = result.getInt("distance");
                    String style = result.getString("style");
                    Competition competition = new Competition(distance, style);
                    competition.setId(id);
                    competitions.add(competition);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error finding competitions: " + e);
        }
        logger.traceExit();
        return competitions;
    }
}
