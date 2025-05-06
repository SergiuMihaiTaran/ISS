package protobuff;

import Domain.Competition;
import Domain.Participant;
import Domain.User;
import Service.IObserver;
import Service.IServices;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProtoProxy implements IServices {
    private String host;
    private int port;
    private IObserver client;

    private InputStream input;
    private OutputStream output;
    private Socket connection;

    private BlockingQueue<Protobufs.Response> qresponses;
    private volatile boolean finished;

    public ProtoProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<>();
    }

    @Override
    public void login(User user, IObserver client) throws Exception {
        initializeConnection();
        sendRequest(ProtoUtils.createLoginRequest(user));
        Protobufs.Response response = readResponse();
        if (response.getType() == Protobufs.Response.Type.OK) {
            this.client = client;
        } else {
            closeConnection();
            throw new Exception(ProtoUtils.getError(response));
        }
    }

    @Override
    public void logout(User user, IObserver client) throws Exception {
        sendRequest(ProtoUtils.createLogoutRequest(user));
        Protobufs.Response response = readResponse();
        closeConnection();
        if (response.getType() == Protobufs.Response.Type.ERROR) {
            throw new Exception(ProtoUtils.getError(response));
        }
    }

    @Override
    public List<Competition> getCompetitionsList() throws Exception {
        sendRequest(ProtoUtils.createGetCompetitionsRequest());
        Protobufs.Response response = readResponse();
        if (response.getType() == Protobufs.Response.Type.ERROR) {
            throw new Exception(ProtoUtils.getError(response));
        }
        return ProtoUtils.getCompetitions(response);
    }

    @Override
    public List<Participant> getParticipantsInCompetition(Competition competition) throws Exception {
        sendRequest(ProtoUtils.createGetParticipantsInCompetitionRequest(competition));
        Protobufs.Response response = readResponse();
        if (response.getType() == Protobufs.Response.Type.ERROR) {
            throw new Exception(ProtoUtils.getError(response));
        }
        return ProtoUtils.getParticipants(response);
    }

    @Override
    public void saveParticipant(Participant participant) throws Exception {
        sendRequest(ProtoUtils.createSaveParticipantRequest(participant));
        System.out.println(participant.getCompetitions());
        Protobufs.Response response = readResponse();
        if (response.getType() == Protobufs.Response.Type.ERROR) {
            throw new Exception(ProtoUtils.getError(response));
        }
    }

    private void initializeConnection() throws Exception {
        try {
            connection = new Socket(host, port);
            output = connection.getOutputStream();
            input = connection.getInputStream();
            finished = false;
            startReader();
        } catch (IOException e) {
            throw new Exception("Error connecting: " + e.getMessage(), e);
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(Protobufs.Request request) throws Exception {
        try {
            System.out.println("Sending request: " + request.getType());
            request.writeDelimitedTo(output);
        } catch (IOException e) {
            throw new Exception("Error sending request: " + e.getMessage(), e);
        }
    }

    private Protobufs.Response readResponse() throws Exception {
        try {
            return qresponses.take();
        } catch (InterruptedException e) {
            throw new Exception("Error reading response: " + e.getMessage(), e);
        }
    }

    private boolean isUpdateResponse(Protobufs.Response.Type type) {
        return type == Protobufs.Response.Type.UPDATE;
    }

    private void handleUpdate(Protobufs.Response updateResponse) {
        System.out.println("Received update response");
        try {
            client.listUpdated();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ReaderThread implements Runnable {
        @Override
        public void run() {
            while (!finished) {
                try {
                    Protobufs.Response response = Protobufs.Response.parseDelimitedFrom(input);
                    System.out.println("Response received: " + response.getType());

                    if (isUpdateResponse(response.getType())) {
                        handleUpdate(response);
                    } else {
                        qresponses.put(response);
                    }
                } catch (IOException | InterruptedException e) {
                    System.out.println("Reader error: " + e.getMessage());
                }
            }
        }
    }
}
