package GUI;

import Domain.Bug;
import Domain.Competition;
import Domain.Participant;
import Domain.User;
import Service.*;
import Utils.UtilFunctions;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class testerController implements Initializable, IObserver,MainWindowController {
    @FXML
    public TableView<Bug> bugTable;

//    @FXML
//    public TableColumn<Competition, Integer> Id;


    public TextField nameField;
    public TextField descriptionField;
    private IServices server;
    public TableColumn<Bug, String> name;

    public TableColumn<Bug, String> description;

    private Stage stage;
    private User user;

    public void setServer(IServices server) {
        this.server = server;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Id.setCellValueFactory(new PropertyValueFactory<>("Id"));
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        description.setCellValueFactory(new PropertyValueFactory<>("Description"));
    }


    public void init() {
        bugTable.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

        bugTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            System.out.println("dasdasdasdasdas");
        });
    }


    @FXML
    private void reportBugClicked() throws Exception {
        if (nameField.getText().isEmpty() ||
                name.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter valid data");
            alert.showAndWait();
            return;
        }
        Bug bug=new Bug(descriptionField.getText(), nameField.getText());
        server.saveBug(bug);
        //participantService.save(participant);
        listUpdated();

    }

    public void loadDataBugs() throws Exception {

        Platform.runLater(() -> {
            List<Bug> freshList = null;
            try {
                freshList = server.getBugsList();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            bugTable.getItems().setAll(freshList);  // replace content with new instances
            bugTable.refresh();  // force re-evaluation of cell factories
        });
    }
//
//    @FXML
//    public void logoutPressed() throws Exception {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
//        Parent root = loader.load();
//        Scene scene = new Scene(root);
//        LoginController controller = loader.getController();
//        controller.setStage(stage);
//        controller.initServices(participantService, userService, competitionsService);
//        controller.setServer(server);
//        stage.setTitle("App");
//        stage.setScene(scene);
//        stage.show();
//        System.out.println(user.getUsername() + "  " + user.getPassword());
//        server.logout(user, this);
//    }

    @Override
    public void listUpdated() throws Exception {
//        Platform.runLater(() -> {
        try {
            System.out.println("da intra aici");
            loadDataBugs();  // This now reloads fresh objects and updates the table



        } catch (Exception e) {
            e.printStackTrace();
        }
//        });
    }


}
