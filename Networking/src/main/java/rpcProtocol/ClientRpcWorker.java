package rpcProtocol;


import Domain.Competition;
import Domain.Participant;
import Domain.User;
import Service.IServices;
import dto.DTOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dto.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import Service.IObserver;

public class ClientRpcWorker implements Runnable, IObserver {
    private IServices server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    private static Logger logger = LogManager.getLogger(ClientRpcWorker.class);

    public ClientRpcWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            connected=true;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    public void run() {
        while(connected){
            try {
                Object request=input.readObject();
                logger.debug("Received request from client: "+request);
                Response response=handleRequest((Request)request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException|ClassNotFoundException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            logger.error("Error "+e);
        }
    }



    private static Response okResponse=new Response.Builder().type(ResponseType.OK).build();

    private Response handleRequest(Request request){
        Response response=null;
        if (request.type()== RequestType.LOGIN){
            logger.debug("Login request ..."+request.type());
            UserDTO udto=(UserDTO)request.data();
            User user= DTOUtils.getFromDTO(udto);

            try {
                server.login(user, this);
                return okResponse;
            } catch (Exception e) {
                connected=false;
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type()== RequestType.LOGOUT){
            logger.debug("Logout request");
            // LogoutRequest logReq=(LogoutRequest)request;
            UserDTO udto=(UserDTO)request.data();
            User user= DTOUtils.getFromDTO(udto);
            try {
                server.logout(user, this);
                connected=false;
                return okResponse;

            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type()== RequestType.GET_COMPETITION_LIST) {
            logger.debug("Get Comp List");

            try {
                List<Competition> competitions = server.getCompetitionsList();
                List<CompetitionDTO> competitionDTOS = DTOUtils.getDTO(competitions);
                return new Response.Builder().type(ResponseType.GET_PARTICIPANT_LIST).data(competitionDTOS).build();
            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
            if (request.type()== RequestType.GET_PARTICIPANTS_IN_COMPETITION) {
                logger.debug("Get Part List");
                CompetitionDTO dto=(CompetitionDTO) request.data();
                Competition comp=DTOUtils.getFromDTO(dto);
                try {
                    List<Participant> competitions = server.getParticipantsInCompetition(comp);
                    List<ParticipantDTO> competitionDTOS = DTOUtils.getDTOp(competitions);
                    return new Response.Builder().type(ResponseType.GET_PARTICIPANTS_IN_COMPETITION).data(competitionDTOS).build();
                } catch (Exception e) {
                    return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
                }
        }
        if (request.type()== RequestType.LOGOUT) {
            logger.debug("Logout request");
            // LogoutRequest logReq=(LogoutRequest)request;
            UserDTO udto = (UserDTO) request.data();
            User user = DTOUtils.getFromDTO(udto);
            try {
                server.logout(user, this);
                connected = false;
                return okResponse;

            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type()== RequestType.SAVE_PARTICIPANT) {
            logger.debug("SAVE PART");
            ParticipantDTO dto=(ParticipantDTO) request.data();
            Participant participant=DTOUtils.getFromDTO(dto);
            try {
                server.saveParticipant(participant);
                return new Response.Builder().type(ResponseType.SAVED).build();
            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        return response;

    }

    private void sendResponse(Response response) throws IOException{
        logger.debug("sending response "+response);
        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
    }

    @Override
    public void listUpdated() {
        Response resp=new Response.Builder().type(ResponseType.UPDATE).build();
        logger.debug("List was updated");
        try {
            sendResponse(resp);

        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }
}
