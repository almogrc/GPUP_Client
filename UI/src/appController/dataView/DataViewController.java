package appController.dataView;

import appController.mainAppController.AppController;
import engine.graph.SerialSet;
import engine.graph.Target;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class DataViewController {
    @FXML private GridPane MainScrollPaneData;
    @FXML private VBox SummaryDataVbox;
    @FXML private Label TotalTargetsLabel;
    @FXML private Label TotalRootsLabel;
    @FXML private Label TotalMidLabel;
    @FXML private Label TotalLeafLabel;
    @FXML private Label TotalIndepLabel;
    @FXML private Tab pathLocatorTab;
    @FXML private ChoiceBox<String> pathFromTargetChoice;
    @FXML private ChoiceBox<String> pathToTargetChoice;
    @FXML private ChoiceBox<String> pathRatioChoice;
    @FXML private ListView<String> PathList;
    @FXML private Button pathButtonStart;
    @FXML private ChoiceBox<String> WhatIfTargetChoice;
    @FXML private ChoiceBox<String> WhatIfRatioChoice;
    @FXML private ListView<String> ListWhatIf;
    @FXML private Button WhatIfStartButton;
    @FXML private ChoiceBox<String> CircleTargetChoice;
    @FXML private ListView<String> CircleList;
    @FXML private Button CircleStartButton;
    @FXML private TableView<Target> TargetsDataTable;
    @FXML private TableColumn<Target, String> TargetNameColumn;
    @FXML private TableColumn<Target, Target.Location> TargetLocationColumn;
    @FXML private TableColumn<Target, String> TargetDataColumn;
    @FXML private TableColumn<Target, ?> ReqColumn;
    @FXML private TableColumn<Target, Integer> ReqDirColumn;
    @FXML private TableColumn<Target, Integer> ReqIndirColumn;
    @FXML private TableColumn<Target, ?> DepColumn;
    @FXML private TableColumn<Target, Integer> DepDirColumn;
    @FXML private TableColumn<Target, Integer> DepIndirColumn;
    @FXML private TableColumn<Target, Integer> SerialSetsTargetColumn;
    @FXML private TableView<SerialSet> SerialSetsTable;
    @FXML private TableColumn<SerialSet, String> SerialSetNameColumn;
    @FXML private TableColumn<SerialSet, List<String>> TISColumn;
    @FXML private Label errorPathLabel;
    @FXML private Label errorWhatIfLabel;
    @FXML private Label errorCircleLabel;
    @FXML private GridPane upperGridPaneData;
    @FXML private Button showGraphVizButton;
    @FXML private Button selectPathButton;
    @FXML private TextField insertFileNameTextField;
    //@FXML private Label serialSetMainLabel;


    private SimpleIntegerProperty totalTargets;
    private SimpleIntegerProperty totalRoots;
    private SimpleIntegerProperty totalIndep;
    private SimpleIntegerProperty totalMidd;
    private SimpleIntegerProperty totalLeaves;
    private SimpleStringProperty errorCircle;
    private SimpleStringProperty errorPath;
    private SimpleStringProperty errorWhatIf;
    private AppController appController;
    private SimpleBooleanProperty isPathSelected;
    private File selectedPathForGraphViz;
    private ImageView imageView;


    public DataViewController(){
         totalTargets = new SimpleIntegerProperty(0);
         totalRoots = new SimpleIntegerProperty(0);
         totalIndep = new SimpleIntegerProperty(0);
         totalMidd = new SimpleIntegerProperty(0);
         totalLeaves = new SimpleIntegerProperty(0);
        errorCircle  = new SimpleStringProperty("");
        errorPath = new SimpleStringProperty("");
        errorWhatIf = new SimpleStringProperty("");
        this.isPathSelected = new SimpleBooleanProperty(false);

    }
    @FXML
    private void initialize(){
        StringExpression sb = Bindings.concat("Total number of targets: ", totalTargets);
        TotalTargetsLabel.textProperty().bind(sb);
        sb = Bindings.concat("Number Of Roots: ", totalRoots);
        TotalRootsLabel.textProperty().bind(sb);
        sb = Bindings.concat("Number Of Middles: ", totalMidd);
        TotalMidLabel.textProperty().bind(sb);
        sb = Bindings.concat("Number Of Leaves: ", totalLeaves);
        TotalLeafLabel.textProperty().bind(sb);
        sb = Bindings.concat("Number Of Independents: ", totalIndep);
        TotalIndepLabel.textProperty().bind(sb);
        sb = Bindings.concat("Number Of Independents: ", totalIndep);
        TotalIndepLabel.textProperty().bind(sb);
        sb = Bindings.concat("", errorPath);
        errorPathLabel.textProperty().bind(sb);
        sb = Bindings.concat("", errorWhatIf);
        errorWhatIfLabel.textProperty().bind(sb);
        sb = Bindings.concat("", errorCircle);
        errorCircleLabel.textProperty().bind(sb);

        showGraphVizButton.disableProperty().bind(isPathSelected.not());

        //TotalIndepLabel.textProperty().bind(Bindings.format("%,d",totalLeaves));

    }

    public GridPane getRoot(){return MainScrollPaneData;}

    public void setAppController(AppController appController) {this.appController=appController; }

    public void resetUIData(){
        resetSummary();
        resetDataTable();
        resetSerialSetsTable();
        resetPathTab();
        resetWhatIfTab();
        resetCircleTab();
        resetImage();
    }

    private void resetImage() {
        isPathSelected.set(false);
        imageView=null;
        insertFileNameTextField.clear();
    }

    private void resetSerialSetsTable() {
        SerialSetsTable.setItems(null);
    }

    private void resetPathTab() {
        ObservableList<String> dataTarget= FXCollections.observableArrayList("--Target Name--");
        ObservableList<String> dataRatio = FXCollections.observableArrayList("--Ratio Choice--");
        PathList.setItems(null);
        errorPath.setValue("");
       // pathFromTargetChoice.setValue("--Target Name--");
        pathFromTargetChoice.setItems(dataTarget);
       // pathToTargetChoice.setValue("--Target Name--");
        pathToTargetChoice.setItems(dataTarget);
       // pathRatioChoice.setValue("--Ratio Choice--");
        pathRatioChoice.setItems(dataRatio);
    }

    private void resetCircleTab() {
        ObservableList<String> dataTarget= FXCollections.observableArrayList("--Target Name--");

        CircleList.setItems(null);
        errorCircle.setValue("");
        CircleTargetChoice.setItems(dataTarget);
        //CircleTargetChoice.setValue("--Target Name--");
    }

    private void resetWhatIfTab() {
        ObservableList<String> dataTarget= FXCollections.observableArrayList("--Target Name--");
        ObservableList<String> dataRatio = FXCollections.observableArrayList("--Ratio Choice--");
        ListWhatIf.setItems(null);
        errorWhatIf.setValue("");
        WhatIfTargetChoice.setItems(dataTarget);
       // WhatIfTargetChoice.setValue("--Target Name--");
        WhatIfRatioChoice.setItems(dataRatio);
       // WhatIfRatioChoice.setValue("--Ratio Choice--");
       // pathButtonStart.disabledProperty();
        //WhatIfStartButton.disabledProperty();
        //CircleStartButton.disabledProperty();
    }

    private void resetDataTable() {
        TargetsDataTable.setItems(null);
    }

    private void resetSummary() {
        totalTargets.set(0);
        totalRoots.set(0);
        totalIndep.set(0);
        totalMidd.set(0);
        totalLeaves.set(0);
    }

    public void updateUIData(){
        upperGridPaneData.prefWidthProperty().bind(appController.getRoot().widthProperty());
        initSummary();
        initDataTable();
        initSerialSetsTable();
        initPathTab();
        initWhatIfTab();
        initCircleTab();

    }

    private void initPathTab() {
        ObservableList<String> TargetOptions = FXCollections.observableArrayList(appController.getTargetNamesList());
        ObservableList<String> RatioOptions  = FXCollections.observableArrayList("Required For","Depends On");
        //pathFromTargetChoice.setValue("--Target Name--"); // this statement shows default value
        pathFromTargetChoice.setItems(TargetOptions);
        //pathToTargetChoice.setValue("--Target Name--"); // this statement shows default value
        pathToTargetChoice.setItems(TargetOptions);
       // pathRatioChoice.setValue("--Ratio Choice--"); // this statement shows default value
        pathRatioChoice.setItems(RatioOptions);


    }
    private void initWhatIfTab(){
        ObservableList<String> TargetOptions = FXCollections.observableArrayList(appController.getTargetNamesList());
        ObservableList<String> RatioOptions  = FXCollections.observableArrayList("Required For","Depends On");
      //  WhatIfTargetChoice.setValue("--Target Name--"); // this statement shows default value
        WhatIfTargetChoice.setItems(TargetOptions);
       // WhatIfRatioChoice.setValue("--Ratio Choice--"); // this statement shows default value
        WhatIfRatioChoice.setItems(RatioOptions);
    }

    private void initCircleTab() {
        ObservableList<String> TargetOptions = FXCollections.observableArrayList(appController.getTargetNamesList());
      //  CircleTargetChoice.setValue("--Target Name--"); // this statement shows default value
        CircleTargetChoice.setItems(TargetOptions);
    }

    public void initSummary() {

        totalTargets.set(appController.getNumOfTargets());
        totalIndep.set(appController.getNumOfIndependent());
        totalLeaves.set(appController.getNumOfLeaf());
        totalRoots.set(appController.getNumOfRoot());
        totalMidd.set(appController.getNumOfMiddle());
    }

    public void initDataTable() {

        final ObservableList<Target> data = FXCollections.observableArrayList(appController.getTargetList());

        TargetNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );
        TargetDataColumn.setCellValueFactory(
                new PropertyValueFactory<>("data")
        );
        TargetLocationColumn.setCellValueFactory(
                new PropertyValueFactory<>("location")
        );
        ReqDirColumn.setCellValueFactory(
                new PropertyValueFactory<>("numOfDirectRequiredFor")
        );
        ReqIndirColumn.setCellValueFactory(
                new PropertyValueFactory<>("numOfIndirectRequiredFor")
        );
        DepDirColumn.setCellValueFactory(
                new PropertyValueFactory<>("numOfDirectDependsOn")
        );
        DepIndirColumn.setCellValueFactory(
                new PropertyValueFactory<>("numOfIndirectDependsOn")
        );
        SerialSetsTargetColumn.setCellValueFactory(
                new PropertyValueFactory<>("serialSetsNumber")
        );

        TargetsDataTable.setItems(data);
    }

    public void initSerialSetsTable() {

        final ObservableList<SerialSet> data = FXCollections.observableArrayList(appController.getListOfSerialSet());

        SerialSetNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );
        TISColumn.setCellValueFactory(
                new PropertyValueFactory<>("targetSetNamesOfSerialSet")
        );

        SerialSetsTable.setItems(data);
    }
    @FXML
    void handleCircleButtonOnAction(ActionEvent event) {
       if(CircleTargetChoice.getValue()== null || CircleTargetChoice.getValue().equals("--Target Name--")){
           errorCircle.setValue("Please choose a target to continue");
       }
       else{
           errorCircle.setValue("");
           final ObservableList<String> data = FXCollections.observableArrayList(appController.getCircles(CircleTargetChoice.getValue()).toString());
           CircleList.setItems(data);
       }
    }

    @FXML
    void handlePathButtonOnAction(ActionEvent event) {

        if(pathFromTargetChoice.getValue() == null || pathFromTargetChoice.getValue().equals("--Target Name--")){
            errorPath.setValue("Please choose starting target to continue");
        }else if(pathToTargetChoice.getValue()  == null ||  pathToTargetChoice.getValue().equals("--Target Name--")){
            errorPath.setValue("Please choose ending target to continue");
        }else if(pathRatioChoice.getValue() == null || pathRatioChoice.getValue().equals("--Ratio Choice--")){
            errorPath.setValue("Please choose ratio to continue");
        }
        else{
            errorPath.setValue("");
            boolean required = false;
            if(pathRatioChoice.getValue().equals("Required For")){
                required=true;
            }
            String dto = appController.getPaths(pathFromTargetChoice.getValue(),pathToTargetChoice.getValue(),required).toString();
            List<String> res = Arrays.asList(dto.split("\n"));
            final ObservableList<String> data = FXCollections.observableArrayList(res);
            PathList.setItems(data);
        }

    }

    @FXML
    void handleWhatIfButtonOnAction(ActionEvent event) {
        if( WhatIfTargetChoice.getValue()== null || WhatIfTargetChoice.getValue().equals("--Target Name--")){
            errorWhatIf.setValue("Please choose a target to continue");
        }else if( WhatIfRatioChoice.getValue()== null || WhatIfRatioChoice.getValue().equals("--Ratio Choice--")){
            errorWhatIf.setValue("Please choose ratio to continue");
        }
        else{
            errorWhatIf.setValue("");
            boolean required=false;
            if(WhatIfRatioChoice.getValue().equals("Required For")) {
                required = true;
            }

            final ObservableList<String> data = FXCollections.observableArrayList(appController.getAllOfIndirect(WhatIfTargetChoice.getValue(),required));
            ListWhatIf.setItems(data);
        }

    }



    @FXML
    void showGraphVizOnAction(ActionEvent event) {
        if(imageView==null){
            if(selectedPathForGraphViz !=null){
                appController.getGraphViz(selectedPathForGraphViz.getAbsolutePath() ,insertFileNameTextField.getText());
                File file = new File(selectedPathForGraphViz.toString()+"\\"+insertFileNameTextField.getText()+".png");
                Image image=new Image(file.toURI().toString(),1000,800,true,true);
                imageView=new ImageView();
                imageView.setImage(image);

            }
        }
        StackPane root = new StackPane();
        ScrollPane scrollPane=new ScrollPane();
        scrollPane.setContent(root);
        Scene scene = new Scene(scrollPane, 600, 400);

        root.getChildren().add(imageView);


        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Graph Viz");



        //root.getChildren().add();

        stage.setScene(scene);
        stage.show();
     }


    @FXML
    void selectPathOnAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Path");
        selectedPathForGraphViz = directoryChooser.showDialog(null);

        if (selectedPathForGraphViz == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File error");
            alert.setContentText("No File Was Chosen");
            alert.show();
            return;

        }else {
            isPathSelected.set(true);
        }
    }
}
