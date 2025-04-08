package GUI;

import Domain.Competition;
import Domain.Participant;
import Domain.User;
import Service.*;
import Utils.UtilFunctions;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable, IObserver {
    @FXML
    public TableView<Competition> competitionTable;
    @FXML
    public TableView<Participant> participantTable;
//    @FXML
//    public TableColumn<Competition, Integer> Id;

    private IServices server;
    public TableColumn<Competition, String> style;

    public TableColumn<Competition, String> distance;
    public TableColumn<Competition, Integer> noParticipantsColumn;
    public TableColumn<Participant, String> name;
    public TableColumn<Participant, Integer> age;
    public TableColumn<Participant, Integer> noCompetitionsColumn;

    public TextField nameLabel;
    public TextField nameField;
    public TextField ageField;
    private Stage stage;
    private ParticipantService participantService;
    private CompetitionsService competitionsService;
    private UserService userService;
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
        style.setCellValueFactory(new PropertyValueFactory<>("Style"));
        distance.setCellValueFactory(new PropertyValueFactory<>("Distance"));
        noParticipantsColumn.setCellValueFactory(cellData ->
        {
            try {
                return new SimpleIntegerProperty(getNoParticipants(cellData.getValue())).asObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        age.setCellValueFactory(new PropertyValueFactory<>("Age"));
        noCompetitionsColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(getNoCompetions(cellData.getValue())).asObject());
    }

    private int getNoCompetions(Participant participant) {
        return participant.getCompetitions().size();
    }

    private int getNoParticipants(Competition competition) throws Exception {
        //return 0;
        return server.getParticipantsInCompetition(competition).size();
    }

    public void initResources(ParticipantService participantService, UserService userService,
                              CompetitionsService competitionsService, User user) throws Exception {
        this.participantService = participantService;
        this.userService = userService;
        this.competitionsService = competitionsService;
        this.user = user;
        loadDataCompetitions();

    }

    public void init() {
        competitionTable.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

        competitionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            System.out.println("dasdasdasdasdas");
            if (newSelection != null) {
                try {

                    showParticipants(newSelection);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void showParticipants(Competition competition) throws Exception {
        Platform.runLater(() -> {
            List<Participant> participants = null;
            try {
                participants = server.getParticipantsInCompetition(competition);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            participantTable.getItems().clear();
            ObservableList<Participant> participants1 = FXCollections.observableArrayList(participants);
            participantTable.setItems(participants1);
        });
    }

    @FXML
    private void addParticipantClicked() throws Exception {
        ObservableList<Competition> selectedCompetitions = competitionTable.getSelectionModel().getSelectedItems();
        if (selectedCompetitions.isEmpty() || nameField.getText().isEmpty() ||
                ageField.getText().isEmpty() || !UtilFunctions.isNumeric(ageField.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter valid data");
            alert.showAndWait();
            return;
        }
        Participant participant = new Participant(nameField.getText(), Integer.parseInt(ageField.getText()), selectedCompetitions);
        server.saveParticipant(participant);
        //participantService.save(participant);
        listUpdated();
    }

    public void loadDataCompetitions() throws Exception {

        Platform.runLater(() -> {
            List<Competition> freshList = null;
            try {
                freshList = server.getCompetitionsList();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            competitionTable.getItems().setAll(freshList);  // replace content with new instances
            competitionTable.refresh();  // force re-evaluation of cell factories
        });
    }

    @FXML
    public void logoutPressed() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        LoginController controller = loader.getController();
        controller.setStage(stage);
        controller.initServices(participantService, userService, competitionsService);
        controller.setServer(server);
        stage.setTitle("App");
        stage.setScene(scene);
        stage.show();
        System.out.println(user.getUsername() + "  " + user.getPassword());
        server.logout(user, this);
    }

    @Override
    public void listUpdated() throws Exception {
//        Platform.runLater(() -> {
        try {
            System.out.println("da intra aici");
            loadDataCompetitions();  // This now reloads fresh objects and updates the table

            ObservableList<Competition> selectedCompetitions =
                    competitionTable.getSelectionModel().getSelectedItems();

            if (!selectedCompetitions.isEmpty()) {
                showParticipants(selectedCompetitions.get(0));  // refresh detail table
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
//        });
    }


}
