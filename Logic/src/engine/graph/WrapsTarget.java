package engine.graph;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WrapsTarget {

    private CheckBox checkbox=new CheckBox();
    private Target target;

    public WrapsTarget(Target target) {
        this.target=target;
    }

    public String getName(){return target.getName();}

    public String getData(){return target.getData();}

    public SimpleStringProperty getFinishStatusProperty(){return target.finishStatusProperty();}

    public String getFinishStatusPropertyValue(){return target.finishStatusProperty().get();}//todo GETVALUE

    public SimpleStringProperty getTargetStatusProperty(){return target.targetStatusProperty();}

    public List<String> getListOfNamesSets(){return target.getListOfNamesSets();}

    public String getTargetStatusPropertyValue(){return target.targetStatusProperty().get();}

    public int getNumOfDirectRequiredFor(){return target.getNumOfDirectRequiredFor();}

    public int getNumOfIndirectRequiredFor(){return target.getNumOfIndirectRequiredFor();}

    public int getNumOfDirectDependsOn(){return target.getNumOfDirectDependsOn();}

    public int getNumOfIndirectDependsOn(){return target.getNumOfIndirectDependsOn();}

    public int getSerialSetsNumber(){return target.getSerialSetsNumber();}

    public CheckBox getCheckbox(){return checkbox;}

    public Target.Location getLocation(){
        return target.getLocation();
    }

    public void setCheckbox(Boolean flag){
        this.checkbox.setSelected(flag);
    }

    public Target getTarget() {return this.target; }

    public void setStatus(String status) {target.setTargetStatusPropertyValue(status);}//TODO delete

}
