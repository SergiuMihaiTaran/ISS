
import Domain.Competition;
import Domain.Participant;
import Repository.*;
import Service.IServices;
import Utils.AbstractServer;
import Utils.RpcConcurrentServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.rmi.ServerException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort=55555;
    private static Logger logger = LogManager.getLogger(StartRpcServer.class);
    public static void main(String[] args) {
        // UserRepository userRepo=new UserRepositoryMock();
        Properties serverProps=new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/server.properties"));
            logger.info("Server properties set {}",serverProps);
            // serverProps.list(System.out);
        } catch (IOException e) {
            logger.error("Cannot find chatserver.properties "+e);
            logger.debug("Looking for file in "+(new File(".")).getAbsolutePath());
            return;
        }
        IUserRepository userRepo=new UsersDBRepository(serverProps);
        RepositoryParticipantInterface<Integer, Participant, Competition> participantRepo=new ParticipantsRepository(serverProps);
        RepositoryInterface<Integer,Competition> competitionRepo=new CompetitionsDBRepository(serverProps);
        IServices chatServerImpl=new ServicesImpl(userRepo,participantRepo,competitionRepo);
        int chatServerPort=defaultPort;
        try {
            chatServerPort = Integer.parseInt(serverProps.getProperty("server.port"));
        }catch (NumberFormatException nef){
            logger.error("Wrong  Port Number"+nef.getMessage());
            logger.debug("Using default port "+defaultPort);
        }
        logger.debug("Starting server on port: "+chatServerPort);
        AbstractServer server = new RpcConcurrentServer(chatServerPort, chatServerImpl);
        try {
            server.start();
        } catch (ServerException e) {
            logger.error("Error starting the server" + e.getMessage());
        }finally {
            try {
                server.stop();
            }catch(ServerException e){
                logger.error("Error stopping server "+e.getMessage());
            }
        }
    }
}
