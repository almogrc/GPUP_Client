package task;

import engine.engineGpup.DetailsForTask;
import engine.engineGpup.Tasks.MyRunTask;
import engine.graph.Target;
import engine.dto.DTOTaskOnTarget;
import javafx.beans.property.SimpleStringProperty;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationTask implements Task, Serializable ,Runnable{
    private final MyRunTask myRunTask;
    private int processingTime;
    private double probabilityForSuccess;
    private double probabilityForSuccessWithWarnings;
    private boolean isRandom;
    private Target target;
    private DTOTaskOnTarget dtoTaskOnTarget;
    private SimpleStringProperty message;

    public SimulationTask(DetailsForTask runDetails, MyRunTask myRunTask) {
        this.processingTime=runDetails.getProcessingTime();
        this.probabilityForSuccess = runDetails.getProbabilityForSuccess();
        this.probabilityForSuccessWithWarnings = runDetails.getProbabilityForSuccessWithWarnings();
        this.isRandom = runDetails.isRandom();
        this.myRunTask=myRunTask;

    }

    @Override
    public void setTarget(Target target, SimpleStringProperty message) {
        this.target=target;
        this.message=message;
    }

    public DTOTaskOnTarget getDTOTaskOnTarget(){
        return dtoTaskOnTarget;
    }

    @Override
    public void run() {
        dtoTaskOnTarget = new DTOTaskOnTarget(target.getName(),target.getData(),target.getTargetStatus());

        if(target.getTargetStatus().equals(Target.TargetStatus.WAITING)){
            target.setTargetStatus(Target.TargetStatus.INPROCESS);
            message.set(target.getName()+ " is "+target.getTargetStatusValue());

            LocalDateTime startingTime = LocalDateTime.now();
            dtoTaskOnTarget.setStartingTime(startingTime);


            int realProcessTime=handleRandomProcessTime();
            myRunTask.insertDtoToConsumer(target.getName()+ " going to sleep for "+realProcessTime+" ms");
            myRunTask.insertDtoToConsumer(target.getName()+ " started sleeping");
            try {
                Thread.sleep(realProcessTime);
            } catch (InterruptedException e) {
            }
            myRunTask.insertDtoToConsumer(target.getName()+ " woke up");

            dtoTaskOnTarget.setProcessingTime(realProcessTime);

            Random r = new Random();
            double outcome = r.nextDouble();

            if(outcome<=probabilityForSuccess){
                double isSuccessWithWarnings = r.nextDouble();
                if(isSuccessWithWarnings<=probabilityForSuccessWithWarnings){
                    target.setFinishStatus(Target.Finish.SUCCESS_WITH_WARNINGS);
                }else{
                    target.setFinishStatus(Target.Finish.SUCCESS);
                }
            }else{
                target.setFinishStatus(Target.Finish.FAILURE);

                List<Target> skippedList=new ArrayList<Target>();
                myRunTask.makeSkipped(target,skippedList);

                dtoTaskOnTarget.setTargetsSkipped(skippedList);

            }

            target.setTargetStatus(Target.TargetStatus.FINISHED);
            dtoTaskOnTarget.addOpenedTarget(myRunTask.allOpenedTargets(target));

            message.set(target.getName()+ " is "+target.getTargetStatusValue()+" "+ target.getFinishStatusValue());

        }
        dtoTaskOnTarget.setFinishStatus(target.getFinishStatus());
        LocalDateTime endingTime = LocalDateTime.now();
        dtoTaskOnTarget.setEndingTime(endingTime);
        dtoTaskOnTarget.setStatus(target.getTargetStatus());
        dtoTaskOnTarget.setFinishStatus(target.getFinishStatus());


        try {
            writeToFile(dtoTaskOnTarget);
        } catch (IOException e) {}

    }

    private int handleRandomProcessTime() {
        int realProcessTime;
        if(isRandom){
            if(processingTime!=0){
                Random r = new Random();
                realProcessTime = r.nextInt(processingTime-0) + 0;
            }else{
                realProcessTime=0;
            }
        }else{
            realProcessTime=processingTime;
        }
        return realProcessTime;
    }

    private void writeToFile(DTOTaskOnTarget newDto) throws IOException {
        try (Writer out1 = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(myRunTask.getFolder()+"//"+newDto.getName()+".log"), "UTF-8"))) {
            out1.write(newDto.toString());
        }
        catch (Exception e){
            throw e;
        }
    }

}






















 /* public void setProcessingTime(int processingTime){this.processingTime=processingTime;}
    public void setProbabilityForSuccess(double probabilityForSuccess){this.probabilityForSuccess=probabilityForSuccess;}
    public void setProbabilityForSuccessWithWarnings(double probabilityForSuccessWithWarnings){this.probabilityForSuccessWithWarnings=probabilityForSuccessWithWarnings;}*/
