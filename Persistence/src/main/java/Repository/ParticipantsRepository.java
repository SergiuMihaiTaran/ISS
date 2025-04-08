package Repository;

import Domain.Competition;
import Domain.Participant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ParticipantsRepository implements RepositoryParticipantInterface<Integer, Participant, Competition> {

    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public ParticipantsRepository(Properties props) {
        logger.info("Initializing ParticipantsCompetitionsRepository with properties: {}", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public void save(Participant participant) {
        logger.info("Saving participant: {}", participant);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement("INSERT INTO Participant ( name, age) VALUES ( ?, ?)")) {

            preStmt.setString(1, participant.getName());
            preStmt.setInt(2, participant.getAge());
            int result = preStmt.executeUpdate();
            logger.trace("Saved participant: {}", result);
            int id=conn.prepareStatement("select last_insert_rowid()").executeQuery().getInt(1);
            // Save competitions associated with the participant
            for (Competition competition : participant.getCompetitions()) {
                addParticipantToCompetition(id, competition.getId());
            }

        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error inserting participant: " + e);
        }
        logger.traceExit();
    }

    @Override
    public Participant update(Participant entity, Participant newEntity) {
        logger.info("Updating participant with ID: {}", entity.getId());
        Connection conn = dbUtils.getConnection();

        try (PreparedStatement preStmt = conn.prepareStatement(
                "UPDATE Participant SET name = ?, age = ? WHERE id = ?")) {
            preStmt.setString(1, newEntity.getName());
            preStmt.setInt(2, newEntity.getAge());
            preStmt.setInt(3, entity.getId());
            int result = preStmt.executeUpdate();
            if (result == 0) {
                logger.warn("No participant found with ID: {}", entity.getId());
                return null; // No participant updated
            }
        } catch (SQLException e) {
            logger.error("Error updating participant", e);
            return null;
        }

        // Update competitions: remove old, insert new
        try (PreparedStatement deleteStmt = conn.prepareStatement(
                "DELETE FROM ParticipantCompetitions WHERE id_participant = ?")) {
            deleteStmt.setInt(1, entity.getId());
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error removing old competitions", e);
            return null;
        }

        for (Competition comp : newEntity.getCompetitions()) {
            addParticipantToCompetition(entity.getId(), comp.getId());
        }

        logger.info("Participant updated successfully: {}", newEntity);
        return newEntity;
    }


    @Override
    public void addParticipantToCompetition(int participantId, int competitionId) {
        logger.info("Adding participant {} to competition {}", participantId, competitionId);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement("INSERT INTO ParticipantCompetitions (id_participant, id_competition) VALUES (?, ?)")) {
            preStmt.setInt(1, participantId);
            preStmt.setInt(2, competitionId);
            int result = preStmt.executeUpdate();
            logger.trace("Participant added to competition: {}", result);
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error adding participant to competition: " + e);
        }
        logger.traceExit();
    }
    @Override
    public void removeParticipantFromCompetition(int participantId, int competitionId) {
        logger.info("Removing participant {} from competition {}", participantId, competitionId);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement("DELETE FROM ParticipantCompetitions WHERE id_participant = ? AND id_competition = ?")) {
            preStmt.setInt(1, participantId);
            preStmt.setInt(2, competitionId);
            int result = preStmt.executeUpdate();
            logger.trace("Participant removed from competition: {}", result);
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error removing participant from competition: " + e);
        }
        logger.traceExit();
    }

    @Override
    public List<Competition> getComposition(int participantId) {
        logger.info("Getting competitions for participant {}", participantId);
        Connection conn = dbUtils.getConnection();
        List<Competition> competitions = new ArrayList<>();
        try (PreparedStatement preStmt = conn.prepareStatement(
                "SELECT c.id, c.distance, c.style FROM Competition c " +
                        "INNER JOIN ParticipantCompetitions pc ON c.id = pc.id_competition " +
                        "WHERE pc.id_participant = ?")) {
            preStmt.setInt(1, participantId);
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
            System.err.println("Error fetching competitions: " + e);
        }
        logger.traceExit();
        return competitions;
    }

    @Override
    public Participant findOne(Integer id) {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preStmt = conn.prepareStatement("SELECT * FROM Participant WHERE id = ?")) {
            preStmt.setInt(1, id);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    int newId = result.getInt("id");
                    String name = result.getString("name");
                    int age = result.getInt("age");

                    // Fetch associated competitions
                    List<Competition> competitions = getComposition(newId);

                    Participant participant = new Participant(name, age, competitions);
                    participant.setId(newId);
                    logger.traceExit();
                    return participant;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error finding participant: " + e);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public List<Participant> findAll() {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        List<Participant> participants = new ArrayList<>();
        try (PreparedStatement preStmt = conn.prepareStatement("SELECT * FROM Participant")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    String name = result.getString("name");
                    int age = result.getInt("age");

                    // Fetch associated competitions
                    List<Competition> competitions = getComposition(id);

                    Participant participant = new Participant(name, age, competitions);
                    participant.setId(id);
                    participants.add(participant);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error finding participants: " + e);
        }
        logger.traceExit();
        return participants;
    }

    @Override
    public Participant delete(Integer id) {
        logger.info("Deleting participant with ID: {}", id);
        Connection conn = dbUtils.getConnection();
        Participant deletedParticipant = findOne(id);
        if (deletedParticipant != null) {
            try (PreparedStatement preStmt = conn.prepareStatement("DELETE FROM Participant WHERE id = ?")) {
                preStmt.setInt(1, id);
                int result = preStmt.executeUpdate();
                if (result > 0) {
                    logger.trace("Participant deleted successfully");
                    return deletedParticipant;
                }
            } catch (SQLException e) {
                logger.error(e);
                System.err.println("Error deleting participant: " + e);
            }
        }
        logger.traceExit();
        return null;
    }
    public List<Participant> getParticipantsInCompetition(Competition competition) {
        int compID=competition.getId();
        logger.info("Getting participants for competition {}", compID);
        Connection conn = dbUtils.getConnection();
        List<Participant> participants = new ArrayList<>();
        try (PreparedStatement preStmt = conn.prepareStatement(

                "SELECT p.id,p.name,p.age FROM Participant p " +
                        "INNER JOIN ParticipantCompetitions pc ON p.id = pc.id_participant " +
                        "WHERE pc.id_competition = ?")) {
            preStmt.setInt(1, compID);
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    String name = result.getString("name");
                    int age = result.getInt("age");
                    Participant participant= new Participant(name,age,getComposition(id));
                    participant.setId(id);
                    participants.add(participant);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error fetching competitions: " + e);
        }
        logger.traceExit();
        return participants;
    }

}
