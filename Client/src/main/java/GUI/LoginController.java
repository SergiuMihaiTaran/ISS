package GUI;

import Domain.User;
import Service.CompetitionsService;
import Service.IServices;
import Service.ParticipantService;
import Service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController  {
    private UserService userService;
    private ParticipantService participantService;
    private CompetitionsService competitionsService;
    private IServices server;
    public Label errorLabel;
    private MainWindowController mainWindowController;
    @FXML
    private TextField username;
    public void setServer(IServices s){
        server=s;
    }
    @FXML
    private TextField password;
    @FXML
    private Label welcomeText;
    private Stage stage;

    public void setMainController(MainWindowController mainWindowController){
        this.mainWindowController=mainWindowController;
    }
    public void setStage(Stage stage) {
        this.stage=stage;
    }
    public void initServices(ParticipantService participantService, UserService userService, CompetitionsService competitionsService) {
        this.participantService = participantService;
        this.userService = userService;
        this.competitionsService = competitionsService;
    }


    @FXML
    public void LoginPressed() throws IOException {
        String name = username.getText();
        String passwd = password.getText();
        System.out.println(name + " " + passwd);
        User crtUser = new User(name, passwd,"");

        try {
            showMainWindow(crtUser);
            server.login(crtUser,mainWindowController);

           // mainWindowController.loadDataCompetitions();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showMainWindow(User user) throws Exception {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getClassLoader().getResource("main_window_view.fxml"));
        Parent mainRoot = mainLoader.load();
        MainWindowController mainWindowController=mainLoader.<MainWindowController>getController();
        mainWindowController.setServer(server);
        mainWindowController.setStage(stage);

        MainWindowController mainController = mainLoader.getController();
        mainController.setServer(server);
        mainController.loadDataCompetitions();
        mainController.init();
        mainController.setUser(user);
        stage.setScene(new Scene(mainRoot));

        //mainWindowController.loadDataCompetitions();
        stage.show();
        this.setMainController(mainController);
    }
    private void showAlert(String message) {
        // Show alert in case of errors or empty fields
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
