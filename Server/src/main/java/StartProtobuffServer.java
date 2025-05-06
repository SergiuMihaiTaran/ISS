import Domain.Competition;
import Domain.Participant;
import Repository.*;
import Service.IServices;
import Utils.AbstractServer;

import java.io.IOException;
import java.util.Properties;


public class StartProtobuffServer {
    private static int defaultPort=55555;
    public static void main(String[] args) {


        Properties serverProps=new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/chatserver.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatserver.properties "+e);
            return;
        }
        IUserRepository userRepo=new UsersDBRepository(serverProps);
        RepositoryParticipantInterface<Integer, Participant, Competition> participantRepo=new ParticipantsRepository(serverProps);
        RepositoryInterface<Integer,Competition> competitionRepo=new CompetitionsDBRepository(serverProps);
        IServices chatServerImpl=new ServicesImpl(userRepo,participantRepo,competitionRepo);
        int chatServerPort=defaultPort;
        try {
            chatServerPort = Integer.parseInt(serverProps.getProperty("chat.server.port"));
        }catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port: "+chatServerPort);
        //AbstractServer server = new ProtobuffConcurrentServer(chatServerPort, chatServerImpl);
        try {
            //server.start();
        } catch (Exception e) {
            System.err.println("Error starting the server" + e.getMessage());
        }



    }
}
