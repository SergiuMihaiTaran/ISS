

import GUI.LoginController;
import GUI.MainWindowController;
import Service.IServices;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuff.ProtoProxy;

import java.io.File;
import java.io.IOException;
import java.util.Properties;


public class StartClient extends Application {
    public static void main(String[] args) {
        launch();
    }
    private Stage primaryStage;

    private static int defaultChatPort = 55555;
    private static String defaultServer = "localhost";

    private static Logger logger = LogManager.getLogger(StartClient.class);

    public void start(Stage primaryStage) throws Exception {
        logger.debug("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartClient.class.getResourceAsStream("/client.properties"));
            logger.info("Client properties set {} ", clientProps);
            // clientProps.list(System.out);
        } catch (IOException e) {
            logger.error("Cannot find client.properties " + e);
            logger.debug("Looking into folder {}", (new File(".")).getAbsolutePath());
            return;
        }
        String serverIP = clientProps.getProperty("server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("server.port"));
        } catch (NumberFormatException ex) {
            logger.error("Wrong port number " + ex.getMessage());
            logger.debug("Using default port: " + defaultChatPort);
        }
        logger.info("Using server IP " + serverIP);
        logger.info("Using server port " + serverPort);

        IServices server = new ProtoProxy(serverIP, serverPort);

        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("login-view.fxml"));
        Parent root=loader.load();
        LoginController ctrl =
                loader.<LoginController>getController();
        ctrl.setServer(server);
        FXMLLoader mainLoader = new FXMLLoader(getClass().getClassLoader().getResource("main_window_view.fxml"));
        Parent mainRoot = mainLoader.load();
        MainWindowController mainWindowController=mainLoader.<MainWindowController>getController();
        mainWindowController.setServer(server);
        ctrl.setStage(primaryStage);
        ctrl.setMainController(mainWindowController);
        MainWindowController mainController = mainLoader.getController();
        mainController.setServer(server);

        primaryStage.setTitle("MPP chat");
        primaryStage.setScene(new Scene(root, 500, 230));
        primaryStage.show();
    }
}


