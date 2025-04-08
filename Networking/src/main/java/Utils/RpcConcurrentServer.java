package Utils;

import Service.IServices;
import Utils.AbsConcurrentServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rpcProtocol.ClientRpcWorker;

import java.net.Socket;


public class RpcConcurrentServer extends AbsConcurrentServer {
    private IServices chatServer;
    private static Logger logger = LogManager.getLogger(RpcConcurrentServer.class);
    public RpcConcurrentServer(int port, IServices chatServer) {
        super(port);
        this.chatServer = chatServer;
        logger.info("Chat- ChatRpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        // ChatClientRpcWorker worker=new ChatClientRpcWorker(chatServer, client);
        ClientRpcWorker worker=new ClientRpcWorker(chatServer, client);

        Thread tw=new Thread(worker);
        return tw;
    }

    @Override
    public void stop(){
        logger.info("Stopping services ...");
    }
}
