package GUI;

import Domain.Competition;
import Domain.Participant;
import Domain.User;
import Service.CompetitionsService;
import Service.ParticipantService;
import Service.UserService;
import Utils.UtilFunctions;
import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    public TableView<Competition> competitionTable;
    public TableView<Participant> participantTable;
    @FXML
    public TableColumn<Competition, Integer> Id;


    public TableColumn<Competition, String> style;

    public TableColumn<Competition, String> distance;
    public TableColumn<Competition,Integer> noParticipantsColumn;
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

    public void setStage(Stage stage) {
        this.stage=stage;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Id.setCellValueFactory(new PropertyValueFactory<>("Id"));
        style.setCellValueFactory(new PropertyValueFactory<>("Style"));
        distance.setCellValueFactory(new PropertyValueFactory<>("Distance"));
        noParticipantsColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(getNoParticipants(cellData.getValue())).asObject());
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        age.setCellValueFactory(new PropertyValueFactory<>("Age"));
        noCompetitionsColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(getNoCompetions(cellData.getValue())).asObject());
    }
    private int getNoCompetions(Participant participant) {
        return participant.getCompetitions().size();
    }
    private int getNoParticipants(Competition competition) {
        return participantService.getParticipantsInCompetition(competition).size();
    }
        public void initResources(ParticipantService participantService,UserService userService,
                              CompetitionsService competitionsService, User user) {
        this.participantService = participantService;
        this.userService = userService;
        this.competitionsService = competitionsService;
        this.user = user;
        competitionTable.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        loadDataCompetitions();
            competitionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    showParticipants(newSelection);
                }
            });
    }
    private void showParticipants(Competition competition) {
        List<Participant> participants= participantService.getParticipantsInCompetition(competition);
        participantTable.getItems().clear();
        ObservableList<Participant> participants1= FXCollections.observableArrayList(participantService.getParticipantsInCompetition(competition));
        participantTable.setItems(participants1);
    }
    @FXML
    private void addParticipantClicked(){
        ObservableList<Competition> selectedCompetitions = competitionTable.getSelectionModel().getSelectedItems();
        if(selectedCompetitions.isEmpty() || nameField.getText().isEmpty() ||
                ageField.getText().isEmpty() || !UtilFunctions.isNumeric(ageField.getText())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter valid data");
            alert.showAndWait();
            return;
        }
        Participant participant=new Participant(nameField.getText(),Integer.parseInt(ageField.getText()),selectedCompetitions);

        participantService.save(participant);

    }

    private void loadDataCompetitions(){
        competitionTable.getItems().clear();
        ObservableList<Competition> competitions= FXCollections.observableArrayList(competitionsService.findAll());
        competitionTable.setItems(competitions);
    }
@FXML
    public void logoutPressed() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
    Parent root =  loader.load();
    Scene scene = new Scene(root);
    LoginController controller = loader.getController();
    controller.setStage(stage);
    controller.initServices(participantService,userService,competitionsService);
    stage.setTitle("App");
    stage.setScene(scene);
    stage.show();
    }
}
