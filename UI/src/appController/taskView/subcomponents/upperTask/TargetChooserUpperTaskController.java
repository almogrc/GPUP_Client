package appController.taskView.subcomponents.upperTask;


import appController.taskView.mainTaskView.MainTaskViewController;
import engine.graph.WrapperTarget;
import engine.graph.WrapsTarget;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.util.List;

public class TargetChooserUpperTaskController {

    @FXML private ChoiceBox<String> WhatIfTargetChoice;
    @FXML private ChoiceBox<String> WhatIfRatioChoice;
    @FXML private Button addWhatIfButton;
    @FXML private Button addAllButton;
    @FXML private Button clearAllButton;
    @FXML private Label errorWhatIfLabel;

    private MainTaskViewController mainTaskViewController;
    private SimpleStringProperty errorWhatIf;
    @FXML
    private void initialize(){
        StringExpression sb = Bindings.concat("", errorWhatIf);
        errorWhatIfLabel.textProperty().bind(sb);
    }

    public TargetChooserUpperTaskController(){
        errorWhatIf = new SimpleStringProperty("");
    }
    public void initMe(){
        ObservableList<String> TargetOptions = FXCollections.observableArrayList(mainTaskViewController.getTargetNamesList());
        ObservableList<String> RatioOptions  = FXCollections.observableArrayList("Required For","Depends On");
        WhatIfTargetChoice.setItems(TargetOptions);
        WhatIfRatioChoice.setItems(RatioOptions);
    }

    public void resetMe(){
        ObservableList<String> dataTarget= FXCollections.observableArrayList("--Target Name--");
        ObservableList<String> dataRatio = FXCollections.observableArrayList("--Ratio Choice--");
        errorWhatIf.setValue("");
        WhatIfTargetChoice.setItems(dataTarget);
        WhatIfRatioChoice.setItems(dataRatio);
    }
    @FXML
    void addWhatIfButtonOnAction(ActionEvent event) {
        if( WhatIfTargetChoice.getValue()== null || WhatIfTargetChoice.getValue().equals("--Target Name--")){
            errorWhatIf.setValue("Please choose a target to continue.");
        }else if( WhatIfRatioChoice.getValue()== null || WhatIfRatioChoice.getValue().equals("--Ratio Choice--")){
            errorWhatIf.setValue("Please choose a ratio to continue.");
        }
        else{
            errorWhatIf.setValue("");
            boolean required=false;
            if(WhatIfRatioChoice.getValue().equals("Required For")) {
                required = true;
            }

            List<String> whatIfListString =mainTaskViewController.getAllOfIndirect(WhatIfTargetChoice.getValue(),required);
            if(!whatIfListString.contains(WhatIfTargetChoice.getValue())){
                whatIfListString.add(WhatIfTargetChoice.getValue());
            }
            List<WrapsTarget> wrapsTargetList = mainTaskViewController.getWrapsTargetList();
            for(WrapsTarget curr:wrapsTargetList){
                if(whatIfListString.contains(curr.getName())){
                    curr.setCheckbox(true);
                }
            }

        }
    }
    @FXML
    void clearAllButtonOnAction(ActionEvent event) {
        errorWhatIf.setValue("");
        List<WrapsTarget> wrapsTargetList = mainTaskViewController.getWrapsTargetList();
        for(WrapsTarget curr :wrapsTargetList){
            curr.setCheckbox(false);
        }
    }
    @FXML
    void addAllButtonOnAction(ActionEvent event) {
        errorWhatIf.setValue("");
        List<WrapsTarget> wrapsTargetList = mainTaskViewController.getWrapsTargetList();
        for(WrapsTarget curr :wrapsTargetList){
            curr.setCheckbox(true);
        }
    }

    public void setParent(MainTaskViewController mainTaskViewController) {
        this.mainTaskViewController = mainTaskViewController;
    }

    public void disableMe() {
        addAllButton.setDisable(true);
        addWhatIfButton.setDisable(true);
        clearAllButton.setDisable(true);
        WhatIfTargetChoice.setDisable(true);
        WhatIfRatioChoice.setDisable(true);
    }
    public void activateMe() {
        addAllButton.setDisable(false);
        addWhatIfButton.setDisable(false);
        clearAllButton.setDisable(false);
        WhatIfTargetChoice.setDisable(false);
        WhatIfRatioChoice.setDisable(false);
    }

}