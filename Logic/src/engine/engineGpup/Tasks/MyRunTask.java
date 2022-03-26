package engine.engineGpup.Tasks;
import engine.dto.DTOTaskOnTarget;
import engine.engineGpup.DetailsForTask;
import engine.graph.SerialSet;
import engine.graph.Target;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import task.CompilationTask;
import task.SimulationTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;


public class MyRunTask extends Task<Boolean>{
    private final DetailsForTask details;
    private final Consumer<String> successSummary;
    private final Consumer<String> successWWSummary;
    private final Consumer<String> failureSummary;
    private final Consumer<String> skippedSummary;
    private final Consumer<String> summary;
    private Consumer<String> logData;
    private String folder;
    private List<Target> chosenTargets;
    private final ThreadPoolExecutor threadExecutor;


    public MyRunTask(DetailsForTask details,
                     List<Target> chosenTargets,
                     ThreadPoolExecutor threadExecutor,
                     Consumer<String> successSummary,
                     Consumer<String> successWWSummary,
                     Consumer<String> failureSummary,
                     Consumer<String> skippedSummary,
                     Consumer<String> summary,
                     String folder,
                     Consumer<String> logData) {
        this.details=details;
        this.chosenTargets=chosenTargets;
        this.threadExecutor=threadExecutor;
        this.successSummary=successSummary;
        this.successWWSummary=successWWSummary;
        this.failureSummary=failureSummary;
        this.skippedSummary=skippedSummary;
        this.summary=summary;
        this.folder=folder;
        this.logData=logData;

    }

    @Override
    protected Boolean call() throws Exception {

        updateMessage("Start running");

        //update progress bar
        updateProgress(0,chosenTargets.size());

        List<Target> realRun=chosenTargets;
        Set<Target> noRunYet=new HashSet<Target>();
        Set<Target>runningSet=new HashSet<Target>();
        for(Target curr:realRun){
            noRunYet.add(curr);

            //update massage
            updateMessage(curr.getName()+ " is "+curr.getTargetStatusValue());
        }
        int numOfEnd=0;
        while (numOfEnd!=realRun.size()){
            Set<Target>temp=new HashSet<Target>();
            for(Target tempTarget:noRunYet){
                temp.add(tempTarget);
            }
            for(Target curr:temp){
                switch(curr.getTargetStatus()) {
                    case FROZEN:{
                        boolean nextIteration=false;
                        for(Target iNeedHim:curr.getDependingOn()){
                            if(realRun.contains(iNeedHim)){
                                if(!((iNeedHim.getFinishStatus().equals(Target.Finish.SUCCESS_WITH_WARNINGS))||(iNeedHim.getFinishStatus().equals(Target.Finish.SUCCESS)))){
                                    nextIteration=true;
                                }
                            }
                        }
                        if(nextIteration){
                            break;
                        }
                        if(!curr.getTargetStatus().equals(Target.TargetStatus.WAITING)){
                            curr.setTargetStatus(Target.TargetStatus.WAITING);
                            //update massage
                            updateMessage(curr.getName()+ " is "+curr.getTargetStatusValue());
                        }
                    }
                    case WAITING: {
                        if(!runningSet.contains(curr)) {
                            List<SerialSet> currListOfSets = curr.getListOfSets();
                            boolean breaking = false;
                            boolean canRun = true;
                            for (SerialSet currSet : currListOfSets) {
                                for (Target currTargetInCurrSet : currSet.getTargetSetOfSerialSet()) {
                                    if (runningSet.contains(currTargetInCurrSet) && !curr.getName().equals(currTargetInCurrSet.getName())) {
                                        canRun = false;
                                        break;
                                    }

                                }
                            }
                            if (canRun) {
                                //maybe dont need to this if
                                if(!curr.getTargetStatus().equals(Target.TargetStatus.WAITING)){
                                    curr.setTargetStatus(Target.TargetStatus.WAITING);
                                    //update massage
                                    updateMessage(curr.getName()+ " is "+curr.getTargetStatusValue());
                                }

                                runningSet.add(curr);
                                task.Task task;
                                if (details.isSimulation()) {
                                    task = new SimulationTask(details,this);
                                } else {
                                    task = new CompilationTask(details, this);
                                    //task = new CompilationTask();
                                }
                                SimpleStringProperty message=new SimpleStringProperty("");

                                message.addListener((observable, oldValue, newValue) ->
                                        updateMessage(newValue)
                                );

                                task.setTarget(curr,message);

                                threadExecutor.execute(task);

                            }
                        }
                    }
                    break;
                    case SKIPPED:{
                        numOfEnd++;
                        //update progress bar
                        updateProgress(numOfEnd,chosenTargets.size());
                        noRunYet.remove(curr);
                    }
                    break;
                    case INPROCESS: {
                        break;
                    }
                    case FINISHED:{
                        //if(curr.getFinishStatus().equals(Target.Finish.FAILURE)){
                        //    List<Target> skippedList=new ArrayList<Target>();
                        //    makeSkipped(curr,skippedList,realRun);
                        //}
                        numOfEnd++;

                        //update progress bar
                        updateProgress(numOfEnd,chosenTargets.size());

                        noRunYet.remove(curr);
                        runningSet.remove(curr);
                    }
                    break;
                    default:
                        // code block
                }
            }

        }
        updateMessage("Run finished");

        int numOfSkipped=0, numOfSuccesses=0, numOfSuccessesWW=0,numOfFailure=0;
        for(Target target:realRun){
            if(target.getTargetStatus().equals(Target.TargetStatus.SKIPPED)){
                numOfSkipped++;
            }
            else if(target.getFinishStatus().equals(Target.Finish.SUCCESS)){
                numOfSuccesses++;
            }
            else if(target.getFinishStatus().equals(Target.Finish.SUCCESS_WITH_WARNINGS)){
                numOfSuccessesWW++;
            }
            else if(target.getFinishStatus().equals(Target.Finish.FAILURE)){
                numOfFailure++;
            }
        }


        int finalNumOfSuccesses = numOfSuccesses;
        Platform.runLater(
                () -> successSummary.accept("Targets succeeded: "+ finalNumOfSuccesses)
        );
        int finalNumOfSuccessesWW = numOfSuccessesWW;
        Platform.runLater(
                () -> successWWSummary.accept("Targets succeeded with warnings: "+ finalNumOfSuccessesWW)
        );
        int finalNumOfFailure = numOfFailure;
        Platform.runLater(
                () -> failureSummary.accept("Targets failure: "+ finalNumOfFailure)
        );
        int finalNumOfSkipped = numOfSkipped;
        Platform.runLater(
                () -> skippedSummary.accept("Targets skipped: "+ finalNumOfSkipped)
        );
        Platform.runLater(
                () -> summary.accept("SUMMARY:")
        );
        return true;

    }

