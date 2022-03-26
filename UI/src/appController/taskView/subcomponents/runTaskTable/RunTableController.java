package appController.taskView.subcomponents.runTaskTable;

import appController.taskView.mainTaskView.MainTaskViewController;
import appController.taskView.subcomponents.runTaskTable.myRectangle.MyRectangle;
import engine.graph.Target;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RunTableController {

    @FXML private GridPane runViewGridPane;
    @FXML private TableView<MyRectangle> frozenTV;
    @FXML private TableColumn<MyRectangle, StackPane> frozenCol;
    @FXML private TableView<MyRectangle> waitingTV;
    @FXML private TableColumn<MyRectangle, StackPane> waitingCol;
    @FXML private TableView<MyRectangle> inProcessTV;
    @FXML private TableColumn<MyRectangle, StackPane> inProcessCol;
    @FXML private TableView<MyRectangle> finishedTV;
    @FXML private TableColumn<MyRectangle, StackPane> finishedCol;
    @FXML private TableView<MyRectangle> skippedTV;
    @FXML private TableColumn<MyRectangle, StackPane> skippedCol;


    private MainTaskViewController mainTaskViewController;
    private List<MyRectangle> frozenList = new ArrayList<>();
    private List<MyRectangle> waitingList = new ArrayList<>();
    private List<MyRectangle> inProcessList = new ArrayList<>();
    private List<MyRectangle> finishedList = new ArrayList<>();
    private List<MyRectangle> skippedList = new ArrayList<>();
    private ObservableList<MyRectangle> myRectangleList;
    private List<MyRectangle> temp;
    private int millisForAnimations =1;


    @FXML
    public void initialize(){

    }

    public RunTableController(){

    }

    public void initRunTable(){
        temp= new ArrayList<MyRectangle>();
        for(Target curr: mainTaskViewController.getCurTargetList()){
            temp.add(new MyRectangle(curr));
        }
        for(Target curr: mainTaskViewController.getCurTargetList()){
            switch(curr.getTargetStatus()) {
                case FROZEN: {
                    frozenList.add(new MyRectangle(curr));
                break;
                }
                case SKIPPED:
                {
                    skippedList.add(new MyRectangle(curr));
                    break;
                }
                case WAITING:
                {
                    waitingList.add(new MyRectangle(curr));
                    break;
                }
                case INPROCESS:
                {
                   inProcessList.add(new MyRectangle(curr));
                    break;
                }
                case FINISHED:
                {
                    finishedList.add(new MyRectangle(curr));
                break;
                }

                default:
                    // code block
            }

        }

        myRectangleList = FXCollections.observableArrayList(temp);
        FilteredList<MyRectangle> filteredFrozen = new FilteredList<MyRectangle>(FXCollections.observableArrayList(frozenList) ,t-> t.setAnimation(millisForAnimations));
        FilteredList<MyRectangle> filteredSkipped = new FilteredList<MyRectangle>(FXCollections.observableArrayList(skippedList) ,t-> t.setAnimation(millisForAnimations));
        FilteredList<MyRectangle> filteredWaiting = new FilteredList<MyRectangle>(FXCollections.observableArrayList(waitingList) ,t-> t.setAnimation(millisForAnimations));
        FilteredList<MyRectangle> filteredInProcess = new FilteredList<MyRectangle>(FXCollections.observableArrayList(inProcessList) ,t-> t.setAnimation(millisForAnimations));
        FilteredList<MyRectangle> filteredFinished = new FilteredList<MyRectangle>(FXCollections.observableArrayList(finishedList) ,t-> t.setAnimation(millisForAnimations));

        initFrozenRunTable(filteredFrozen);
        initSkippedRunTable(filteredSkipped);
        initWaitingRunTable(filteredWaiting);
        initInProcessRunTable(filteredInProcess);
        initFinishedRunTable(filteredFinished);



        for(MyRectangle currRectangle:temp){
            currRectangle.getStatusProperty().addListener((observable, oldValue, newValue) -> {
                synchronized (this) {
                    switch (oldValue) {
                        case "Frozen": {
                            boolean found = false;
                            frozenTV.setItems(FXCollections.observableArrayList(frozenList));
                            Iterator<MyRectangle> iter = frozenList.iterator();
                            while (iter.hasNext() && !found) {
                                MyRectangle test = (MyRectangle) iter.next();
                                if (!test.getTarget().getTargetStatus().equals(Target.TargetStatus.FROZEN)) {
                                    iter.remove();
                                    found = true;
                                }
                                if (found) {
                                    frozenTV.setItems(FXCollections.observableArrayList(frozenList));
                                    if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.WAITING)) {
                                        waitingList.add(test);
                                        waitingTV.setItems(FXCollections.observableArrayList(waitingList));
                                    } else if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.SKIPPED)) {
                                        skippedList.add(test);
                                        skippedTV.setItems(FXCollections.observableArrayList(skippedList));
                                    }
                                }
                                test.setAnimation(millisForAnimations);
                            }

                        }
                        break;
                        case "Skipped": {
                            skippedTV.setItems(FXCollections.observableArrayList(skippedList));
                            boolean found = false;
                            Iterator<MyRectangle> iter = skippedList.iterator();
                            while (iter.hasNext() && !found) {
                                MyRectangle test = (MyRectangle) iter.next();
                                if (!test.getTarget().getTargetStatus().equals(Target.TargetStatus.SKIPPED)) {
                                    iter.remove();
                                    skippedTV.setItems(FXCollections.observableArrayList(skippedList));
                                    if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.FROZEN)) {
                                        frozenList.add(test);
                                        frozenTV.setItems(FXCollections.observableArrayList(frozenList));
                                    }else if(test.getTarget().getTargetStatus().equals(Target.TargetStatus.WAITING)){
                                        waitingList.add(test);
                                        waitingTV.setItems(FXCollections.observableArrayList(waitingList));
                                    }else if(test.getTarget().getTargetStatus().equals(Target.TargetStatus.INPROCESS)){
                                        inProcessList.add(test);
                                        inProcessTV.setItems(FXCollections.observableArrayList(inProcessList));
                                    }
                                    found = true;
                                }
                                    test.setAnimation(millisForAnimations);
                            }
                        }
                        break;
                        case "Waiting": {

                            boolean found = false;
                            waitingTV.setItems(FXCollections.observableArrayList(waitingList));
                            MyRectangle test;
                            Iterator<MyRectangle> iter = waitingList.iterator();
                            while (iter.hasNext() && !found) {
                                test = (MyRectangle) iter.next();
                                if (!test.getTarget().getTargetStatus().equals(Target.TargetStatus.WAITING)) {
                                    iter.remove();
                                    waitingTV.setItems(FXCollections.observableArrayList(waitingList));
                                    if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.INPROCESS)) {
                                        inProcessList.add(test);
                                        inProcessTV.setItems(FXCollections.observableArrayList(inProcessList));
                                    } else if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.SKIPPED)) {
                                        skippedList.add(test);
                                        skippedTV.setItems(FXCollections.observableArrayList(skippedList));
                                    }
                                    found = true;
                                }
                                test.setAnimation(millisForAnimations);
                            }

                            break;
                        }
                        case "In Process": {
                            boolean found = false;
                            inProcessTV.setItems(FXCollections.observableArrayList(inProcessList));
                            Iterator<MyRectangle> iter = inProcessList.iterator();
                            while (iter.hasNext() && !found) {
                                MyRectangle test = (MyRectangle) iter.next();
                                if (!test.getTarget().getTargetStatus().equals(Target.TargetStatus.INPROCESS)) {
                                    iter.remove();
                                    found = true;
                                }
                                if (found) {
                                    inProcessTV.setItems(FXCollections.observableArrayList(inProcessList));
                                    if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.FINISHED)) {
                                        finishedList.add(test);
                                        finishedTV.setItems(FXCollections.observableArrayList(finishedList));
                                        //finishedTV.refresh();
                                    } else if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.SKIPPED)) {
                                        skippedList.add(test);
                                        skippedTV.setItems(FXCollections.observableArrayList(skippedList));
                                    }
                                }
                                test.setAnimation(millisForAnimations);
                            }

                            break;
                        }
                        case "Finished": {
                            boolean found = false;
                            finishedTV.setItems(FXCollections.observableArrayList(finishedList));
                            Iterator<MyRectangle> iter = finishedList.iterator();
                            while (iter.hasNext() && !found) {
                                MyRectangle test = (MyRectangle) iter.next();
                                if (!test.getTarget().getTargetStatus().equals(Target.TargetStatus.FINISHED)) {
                                    iter.remove();
                                    found = true;
                                }
                                if (found) {
                                    finishedTV.setItems(FXCollections.observableArrayList(finishedList));

                                    if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.SKIPPED)) {
                                        skippedList.add(test);
                                        skippedTV.setItems(FXCollections.observableArrayList(skippedList));
                                    }else if(test.getTarget().getTargetStatus().equals(Target.TargetStatus.FROZEN)){
                                        frozenList.add(test);
                                        frozenTV.setItems(FXCollections.observableArrayList(frozenList));
                                    }else if(test.getTarget().getTargetStatus().equals(Target.TargetStatus.WAITING)){
                                        waitingList.add(test);
                                        waitingTV.setItems(FXCollections.observableArrayList(waitingList));
                                    }else if(test.getTarget().getTargetStatus().equals(Target.TargetStatus.INPROCESS)){
                                        inProcessList.add(test);
                                        inProcessTV.setItems(FXCollections.observableArrayList(inProcessList));
                                    }
                                }
                                test.setAnimation(millisForAnimations);
                            }
                            break;
                        }

                        default: {
                            break;
                        }
                    }
                    if(frozenList.isEmpty() && inProcessList.isEmpty() && waitingList.isEmpty()){
                        frozenTV.refresh();
                        skippedTV.refresh();
                        finishedTV.refresh();
                        inProcessTV.refresh();
                        waitingTV.refresh();
                    }

                }
            });
        }
    }

    public void resetMe(){//TODO maybe clear lists
        frozenTV.refresh();
        skippedTV.refresh();
        finishedTV.refresh();
        inProcessTV.refresh();
        waitingTV.refresh();
        resetLoadedTask();
    }

    private void initFrozenRunTable(FilteredList<MyRectangle> filteredFrozen){
        frozenCol.setCellValueFactory(
                new PropertyValueFactory<>("myRectangle")
        );
        frozenTV.setItems(filteredFrozen);
    }

    private void initSkippedRunTable(FilteredList<MyRectangle> filteredSkipped){
        skippedCol.setCellValueFactory(
                new PropertyValueFactory<>("myRectangle")
        );
        skippedTV.setItems(filteredSkipped);

    }

    private void initWaitingRunTable(FilteredList<MyRectangle> filteredWaiting){
        waitingCol.setCellValueFactory(
                new PropertyValueFactory<>("myRectangle")
        );
        waitingTV.setItems(filteredWaiting);
    }

    private void initInProcessRunTable(FilteredList<MyRectangle> filteredInProcess){
        inProcessCol.setCellValueFactory(
                new PropertyValueFactory<>("myRectangle")
        );
        inProcessTV.setItems(filteredInProcess);
    }

    private void initFinishedRunTable(FilteredList<MyRectangle> filteredFinished){
        finishedCol.setCellValueFactory(
                new PropertyValueFactory<>("myRectangle")
        );
        finishedTV.setItems(filteredFinished);
    }


    public void setParent(MainTaskViewController mainTaskViewController) {
        this.mainTaskViewController=mainTaskViewController;
    }

    public void resetLoadedTask() {
        frozenList.clear();
        frozenTV.setItems(FXCollections.observableArrayList(frozenList));
        skippedList.clear();
        skippedTV.setItems(FXCollections.observableArrayList(frozenList));
        finishedList.clear();
        finishedTV.setItems(FXCollections.observableArrayList(frozenList));
        inProcessList.clear();
        inProcessTV.setItems(FXCollections.observableArrayList(frozenList));
        waitingList.clear();
        waitingTV.setItems(FXCollections.observableArrayList(frozenList));
    }

    public void animationsOff() {
        millisForAnimations = 1;
    }

    public void animationsOn() {
        millisForAnimations = 2000;
    }
}
