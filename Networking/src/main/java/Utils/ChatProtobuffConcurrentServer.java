package Utils;

import Service.IServices;

import java.net.Socket;

//
//public class ChatProtobuffConcurrentServer extends AbsConcurrentServer {
//    private IServices chatServer;
//    public ChatProtobuffConcurrentServer(int port, IServices chatServer) {
//        super(port);
//        this.chatServer = chatServer;
//        System.out.println("Chat- ChatProtobuffConcurrentServer");
//    }
//
//    @Override
//    protected Thread createWorker(Socket client) {
//        ProtoChatWorker worker=new ProtoChatWorker(chatServer,client);
//        Thread tw=new Thread(worker);
//        return tw;
//    }
//}