    public synchronized void makeSkipped(Target currentTarget,List<Target> skippedList) {
        if(chosenTargets.contains(currentTarget)){
            if(currentTarget.getLocation().equals(Target.Location.ROOT)){
                return;
            }
            else{
                for(Target neighbor:currentTarget.getRequiredTo()) {
                    if(chosenTargets.contains(neighbor)){

                        if(!neighbor.getTargetStatus().equals(Target.TargetStatus.SKIPPED)){
                            neighbor.setTargetStatus(Target.TargetStatus.SKIPPED);
                            updateMessage(neighbor.getName()+ " is "+neighbor.getTargetStatusValue());
                        }

                        if (!skippedList.contains(neighbor)) {
                            skippedList.add(neighbor);
                            makeSkipped(neighbor, skippedList);
                        }
                    }
                }
            }
        }
    }


    public synchronized List<String> allOpenedTargets(Target target) {
        List<String> openList=new ArrayList<String>();
        for(Target curr: target.getRequiredTo()){
            if(chosenTargets.contains(curr)){
                boolean Open=true;

                for(Target currFather: curr.getDependingOn()){
                    if(chosenTargets.contains(currFather)) {
                        if(!(currFather.getTargetStatus().equals(Target.TargetStatus.FINISHED)||currFather.getTargetStatus().equals(Target.TargetStatus.SKIPPED))){
                            Open = false;
                        }
                    }
                }
                if(Open){
                    openList.add(curr.getName());
                }
            }

        }

        return openList;
    }

    public String getFolder(){return folder;}


/*
    private void createFileForTask(String typeOfTask) {
        Date date=new Date();

        //SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/YYYY hh:mm:ss");


        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss");
        folder=pathForTaskFolder+"\\"+ typeOfTask+" - "+sdf.format(date);
        File file=fileWithDirectoryAssurance(pathForTaskFolder,"\\"+ typeOfTask+" - "+sdf.format(date));
        File temp=new File(pathForTaskFolder);
        if(!temp.exists()){
            throw new RuntimeException("There no path like: "+ pathForTaskFolder);
        }
        if(!file.mkdir()){
            throw new RuntimeException("Can not open folder.");
        }
    }
*/

    private static File fileWithDirectoryAssurance(String directory, String filename) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        return new File(directory + "/" + filename);
    }

    public synchronized void insertDtoToConsumer(String curData) {
        Platform.runLater(
                () -> logData.accept(curData)
        );
    }
}
