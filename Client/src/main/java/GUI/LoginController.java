package GUI;

import Domain.TypeOfEmployee;
import Domain.User;
import Service.CompetitionsService;
import Service.IServices;
import Service.ParticipantService;
import Service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
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
    private ChoiceBox<String> typeChoiceBox;

    @FXML
    public void initialize() {
        typeChoiceBox.getItems().addAll("developer", "tester");
        typeChoiceBox.setValue("developer"); // default selection
    }
    @FXML
    public void LoginPressed() throws IOException {
        String name = username.getText();
        String passwd = password.getText();
        System.out.println(name + " " + passwd);
        User crtUser = new User(name, passwd,"", TypeOfEmployee.valueOf(typeChoiceBox.getValue()));

        try {
            showMainWindow(crtUser);
            server.login(crtUser,mainWindowController);

           // mainWindowController.loadDataCompetitions();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("User not found");
            stage.close();
        }
    }
    private void showMainWindow(User user) throws Exception {
        if(user.getTypeOfEmployee().equals(TypeOfEmployee.tester)) {
            FXMLLoader mainLoader = new FXMLLoader(getClass().getClassLoader().getResource("developer-view.fxml"));
            Parent mainRoot = mainLoader.load();
            developerController mainWindowController = mainLoader.<developerController>getController();
            mainWindowController.setServer(server);
            mainWindowController.setStage(stage);

            developerController mainController = mainLoader.getController();
            mainController.setServer(server);
            mainController.loadDataBugs();
            mainController.init();
            mainController.setUser(user);
            stage.setScene(new Scene(mainRoot));

            //mainWindowController.loadDataCompetitions();
            stage.show();
            this.setMainController(mainController);
        }
        else{
            FXMLLoader mainLoader = new FXMLLoader(getClass().getClassLoader().getResource("tester-view.fxml"));
            Parent mainRoot = mainLoader.load();
            testerController mainWindowController = mainLoader.<testerController>getController();
            mainWindowController.setServer(server);
            mainWindowController.setStage(stage);

            testerController mainController = mainLoader.getController();
            mainController.setServer(server);
            mainController.loadDataBugs();
            mainController.init();
            mainController.setUser(user);
            stage.setScene(new Scene(mainRoot));

            //mainWindowController.loadDataCompetitions();
            stage.show();
            this.setMainController(mainController);
        }
    }
    private void showAlert(String message) {
        // Show alert in case of errors or empty fields
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
