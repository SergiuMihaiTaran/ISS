import GUI.LoginController;
import Repository.CompetitionsDBRepository;
import Repository.ParticipantsRepository;
import Repository.UsersDBRepository;

import Service.CompetitionsService;
import Service.ParticipantService;
import Service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartApplication extends Application {
    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Hello World");
        Properties props=new Properties();
        try {
            props.load(new FileReader("bd.config"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }
        UsersDBRepository repository=new UsersDBRepository(props);
        UserService userService=new UserService(repository);
        CompetitionsDBRepository competitionsRepository=new CompetitionsDBRepository(props);
        CompetitionsService competitionsService=new CompetitionsService(competitionsRepository);
        ParticipantsRepository participantsRepository=new ParticipantsRepository(props);
        ParticipantService participantService=new ParticipantService(participantsRepository);
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("login-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        LoginController controller = fxmlLoader.getController();
        controller.initServices(participantService,userService,competitionsService);
        stage.setTitle("App");
        controller.setStage(stage);
        stage.setScene(scene);
        stage.show();
    }
}
