package GUI;

import Domain.Bug;
import Domain.User;
import Service.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class developerController implements Initializable, IObserver ,MainWindowController{
    @FXML
    public TableView<Bug> bugTable;
//    @FXML
//    private TableColumn<Bug, Integer> iD;
    @FXML
    public TableColumn<Bug, Integer> Id;

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
        Id.setCellValueFactory(new PropertyValueFactory<>("Id"));
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        Id.setVisible(false);
        description.setCellValueFactory(new PropertyValueFactory<>("Description"));
    }


    public void init() {
        bugTable.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

        bugTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            System.out.println("dasdasdasdasdas");
        });
    }


    @FXML
    private void removeBugClicked() throws Exception {
        ObservableList<Bug> selectedItems = bugTable.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter valid data");
            alert.showAndWait();
            return;
        }
        for (Bug bug : selectedItems) {
            server.removeBug(bug);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(bug.getId().toString());
            alert.setHeaderText(null);
            alert.setContentText("Please enter valid data");
            alert.showAndWait();
            return;
        }
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
