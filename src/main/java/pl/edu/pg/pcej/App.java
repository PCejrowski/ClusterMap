package pl.edu.pg.pcej;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App extends Application {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    public static final String FXML_FILE = "/fxml/clusterMap.fxml";
    public static final String STYLES_CSS = "/styles/styles.css";
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    public static final String TITLE = "Cluster Map by Paweł Cejrowski";

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        log.trace("Loading FXML file: " + FXML_FILE);
        Parent rootNode = loader.load(getClass().getResourceAsStream(FXML_FILE));
        Scene scene = new Scene(rootNode, WIDTH, HEIGHT);
        scene.getStylesheets().add(STYLES_CSS);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }
}
