package uifx;

import appController.mainAppController.AppController;
import appController.dataView.DataViewController;
import appController.taskView.mainTaskView.MainTaskViewController;
import engine.engineGpup.GpupExecution;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.net.URL;


public class UIFX extends Application {

    public static void main(String[] args) {
        UIFX uifx=new UIFX();
        uifx.launch(args);

    }
    //נקודה נקודה - לתת נתיב אבסולוטי מהsrc


    @Override
    public void start(Stage primaryStage) throws Exception {

        //CSSFX.start();
        FXMLLoader loader = new FXMLLoader();
        URL appFXML = getClass().getResource("/appController/mainAppController/mainStage.fxml");
        loader.setLocation(appFXML);
        ScrollPane root = loader.load();

        primaryStage.setTitle("G.P.U.P");

        AppController appController= loader.getController();
        GpupExecution gpupExecution= new GpupExecution(appController);
        appController.setPrimaryStage(primaryStage);
        appController.setGpupExecution(gpupExecution);

        loader= new FXMLLoader();
        URL dataViewFXML = getClass().getResource("/appController/dataView/targetsData.fxml");
        loader.setLocation(dataViewFXML);
        loader.load();
        DataViewController dataViewController = loader.getController();

        appController.setDataViewController(dataViewController);
        dataViewController.setAppController(appController);

        loader= new FXMLLoader();
        URL taskViewFXML = getClass().getResource("/appController/taskView/mainTaskView/mainTaskView.fxml");
        loader.setLocation(taskViewFXML);
        loader.load();
        MainTaskViewController mainTaskViewController = loader.getController();

        appController.setTaskViewController(mainTaskViewController);
        mainTaskViewController.setAppController(appController);

        Scene scene = new Scene(root, 1250,600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}