package GUI;

import Domain.Entity;
import Domain.User;
import Service.CompetitionsService;
import Service.ParticipantService;
import Service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController  {
    private UserService userService;
    private ParticipantService participantService;
    private CompetitionsService competitionsService;
    public Label errorLabel;
    @FXML
    private TextField username;

    @FXML
    private TextField password;
    @FXML
    private Label welcomeText;
    private Stage stage;


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

        User currentLogin=userService.searchByNameAndPassword(username.getText(),password.getText());
        if(currentLogin==null) {
            showAlert("Username/Password is incorrect");
            return;
        }
        showMainWindow(currentLogin);

    }
    private void showMainWindow(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main_window_view.fxml"));
        Parent root =  loader.load();
        Scene scene = new Scene(root);
        System.out.println(stage.toString());
        MainWindowController controller = loader.getController();
        controller.setStage(stage);
        controller.initResources(participantService,userService,competitionsService,user);
        stage.setTitle("App");
        stage.setScene(scene);
        stage.show();
    }
    private void showAlert(String message) {
        // Show alert in case of errors or empty fields
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
