package rpcProtocol;

import Domain.Competition;
import Domain.Participant;
import Domain.User;
import Service.IObserver;
import Service.IServices;
import dto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServicesRpcProxy implements IServices {
    private String host;
    private int port;

    private static Logger logger = LogManager.getLogger(ServicesRpcProxy.class);

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;
    public ServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses=new LinkedBlockingQueue<Response>();
    }
    public void login(User user, IObserver client) throws Exception {
        initializeConnection();
        UserDTO udto= DTOUtils.getDTO(user);
        Request req=new Request.Builder().type(RequestType.LOGIN).data(udto).build();

        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            this.client=client;
            return;
        }

        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            closeConnection();
            throw new Exception(err);

        }
    }
    public void logout(User user, IObserver client) throws Exception {
        UserDTO udto= DTOUtils.getDTO(user);
        Request req=new Request.Builder().type(RequestType.LOGOUT).data(udto).build();
        sendRequest(req);
        Response response=readResponse();
        closeConnection();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new Exception(err);
        }
    }

    @Override
    public List<Competition> getCompetitionsList() throws Exception {

        Request req=new Request.Builder().type(RequestType.GET_COMPETITION_LIST).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new Exception(err);
        }
        List<CompetitionDTO> frDTO=(List<CompetitionDTO>) response.data();
        List<Competition> competitions= DTOUtils.getFromDTO(frDTO);
        return competitions;
    }

    @Override
    public List<Participant> getParticipantsInCompetition(Competition competition) throws Exception {
        Request req=new Request.Builder().type(RequestType.GET_PARTICIPANTS_IN_COMPETITION).data(DTOUtils.getDTO(competition)).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new Exception(err);
        }
        List<ParticipantDTO> frDTO=(List<ParticipantDTO>) response.data();
        List<Participant> participants = DTOUtils.getFromDTOp(frDTO);
        return participants;
    }

    @Override
    public void saveParticipant(Participant participant) throws Exception {
        Request req=new Request.Builder().type(RequestType.SAVE_PARTICIPANT).data(DTOUtils.getDTO(participant)).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new Exception(err);
        }
    }




    private void sendRequest(Request request)throws Exception {
        logger.debug("Sending request {} ",request);
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }private void initializeConnection() throws Exception {
        try {
            connection=new Socket(host,port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        } catch (IOException e) {
            logger.error("Error initializing connection "+e);
            logger.error(e.getStackTrace());
        }
    }
    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }
    private Response readResponse() throws Exception {
        Response response=null;
        try{
            response=qresponses.take();
        } catch (InterruptedException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
        return response;
    }
    private void closeConnection() {
        logger.debug("Closing connection");
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }

    }
    private void handleUpdate(Response response){
        if (response.type()== ResponseType.UPDATE){
            //User friend= DTOUtils.getFromDTO((UserDTO) response.data());
            logger.debug("List Updated");
            try {
                client.listUpdated();
            } catch (Exception e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
        }
    }
    private boolean isUpdate(Response response){
        return response.type()== ResponseType.UPDATE;
    }
    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    logger.debug("response received "+response);
                    if (isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }else{
                        try {
                            qresponses.put((Response)response);
                        } catch (InterruptedException e) {
                            logger.error(e);
                            logger.error(e.getStackTrace());
                        }
                    }
                } catch (IOException|ClassNotFoundException e) {
                    logger.error("Reading error "+e);
                }
            }
        }
    }
}
