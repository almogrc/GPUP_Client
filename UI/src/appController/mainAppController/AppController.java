package appController.mainAppController;

import appController.dataView.DataViewController;
import appController.taskView.mainTaskView.MainTaskViewController;
import engine.dto.Dto;
import engine.engineGpup.DetailsForTask;
import engine.engineGpup.GpupExecution;
import engine.graph.SerialSet;
import engine.graph.Target;
import engine.graph.WrapsTarget;
import javafx.animation.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class AppController {

    @FXML private Button xmlFileLoaderButton;
    @FXML private ImageView image;
    @FXML private Button showTargetsDataButton;
    @FXML private Button runTaskButton;
    @FXML private BorderPane MainBorderPane;
    @FXML private ScrollPane mainScrollBar;

    private SimpleBooleanProperty isFileSelected;

    private GpupExecution gpupExecution;
    private Stage primaryStage;
    private DataViewController dataViewController;
    private MainTaskViewController mainTaskViewController;
   // private TempController tempController;

    public AppController(){
        this.isFileSelected = new SimpleBooleanProperty(false);

    }

    @FXML private void initialize() {
       showTargetsDataButton.disableProperty().bind(isFileSelected.not());
       runTaskButton.disableProperty().bind(isFileSelected.not());
       animationsSetter();

       //mainScrollBar.setFitToHeight(true);
       //mainScrollBar.setFitToWidth(true);
    }

    private void animationsSetter() {
        RotateTransition rotateTransition =new RotateTransition();
        rotateTransition.setNode(image);
        rotateTransition.setDuration(Duration.millis(2500));
        rotateTransition.setInterpolator(Interpolator.LINEAR);
        rotateTransition.setByAngle(720);
        rotateTransition.play();
        FadeTransition fadeTransition=new FadeTransition();
        fadeTransition.setNode(image);
        fadeTransition.setDuration(Duration.millis(2500));
        fadeTransition.setInterpolator(Interpolator.LINEAR);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    @FXML void AnimationsOff(ActionEvent event) {
        mainTaskViewController.animationsOff();
    }

    @FXML void animationsOn(ActionEvent event) {
        mainTaskViewController.animationsOn();
    }

    public BorderPane getRoot(){ return MainBorderPane; }
    @FXML
    public void handleLoadXMLClicked(ActionEvent actionEvent) throws JAXBException, FileNotFoundException {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Select XML File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML file","*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }
        isFileSelected.set(true);
        try {
            gpupExecution.initGpupExecution(selectedFile);
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File error");
            alert.setContentText(e.getMessage());
            alert.show();
        }

        //MainBorderPane.setCenter(tempController.getRoot());
        dataViewController.resetUIData();
        mainTaskViewController.resetTaskData();

    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage=primaryStage;
    }

    public void setGpupExecution(GpupExecution gpupExecution) {
        this.gpupExecution=gpupExecution;
    }

    @FXML public void clickSTDButtonListener(ActionEvent event) {

        MainBorderPane.setCenter(dataViewController.getRoot());
        dataViewController.resetUIData();
        dataViewController.updateUIData();

    }

    @FXML void handleRunTaskClicked(ActionEvent event) {
        MainBorderPane.setCenter(mainTaskViewController.getRoot());
        mainTaskViewController.initTreeView();
        mainTaskViewController.initMainTask();
    }

    public void setDataViewController(DataViewController dataViewController) { this.dataViewController=dataViewController; }

    public List<Target> getTargetList(){return gpupExecution.getTargetList();}

    public List<String> getTargetNamesList(){return gpupExecution.getTargetNamesList(); }

    public int getNumOfTargets(){ return gpupExecution.getNumOfTargets();}

    public int getNumOfIndependent(){ return gpupExecution.getNumOfIndependent();}

    public int getNumOfLeaf(){return gpupExecution.getNumOfLeaf();}

    public int getNumOfMiddle(){return gpupExecution.getNumOfMiddle();}

    public int getNumOfRoot(){return gpupExecution.getNumOfRoot();}

    public WrapsTarget getWrapsTargetByName(String name){return gpupExecution.getWrapsTargetByName(name);}

    public List<SerialSet> getListOfSerialSet(){return gpupExecution.getListOfSerialSet(); }

    public Dto getCircles(String targetName) {
       return gpupExecution.getCircleOfTarget(targetName);
    }

    public Dto getPaths(String from, String to, boolean required) {
        return gpupExecution.getAllPathsBetweenTargets(from,to,required);
    }

    public List<String> getAllOfIndirect(String targetName,boolean isRequired) {
        return gpupExecution.getAllOfIndirect(targetName,isRequired);
    }

    public void setTaskViewController(MainTaskViewController mainTaskViewController) {
        this.mainTaskViewController=mainTaskViewController;
    }


    public List<WrapsTarget> getWrapsTargetList(){return gpupExecution.getWrapsTargetList();}

    public int getNumOfThreads() {
        return gpupExecution.getNumOfThreads();
    }

    public void runTask(DetailsForTask newDetail,
                        Consumer<String>successSummary,
                        Consumer<String>successWWSummary,
                        Consumer<String>failureSummary,
                        Consumer<String>skippedSummary,
                        Consumer<String>summary,
                        Consumer<String> logData,
                        Runnable onFinish) {
        gpupExecution.runTask(newDetail,successSummary,successWWSummary,failureSummary,skippedSummary,summary,logData,onFinish);
    }

    public void bindTaskToUIComponents(Task<Boolean> myRun,Runnable onFinish) {
        mainTaskViewController.bindTaskToMainTaskViewControllerComponents(myRun, onFinish);
    }
    @FXML
    void handleDefaultCSSChoice(ActionEvent event) {
        primaryStage.getScene().getStylesheets().removeAll(this.getClass().getResource("Modern.css").toExternalForm(),this.getClass().getResource("Sea.css").toExternalForm());
    }

    @FXML
    void handleModernCSSChoice(ActionEvent event) {
        primaryStage.getScene().getStylesheets().remove(this.getClass().getResource("Sea.css").toExternalForm());
        primaryStage.getScene().getStylesheets().add(this.getClass().getResource("Modern.css").toExternalForm());
    }

    @FXML
    void handleSeaCSSChoice(ActionEvent event) {
        primaryStage.getScene().getStylesheets().remove(this.getClass().getResource("Modern.css").toExternalForm());
        primaryStage.getScene().getStylesheets().add(this.getClass().getResource("Sea.css").toExternalForm());
        primaryStage.getScene().fillProperty().setValue(Color.rgb(73,173,154,1));


    }

    public void handlePause(boolean isStopTask) {
        gpupExecution.stopTask(isStopTask);
    }

    public void setNumOfThreads(int numOfThreads) {
        gpupExecution.setNumOfThreads(numOfThreads);
    }

    public void getGraphViz(String outPutPath, String filesNames) {
        gpupExecution.getGraphUsingGraphViz(outPutPath, filesNames);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
