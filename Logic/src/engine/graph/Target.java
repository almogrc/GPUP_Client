package engine.graph;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.util.*;

public class Target implements Serializable {


    public void setTargetStatusPropertyValue(String status) {
        targetStatusProperty.setValue(status);
    }

    public enum Location{LEAF,MIDDLE,ROOT,INDEPENDENT}
    public enum Finish{SUCCESS,FAILURE, SUCCESS_WITH_WARNINGS,NOTFINISHED}
    public enum TargetStatus{FROZEN ,SKIPPED ,WAITING ,INPROCESS ,FINISHED}

    private String name;
    private String data;
    private Date startTimeOfWiting;
    private Date startTimeOfInProcess;

    private TargetStatus targetStatus;
    private SimpleStringProperty targetStatusProperty;
    private Finish finishStatus;
    private SimpleStringProperty finishStatusProperty;

    private Location location;
    private List<Target>  dependsOn=new ArrayList<Target>();
    private List<Target> requiredFor=new ArrayList<Target>();
    private List<SerialSet> listOfSets;

    public Target(String name, String data, Location type){
        this.name=name;
        this.data=data;
        location = type;
        listOfSets=null;
        targetStatusProperty=new SimpleStringProperty("Frozen");
        finishStatusProperty=new SimpleStringProperty("Not Finished");
        setFinishStatus(Finish.NOTFINISHED);
        setTargetStatus(TargetStatus.FROZEN);

    }

    public void addSerialSets(List<SerialSet> listOfSets) {
        this.listOfSets=listOfSets;
    }

    public String getName(){ return name;  }

    public List<Target>getDependingOn(){
        return dependsOn;
    }

    public List<Target>getRequiredTo(){
        return requiredFor;
    }

    public TargetStatus getTargetStatus(){
        return targetStatus;
    }

    public Finish getFinishStatus(){
        return finishStatus;
    }


    public boolean addDependingOn(Target newTarget){
        if(newTarget!=null){
            dependsOn.add(newTarget);
            return true;
        }else {
            ///trow try add null target
            return false;
        }
    }

    public boolean addRequiredFor(Target newTarget){
        if(newTarget!=null){
            requiredFor.add(newTarget);
            return true;
        }else {
            ///trow try add null target
            return false;
        }
    }

    public Location getLocation(){return location;}

    public String getData() {return data;}

    public List<String> getRequiredToTargetsName() {
        List<String>res=new ArrayList<String>();
        for(Target target : requiredFor){
            res.add(target.getName());
        }
        return res;
    }

    public List<String> getDependingOnTargetsName() {
        List<String>res=new ArrayList<String>();
        for(Target target : dependsOn){
            res.add(target.getName());
        }
        return res;
    }



    @Override
    public String toString(){
        return name;
    }

    public void setFinishStatus(Finish outcome) {
        finishStatus=outcome;
        switch(outcome) {
            case SUCCESS:
                finishStatusProperty.set("Success");
                break;
            case FAILURE:
                finishStatusProperty.set("Failure");
                break;
            case SUCCESS_WITH_WARNINGS:
                finishStatusProperty.set("Success With Warnings");
                break;
            case NOTFINISHED:
                finishStatusProperty.set("Not Finished");
                break;
            default:
                // code block
        }
    }

    public SimpleStringProperty finishStatusProperty() {
        return finishStatusProperty;
    }

    public void setTargetStatus(TargetStatus status) {
        targetStatus=status;

        switch(status) {
            case FROZEN:
                targetStatusProperty.set("Frozen");
                break;
            case SKIPPED:
                targetStatusProperty.set("Skipped");
                break;
            case WAITING:{
                targetStatusProperty.set("Waiting");
                if(startTimeOfWiting==null){
                    startTimeOfWiting=new Date();
                }
            }
                break;
            case INPROCESS:
                targetStatusProperty.set("In Process");
                if(startTimeOfInProcess==null){
                    startTimeOfInProcess=new Date();
                }
                break;
            case FINISHED:
                targetStatusProperty.set("Finished");
                break;
            default:
                // code block
        }
    }

    public SimpleStringProperty targetStatusProperty() {
        return targetStatusProperty;
    }

    public int getNumOfDirectDependsOn(){return dependsOn.size();}

    public int getNumOfDirectRequiredFor(){return requiredFor.size();}

    public int getNumOfIndirectDependsOn(){
        return getAllOfIndirectDependsOn().size();
    }

    public Set<Target> getAllOfIndirectDependsOn(){
        Set<Target> canGetThem=new HashSet<Target>();
        Set<Target> temp=new HashSet<Target>();

        canGetThem.add(this);
        int size;

        do{
            size=canGetThem.size();
            for(Target curr: canGetThem){
                temp.addAll(curr.dependsOn);
            }
            canGetThem.addAll(temp);
            temp.clear();
        }while (size!=canGetThem.size());
        canGetThem.remove(this);
        return canGetThem;
    }

    public int getNumOfIndirectRequiredFor(){
        return getAllOfIndirectRequiredFor().size();
    }

    public Set<Target> getAllOfIndirectRequiredFor(){
        Set<Target> canGetThem=new HashSet<Target>();
        Set<Target> temp=new HashSet<Target>();

        canGetThem.add(this);
        int size;

        do{
            size=canGetThem.size();
            for(Target curr: canGetThem){
                temp.addAll(curr.requiredFor);
            }
            canGetThem.addAll(temp);
            temp.clear();
        }while (size!=canGetThem.size());
        canGetThem.remove(this);
        return canGetThem;
    }

    public int getSerialSetsNumber(){return listOfSets.size();}

    public List<String> getListOfNamesSets(){
        List<String> res = new ArrayList<String>();
        for(SerialSet serialSet:listOfSets) {
            res.add(serialSet.getName());
        }
        return res;
    }
    public String getFinishStatusValue() {
        return finishStatusProperty.get();
    }

    public String getTargetStatusValue() {
        return targetStatusProperty.get();
    }

    public List<SerialSet> getListOfSets(){
        return listOfSets;
    }




    public String howMSinProcess(){
        Date d=new Date();
        return String.valueOf((d.getTime()-startTimeOfInProcess.getTime()));
    }


    public String howMSWaiting(){
        Date d=new Date();
        return String.valueOf((d.getTime()-startTimeOfWiting.getTime()));
    }




    public String FrozenBecauseThisTargets(List <String> chosenTargets){
        List<String>res=new ArrayList<String>();
        for(Target iNeedHim:dependsOn){
            if(chosenTargets.contains( iNeedHim.getName())) {
                if (iNeedHim.getTargetStatus().equals(TargetStatus.WAITING) || iNeedHim.getTargetStatus().equals(TargetStatus.FROZEN) || iNeedHim.getTargetStatus().equals(TargetStatus.INPROCESS)) {
                    res.add(iNeedHim.getName());
                }
            }
        }
        return res.toString();
    }


    public String skippedBecauseThisTargets(List <String> chosenTargets){
        List<String>res=new ArrayList<String>();
        for(Target iNeedHim:dependsOn){
            if(chosenTargets.contains( iNeedHim.getName())) {
                if (iNeedHim.getTargetStatus().equals(TargetStatus.SKIPPED) || iNeedHim.getFinishStatus().equals(Finish.FAILURE)) {
                    res.add(iNeedHim.getName());
                }
            }
        }
        return res.toString();
    }
}
