package appController.taskView.mainTaskView;

import appController.mainAppController.AppController;
//import appController.taskView.subcomponents.flowPaneOfTargetChoiseBoxes.FlowPaneTargetsController;
import appController.taskView.subcomponents.runTaskTable.RunTableController;
import appController.taskView.subcomponents.taskPreference.CompileTaskPrefController;
import appController.taskView.subcomponents.taskPreference.SimulationTaskPrefController;
import appController.taskView.subcomponents.upperTask.TargetChooserUpperTaskController;
import engine.engineGpup.DetailsForTask;
import engine.graph.Target;
import engine.graph.WrapsTarget;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MainTaskViewController {
    @FXML private GridPane mainGridPane;
   // @FXML private ScrollPane MainScrollPaneTask;
   // @FXML private FlowPane flowPaneTargets;
    @FXML private GridPane WhatIfUpperTaskGridPane;
    @FXML private TargetChooserUpperTaskController WhatIfUpperTaskGridPaneController;
    @FXML private GridPane runTaskTableViewGridPane;
    @FXML private RunTableController runTaskTableViewGridPaneController;
    @FXML private TableView<WrapsTarget> PreTaskTableView;
    @FXML private TableColumn<WrapsTarget, String> NameColumnPre;
    @FXML private TableColumn<WrapsTarget, CheckBox> CheckBoxColumnPre;
    @FXML private TableColumn<WrapsTarget, String> LocationColumnPre;
    @FXML private TableColumn<WrapsTarget, String> DataColumnPre;
    @FXML private TableColumn<WrapsTarget, String> FinishColumnPre;
    @FXML private TableColumn<WrapsTarget, String> StatusColumnPre;
    @FXML private ChoiceBox<Integer> threadsNumberChoiceBox;
    @FXML private Label errorTypeTaskOrNoTargetsOrNoThreadsLabel;
    @FXML private Button ProceedButton;
    @FXML private ChoiceBox<String> typeOfTaskChoiceBox;
    @FXML private BorderPane PrefTaskBorderPane;
    @FXML private Label ErrorPrefLabel;
    @FXML private Button LoadButton;
    @FXML private ChoiceBox<String> HowToStartChoicePart;
    @FXML private ChoiceBox<String> liveDataOnTargetChoiceBox;
    @FXML private Label StatusLabel;
    @FXML private Label SerialSetsLabel;
    @FXML private Label DataOnTargetLabel;
    @FXML private Label errorGetDataLabel;
    @FXML private Button GetDataButton;
    @FXML private Button StopTaskButton;
    @FXML private Button RunTaskButton;
    @FXML private Label GetDataLabel;
    @FXML private Label progressPercentLabel;
    @FXML private ProgressBar taskProgressBar;//////////////////////////////////////////
    @FXML private Label taskMessageLabel;
    @FXML private Button clearLoadedButton;
    @FXML private TextArea logTaskTextArea;
    @FXML private Label summaryTitleLabel;
    @FXML private Label FailedTaskLabel;
    @FXML private Label successTaskLabel;
    @FXML private Label skippedTaskLabel;
    @FXML private Label successWithWarnTaskLabel;
    @FXML private TreeView treeViewBottomUp;
    @FXML private TreeView treeViewTopDown;
    @FXML private ChoiceBox<Integer> changeNumberOfThreadsChoiceBox;
    @FXML private Button changeThreadNumberButton;

    private AppController appController;
    private CompileTaskPrefController compileTaskPrefController;
    private SimulationTaskPrefController simulationTaskPrefController;
    private DetailsForTask newDetail;
    private SimpleStringProperty getDataTitle;
    private SimpleStringProperty status;
    private SimpleStringProperty serialSets;
    private SimpleStringProperty dataOnTarget;
    private SimpleStringProperty errorGetData;
    private SimpleStringProperty errorTypeTaskOrNoTargetsOrNoThreads;
    private SimpleStringProperty errorPref;
    private SimpleStringProperty summaryTitle;
    private SimpleStringProperty failedTask;
    private SimpleStringProperty successTask;
    private SimpleStringProperty skippedTask;
    private SimpleStringProperty successWithWarnTask;
    private SimpleBooleanProperty isFinished;
    private SimpleStringProperty logTask;

    public MainTaskViewController() {
        errorTypeTaskOrNoTargetsOrNoThreads = new SimpleStringProperty("");
        errorPref = new SimpleStringProperty("");
        status = new SimpleStringProperty("");
        serialSets = new SimpleStringProperty("");
        dataOnTarget = new SimpleStringProperty("");
        errorGetData = new SimpleStringProperty("");
        getDataTitle = new SimpleStringProperty("");
        summaryTitle = new SimpleStringProperty("");
        failedTask = new SimpleStringProperty("");
        successTask = new SimpleStringProperty("");
        skippedTask = new SimpleStringProperty("");
        successWithWarnTask = new SimpleStringProperty("");
        isFinished= new SimpleBooleanProperty(false);
        logTask= new SimpleStringProperty("");
    }

    @FXML public void initialize() throws IOException {
        if(WhatIfUpperTaskGridPaneController!=null ){
            WhatIfUpperTaskGridPaneController.setParent(this);
        }
        if(runTaskTableViewGridPaneController!=null){
            runTaskTableViewGridPaneController.setParent(this);
        }

        StringExpression sb = Bindings.concat("", errorTypeTaskOrNoTargetsOrNoThreads);
        errorTypeTaskOrNoTargetsOrNoThreadsLabel.textProperty().bind(sb);
        sb = Bindings.concat("", errorPref);
        ErrorPrefLabel.textProperty().bind(sb);
        sb = Bindings.concat("", getDataTitle);
        GetDataLabel.textProperty().bind(sb);
        sb = Bindings.concat("", status);
        StatusLabel.textProperty().bind(sb);
        sb = Bindings.concat("", serialSets);
        SerialSetsLabel.textProperty().bind(sb);
        sb = Bindings.concat("", dataOnTarget);
        DataOnTargetLabel.textProperty().bind(sb);
        sb = Bindings.concat("", errorGetData);
        errorGetDataLabel.textProperty().bind(sb);

        sb = Bindings.concat("", summaryTitle);
        summaryTitleLabel.textProperty().bind(sb);
        sb = Bindings.concat("", failedTask);
        FailedTaskLabel.textProperty().bind(sb);
        sb = Bindings.concat("", successTask);
        successTaskLabel.textProperty().bind(sb);
        sb = Bindings.concat("", skippedTask);
        skippedTaskLabel.textProperty().bind(sb);
        sb = Bindings.concat("", successWithWarnTask);
        successWithWarnTaskLabel.textProperty().bind(sb);

        //sb = Bindings.concat("", logTask);
        //logTaskTextArea.textProperty().bind(sb);

        FXMLLoader loader = new FXMLLoader();
        URL simulationFXML = getClass().getResource("/appController/taskView/subcomponents/taskPreference/simulationTaskPref.fxml");
        loader.setLocation(simulationFXML);
        loader.load();
        simulationTaskPrefController = loader.getController();
        loader = new FXMLLoader();
        URL compileFXML = getClass().getResource("/appController/taskView/subcomponents/taskPreference/compileTaskPref.fxml");
        loader.setLocation(compileFXML);
        loader.load();
        compileTaskPrefController = loader.getController();
        liveDataOnTargetChoiceBox.setDisable(true);
        LoadButton.setDisable(true);
        StopTaskButton.setDisable(true);
        RunTaskButton.setDisable(true);
        GetDataButton.setDisable(true);
        threadsNumberChoiceBox.setDisable(true);
        HowToStartChoicePart.setDisable(true);
        changeNumberOfThreadsChoiceBox.setDisable(true);
        changeThreadNumberButton.setDisable(true);
        logTaskTextArea.setWrapText(true);
        logTaskTextArea.setEditable(false);


    }

    @FXML  public void selectItemBottomUp(){//TODO
        TreeItem<String> target = (TreeItem<String>) treeViewBottomUp.getSelectionModel().getSelectedItem();

    }

    @FXML public void selectItemTopDown(){

    }

    @FXML private void handleProceedButtonPressed() {
        if(!someTargetIsChosen()){
            errorTypeTaskOrNoTargetsOrNoThreads.setValue("Please choose targets from the table above to process.");
        }
        else if(typeOfTaskChoiceBox.getValue()== null || typeOfTaskChoiceBox.getValue().equals("--Task Type--")){
            errorTypeTaskOrNoTargetsOrNoThreads.setValue("Please choose type of task to continue.");
        }
        else{
            errorTypeTaskOrNoTargetsOrNoThreads.setValue("");
            LoadButton.setDisable(false);
            if(typeOfTaskChoiceBox.getValue().equals("Simulation")) {
                PrefTaskBorderPane.setCenter(simulationTaskPrefController.getRoot());
            }else if(typeOfTaskChoiceBox.getValue().equals("Compilation")){
                PrefTaskBorderPane.setCenter(compileTaskPrefController.getRoot());
            }
            threadsNumberChoiceBox.setDisable(false);
            HowToStartChoicePart.setDisable(false);
            typeOfTaskChoiceBox.setDisable(true);
        }
    }

    @FXML private void handleRunTask(){
        GetDataButton.setDisable(false);
        liveDataOnTargetChoiceBox.setDisable(false);
        errorPref.set("");
        initGetLiveData();
        blockNonTaskComp();
        runTask(newDetail);
        LoadButton.setDisable(true);
        clearLoadedButton.setDisable(true);
        resetLogs();
        RunTaskButton.setDisable(true);
        StopTaskButton.setDisable(false);
        StopTaskButton.setText("Pause");
        if(typeOfTaskChoiceBox.getValue()!=null&& typeOfTaskChoiceBox.getValue().equals("Compilation")){
            compileTaskPrefController.deactivateMe();
        }

    }

    @FXML private void handleGetDataButton(){
        status.setValue("");
        serialSets.setValue("");
        dataOnTarget.setValue("");
        if(liveDataOnTargetChoiceBox.getValue()== null || liveDataOnTargetChoiceBox.getValue().equals("--Target Name--")){
            errorGetData.setValue("Please choose a target to continue.");
        }else {
            errorGetData.setValue("");
            WrapsTarget chosenTarget = appController.getWrapsTargetByName(liveDataOnTargetChoiceBox.getValue());
            status.setValue("Target status: "+chosenTarget.getTargetStatusPropertyValue());
            serialSets.setValue("Serial sets with target: "+ chosenTarget.getListOfNamesSets());
            switch(chosenTarget.getTargetStatusPropertyValue()){
                case "Frozen":
                {
                    dataOnTarget.setValue("Frozen because "+chosenTarget.getTarget().FrozenBecauseThisTargets(chosenTargetNames()));
                    break;
                }
                case "Skipped":
                {
                    dataOnTarget.setValue("Skipped because "+chosenTarget.getTarget().skippedBecauseThisTargets(chosenTargetNames()));
                    break;
                }
                case "Waiting":
                {
                    dataOnTarget.setValue("Waiting for "+chosenTarget.getTarget().howMSWaiting()+" ms");
                    break;
                }
                case "In Process":
                {
                    dataOnTarget.setValue("In Process for "+chosenTarget.getTarget().howMSinProcess()+" ms");
                    break;
                }
                case "Finished":
                {
                    dataOnTarget.setValue("Finished with the status: "+chosenTarget.getFinishStatusPropertyValue());
                    break;
                }

            }
            //dataOnTarget
        }
    }

    @FXML private void initLoadedData(){
        resetLoadedTask();
        boolean isIncremental =false;
        int numOfThreads;
        boolean isSimulation;

        if(!someTargetIsChosen()) {
            errorPref.setValue("Please choose targets from the table above to process.");
        }else if(threadsNumberChoiceBox.getValue() == null || threadsNumberChoiceBox.getValue().equals(0)){
            errorPref.setValue("Please choose number of threads to continue.");
        }else if(HowToStartChoicePart.getValue() == null||HowToStartChoicePart.getValue().equals("--Way To Run--")) {
            errorPref.setValue("Please choose the way to start the task.");
        }else{
            if (HowToStartChoicePart.getValue().equals("Incremental") && chosenTargetsCanRunIncremental()) {
                isIncremental = true;
            }
            if(typeOfTaskChoiceBox.getValue().equals("Simulation")) {
                isSimulation=true;
                boolean isRandom = simulationTaskPrefController.getIsRandom();
                String processTime = simulationTaskPrefController.getProcessTime();
                String probSuccess =simulationTaskPrefController.getProbSuccessTextFiled();
                String probSuccessWWarnings= simulationTaskPrefController.getProbSuccessWWarningsTextFiled();
                Double probSuccessWWarningsDouble;
                Integer processTimeInteger;
                Double probSuccessDouble;
            try {
                processTimeInteger = Integer.parseInt(processTime);
                } catch (NumberFormatException e) {
                    errorPref.setValue("Please enter an integer for process time.");
                    return;
                }
                try {
                    probSuccessDouble = Double.parseDouble(probSuccess);
                    if (probSuccessDouble > 1.0 || probSuccessDouble < 0.0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    errorPref.setValue("Please enter a number between 0 to 1 for probability for success.");
                    return;
                }
                try {
                    probSuccessWWarningsDouble = Double.parseDouble(probSuccessWWarnings);
                    if (probSuccessWWarningsDouble > 1.0 || probSuccessWWarningsDouble < 0.0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    errorPref.setValue("Please enter a number between 0 to 1 for probability for success with warnings.");
                    return;
                }
                errorPref.setValue("");

                numOfThreads = threadsNumberChoiceBox.getValue();
                newDetail = new DetailsForTask(processTimeInteger, probSuccessDouble, probSuccessWWarningsDouble, isRandom, isIncremental,isSimulation,numOfThreads);
                RunTaskButton.setDisable(false);
                runTaskTableViewGridPaneController.initRunTable();
                blockNonTaskComp();
                clearLoadedButton.setText("Back & Clear");

            }else if(typeOfTaskChoiceBox.getValue().equals("Compilation")) {
                isSimulation=false;
                PrefTaskBorderPane.setCenter(compileTaskPrefController.getRoot());
                File directoryToCompile = compileTaskPrefController.getDirectoryToCompile();
                File directoryForCompiled = compileTaskPrefController.getDirectoryForCompiled();
                if (!someTargetIsChosen()) {
                    errorPref.setValue("Please choose targets from the table above to process.");
                } else if (directoryToCompile == null) {
                    errorPref.setValue("Directory to compile codes from not available.");
                } else if (directoryForCompiled == null) {
                    errorPref.setValue("Directory for compiled codes not available.");
                } else {
                    errorPref.setValue("");
                    StopTaskButton.setDisable(false);
                    numOfThreads = threadsNumberChoiceBox.getValue();
                    newDetail = new DetailsForTask(directoryToCompile, directoryForCompiled, isIncremental, isSimulation, numOfThreads);
                    RunTaskButton.setDisable(false);
                    runTaskTableViewGridPaneController.initRunTable();
                    blockNonTaskComp();
                    clearLoadedButton.setText("Back & Clear");
                    compileTaskPrefController.deactivateMe();

                }
            }
                }if (HowToStartChoicePart.getValue().equals("Incremental") && !chosenTargetsCanRunIncremental()) {//TODO
                    //errorPref.setValue("Chosen targets not all been processed - task will start from scratch.");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setContentText("Chosen targets not all been processed.\nTask will start from scratch.");
                    alert.show();
                }

        }

    @FXML public void handlePause(){
        if(StopTaskButton.getText().equals("Pause")){
            StopTaskButton.setText("Resume");
            changeThreadNumberButton.setDisable(false);
            changeNumberOfThreadsChoiceBox.setDisable(false);
            appController.handlePause(true);
        }else if(StopTaskButton.getText().equals("Resume")){
            StopTaskButton.setText("Pause");
            changeThreadNumberButton.setDisable(true);
            changeNumberOfThreadsChoiceBox.setDisable(true);
            appController.handlePause(false);
        }

    }

    @FXML  private void handleClearLoadedButton(){
        if(clearLoadedButton.getText().equals("Back & Clear")) {
            clearLoadedButton.setText("Clear");
            WhatIfUpperTaskGridPaneController.activateMe();
            for (WrapsTarget wrapsTarget : getWrapsTargetList()) {
                wrapsTarget.getCheckbox().setDisable(false);
            }
            taskProgressBar.setProgress(0);

            summaryTitle.setValue("");
            failedTask.setValue("");
            successTask.setValue("");
            skippedTask.setValue("");
            successWithWarnTask.setValue("");
            taskMessageLabel.textProperty().setValue("");
            progressPercentLabel.textProperty().setValue("0%");
            RunTaskButton.setDisable(true);
            resetLoadedTask();
            resetChoices();

        }
        if(typeOfTaskChoiceBox.getValue()!=null&& typeOfTaskChoiceBox.getValue().equals("Compilation")){
            compileTaskPrefController.activateMe();
        }

        LoadButton.setDisable(true);
        resetChoices();
        // resetTaskData();
        ProceedButton.setDisable(false);
        typeOfTaskChoiceBox.setDisable(false);
        HowToStartChoicePart.setDisable(false);
        threadsNumberChoiceBox.setDisable(false);
        //errorPref.setValue("");
        errorTypeTaskOrNoTargetsOrNoThreads.setValue("");

    }

    @FXML private void handleApplyThreads(){
        if(changeNumberOfThreadsChoiceBox.getValue() != null && !changeNumberOfThreadsChoiceBox.getValue().equals(0)){
            appController.setNumOfThreads(changeNumberOfThreadsChoiceBox.getValue());
        }
    }

    public GridPane getRoot() { return mainGridPane; }

    public void initMainTask(){
        activeMe();
        initWhatIfUpperTaskGridPane();
        initPreTaskTable();
        initTaskPref();

    }

    private void activeMe() {
        WhatIfUpperTaskGridPaneController.activateMe();
        typeOfTaskChoiceBox.setDisable(false);
        HowToStartChoicePart.setDisable(false);
        threadsNumberChoiceBox.setDisable(false);
        ProceedButton.setDisable(false);
        LoadButton.setDisable(true);

    }

    private void initTaskPref() {
        ObservableList<String> wayToRunOptions  = FXCollections.observableArrayList("From Scratch","Incremental");
        HowToStartChoicePart.setItems(wayToRunOptions);
        HowToStartChoicePart.setValue("--Way To Run--");

        ObservableList<String> taskOptions  = FXCollections.observableArrayList("Simulation","Compilation");
        ObservableList<Integer> threadNumOptions;
        List<Integer> threadNumList=new ArrayList<Integer>();
        int numOfThreads = appController.getNumOfThreads();
        for(int i = 1;i<numOfThreads;i++){
            threadNumList.add(i);
        }
        threadNumOptions = FXCollections.observableArrayList(threadNumList);
        threadsNumberChoiceBox.setItems(threadNumOptions);
        threadsNumberChoiceBox.setValue(0);
        changeNumberOfThreadsChoiceBox.setItems(threadNumOptions);
        changeNumberOfThreadsChoiceBox.setValue(0);
        typeOfTaskChoiceBox.setItems(taskOptions);
        typeOfTaskChoiceBox.setValue("--Task Type--");
    }

    private void initPreTaskTable() {
        final ObservableList<WrapsTarget> data = FXCollections.observableArrayList(appController.getWrapsTargetList());

        NameColumnPre.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );
        CheckBoxColumnPre.setCellValueFactory(
                new PropertyValueFactory<>("checkbox")
        );
        LocationColumnPre.setCellValueFactory(
                new PropertyValueFactory<>("location")
        );
        DataColumnPre.setCellValueFactory(
                new PropertyValueFactory<>("data")
        );
        FinishColumnPre.setCellValueFactory(
                new PropertyValueFactory<>("finishStatusPropertyValue")
        );
        StatusColumnPre.setCellValueFactory(
                new PropertyValueFactory<>("targetStatusPropertyValue")
        );
        PreTaskTableView.setItems(data);
    }

    public void initTreeView() {
        initTreeViewTopDown();
        initTreeViewBottomUp();
    }

    private void initTreeViewTopDown(){
        TreeItem<String> rootItem = new TreeItem<>("Top-Down Tree View");
        for(Target target:appController.getTargetList()) {
            if(target.getLocation().equals(Target.Location.ROOT) ||target.getLocation().equals(Target.Location.INDEPENDENT)) {
                TreeItem<String> newRoot = new TreeItem<>(target.getName()+ " - " +target.getTargetStatusValue());
                rootItem.getChildren().add(initTreeViewTopDownRec(newRoot));
            }
        }
        treeViewTopDown.setRoot(rootItem);
    }

    public void initTreeViewBottomUp() {
        TreeItem<String> rootItem = new TreeItem<>("Bottom-Up Tree View");
        for(Target target:appController.getTargetList()) {
            if(target.getLocation().equals(Target.Location.LEAF)) {
                TreeItem<String> newRoot = new TreeItem<>(target.getName()+ " - " +target.getTargetStatusValue());
                rootItem.getChildren().add(initTreeViewBottomUpRec(newRoot));
            }
        }
        treeViewBottomUp.setRoot(rootItem);
    }

    private void initGetLiveData(){//TODO reset values
        liveDataOnTargetChoiceBox.setDisable(false);
        GetDataButton.setDisable(false);
        // liveDataOnTargetChoiceBox.setItems(null);
        liveDataOnTargetChoiceBox.setValue("--Target Name--");
        ObservableList<String> TargetsOptions  = FXCollections.observableArrayList(chosenTargetNames());
        liveDataOnTargetChoiceBox.setItems(TargetsOptions);
        getDataTitle.setValue("Get Live data on target:");

    }

    private TreeItem<String> initTreeViewTopDownRec(TreeItem<String> rootItem){
        Target curRoot = null;
        for(Target target:appController.getTargetList()){
            if(rootItem.getValue().equals(target.getName()+ " - "+target.getTargetStatusValue())){
                curRoot = target;
                if(curRoot.getDependingOn().size() == 0)
                {
                    return new TreeItem<>(curRoot.getName() + " - " + curRoot.getTargetStatusValue());
                }else {
                    for (Target curTarget : curRoot.getDependingOn()) {
                        TreeItem<String> curTargetItem = new TreeItem<>(curTarget.getName() + " - " + curTarget.getTargetStatusValue());
                        rootItem.getChildren().add(initTreeViewTopDownRec(curTargetItem));
                    }
                }
                break;
            }
        }
        return rootItem;

    }

    private TreeItem<String> initTreeViewBottomUpRec(TreeItem<String> rootItem) {
        Target curRoot = null;
        for(Target target:appController.getTargetList()){
            if(rootItem.getValue().equals(target.getName()+ " - "+target.getTargetStatusValue())){
                curRoot = target;
                if(curRoot.getRequiredTo().size() == 0)
                {
                    return new TreeItem<>(curRoot.getName() + " - " + curRoot.getTargetStatusValue());
                }else {
                    for (Target curTarget : curRoot.getRequiredTo()) {
                        TreeItem<String> curTargetItem = new TreeItem<>(curTarget.getName() + " - " + curTarget.getTargetStatusValue());
                        rootItem.getChildren().add(initTreeViewBottomUpRec(curTargetItem));
                    }
                }
                break;
            }
        }
        return rootItem;
    }

    public void initWhatIfUpperTaskGridPane(){WhatIfUpperTaskGridPaneController.initMe();}

    public void setAppController(AppController appController) {
        this.appController=appController;

    }

    public void resetTreeView(){
        treeViewBottomUp.setRoot(null);
        treeViewTopDown.setRoot(null);
    }

    private void resetChoices() {
        if(typeOfTaskChoiceBox.getValue()!=null&& typeOfTaskChoiceBox.getValue().equals("Simulation")) {
            simulationTaskPrefController.resetMe();
        }else if(typeOfTaskChoiceBox.getValue()!=null&& typeOfTaskChoiceBox.getValue().equals("Compilation")){
            compileTaskPrefController.resetMe();
        }

        PrefTaskBorderPane.setCenter(null);
    }

    private void resetLoadedTask(){
        runTaskTableViewGridPaneController.resetLoadedTask();

    }

    public void resetLogs(){
        logTaskTextArea.clear();
    }

    private void resetPreTaskTable() {
        PreTaskTableView.setItems(null);
    }

    public void resetTaskData() {
        ObservableList<Integer> dataTreads= FXCollections.observableArrayList(0);
        ObservableList<String> dataWay= FXCollections.observableArrayList("--Way To Run--");
        ObservableList<String> dataTaskKind= FXCollections.observableArrayList("--Task Type--");
        runTaskTableViewGridPaneController.resetMe();
        WhatIfUpperTaskGridPaneController.resetMe();
        PreTaskTableView.setItems(null);
        resetChoices();
        typeOfTaskChoiceBox.setItems(dataTaskKind);
        HowToStartChoicePart.setItems(dataWay);
        threadsNumberChoiceBox.setItems(dataTreads);
        resetLogs();
        resetLoadedTask();
        resetTreeView();
        changeNumberOfThreadsChoiceBox.setDisable(true);
        changeThreadNumberButton.setDisable(true);
        taskProgressBar.setProgress(0);
        summaryTitle.setValue("");
        failedTask.setValue("");
        successTask.setValue("");
        skippedTask.setValue("");
        successWithWarnTask.setValue("");
        taskMessageLabel.textProperty().setValue("");
        progressPercentLabel.textProperty().setValue("0%");
        LoadButton.setDisable(true);


    }

    public void refreshMyTable() {
        resetPreTaskTable();
        initPreTaskTable();
    }

    public void blockNonTaskComp(){
        ProceedButton.setDisable(true);
        //LoadButton.setDisable(true);
        WhatIfUpperTaskGridPaneController.disableMe();
        for(WrapsTarget wrapsTarget:getWrapsTargetList()){
            wrapsTarget.getCheckbox().setDisable(true);
        }
        threadsNumberChoiceBox.setDisable(true);
        typeOfTaskChoiceBox.setDisable(true);
        HowToStartChoicePart.setDisable(true);

    }

    public void animationsOff() {
        runTaskTableViewGridPaneController.animationsOff();
    }

    public void animationsOn(){
        runTaskTableViewGridPaneController.animationsOn();
    }

    public List<String> getAllOfIndirect(String targetName,boolean isRequired) {
        return (appController.getAllOfIndirect(targetName,isRequired));
    }

    public List<String> getTargetNamesList() {
        return appController.getTargetNamesList();
    }

    public List<WrapsTarget> getWrapsTargetList() {
        return appController.getWrapsTargetList();
    }

    private boolean chosenTargetsCanRunIncremental() {//TODO
        List<WrapsTarget> allList = appController.getWrapsTargetList();
        List<WrapsTarget> curList = new ArrayList<WrapsTarget>();

        for (WrapsTarget wrapsTarget : allList) {
            if (wrapsTarget.getCheckbox().isSelected()) {
                curList.add(wrapsTarget);
            }
        }
        for(WrapsTarget wrapsTarget :curList){
            if(wrapsTarget.getTarget().getTargetStatus().equals(Target.TargetStatus.FROZEN)){
                return false;
            }
        }
        return true;
    }

    private void runTask(DetailsForTask newDetail) {

        appController.runTask(newDetail,
                successTask::set,
                successWithWarnTask::set,
                failedTask::set,
                skippedTask::set,
                summaryTitle::set,
                logTask::set,
                ()->{
                    isFinished.set(true);
                });
        logTask.addListener((observable, oldValue, newValue) -> {
            logTaskTextArea.appendText(newValue+"\n");
        });

    }

    public void bindTaskToMainTaskViewControllerComponents(Task<Boolean> myRun, Runnable onFinish) {
        //taskMassage
        taskMessageLabel.textProperty().bind(myRun.messageProperty());

        // task progress bar
        taskProgressBar.progressProperty().bind(myRun.progressProperty());


        // task percent label
        progressPercentLabel.textProperty().bind(
                Bindings.concat(
                        Bindings.format(
                                "%.0f",
                                Bindings.multiply(
                                        myRun.progressProperty(),
                                        100)),
                        " %"));

        // task cleanup upon finish
        //myRun.valueProperty().addListener((observable, oldValue, newValue) -> {
        //  onTaskFinished(Optional.ofNullable());
        //});

        myRun.valueProperty().addListener((observable,oldValue,newValue)->{
            onTaskFinished(Optional.ofNullable(onFinish));
        });
    }

    private <T> void onTaskFinished(Optional<T> onFinish) {
        GetDataButton.setDisable(true);
        liveDataOnTargetChoiceBox.setDisable(true);
        this.taskMessageLabel.textProperty().unbind();
        this.progressPercentLabel.textProperty().unbind();
        this.taskProgressBar.progressProperty().unbind();
        LoadButton.setDisable(true);
        clearLoadedButton.setDisable(false);
        treeViewTopDown.setRoot(null);
        treeViewBottomUp.setRoot(null);
        initTreeView();
        PreTaskTableView.refresh();
        RunTaskButton.setDisable(true);
        StopTaskButton.setDisable(true);
        StopTaskButton.setText("Pause");
        changeThreadNumberButton.setDisable(true);
        changeNumberOfThreadsChoiceBox.setDisable(true);
        if(typeOfTaskChoiceBox.getValue()!=null&& typeOfTaskChoiceBox.getValue().equals("Compilation")){
            compileTaskPrefController.activateMe();
        }

    }

    private List<String> chosenTargetNames(){
        List<WrapsTarget> curList = appController.getWrapsTargetList();
        List<String> res= new ArrayList<String>();
        for(WrapsTarget wrapsTarget:curList){
            if(wrapsTarget.getCheckbox().isSelected()){
                res.add(wrapsTarget.getName());
            }
        }
        return res;
    }

    private boolean someTargetIsChosen(){
        List<WrapsTarget> curList = appController.getWrapsTargetList();
        for (WrapsTarget wrapsTarget : curList) {
            if (wrapsTarget.getCheckbox().isSelected()) {
                return true;
            }
        }
        return false;
    }

    public List<Target> getCurTargetList() {
        List<WrapsTarget> curList = appController.getWrapsTargetList();
        List<Target> targetList = new ArrayList<Target>();
        for(WrapsTarget wrapsTarget : curList){
            if(wrapsTarget.getCheckbox().isSelected()){
                targetList.add(wrapsTarget.getTarget());
            }
        }
        return targetList;
    }

}