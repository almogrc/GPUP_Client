package task;

import engine.dto.DTOTaskOnTarget;
import engine.engineGpup.DetailsForTask;
import engine.engineGpup.Tasks.MyRunTask;
import engine.graph.Target;
import javafx.beans.property.SimpleStringProperty;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CompilationTask implements  Task ,Runnable{
private File directoryToCompile;
private File directoryForCompiled;
private Target target;
private SimpleStringProperty message;
private MyRunTask myRunTask;
private DTOTaskOnTarget dtoTaskOnTarget;

    public CompilationTask(DetailsForTask runDetails, MyRunTask myRunTask) {
          this.directoryForCompiled=runDetails.getDirectoryForCompiled();
          this.directoryToCompile=runDetails.geDirectoryToCompile();
          this.myRunTask=myRunTask;
        }

    @Override
    public void setTarget(Target target, SimpleStringProperty message) {
        this.target=target;
        this.message=message;
    }

    @Override
    public DTOTaskOnTarget getDTOTaskOnTarget(){
        return dtoTaskOnTarget;
    }

    @Override
    public void run() {
        dtoTaskOnTarget = new DTOTaskOnTarget(target.getName(), target.getData(), target.getTargetStatus());

        if (target.getTargetStatus().equals(Target.TargetStatus.WAITING)) {
            target.setTargetStatus(Target.TargetStatus.INPROCESS);
            message.set(target.getName() + " is " + target.getTargetStatusValue());

            myRunTask.insertDtoToConsumer(target.getName() + " started compilation process");
            String[] command = {"javac", "-d", directoryForCompiled.getAbsolutePath(), "-cp", directoryToCompile.getAbsolutePath(), directoryToCompile.getAbsolutePath() + "/" + target.getData().replace(".", "/").concat(".java")};
            myRunTask.insertDtoToConsumer("Going to compile: javac" + "-d" + (directoryToCompile.getAbsolutePath()) + "-cp" + (directoryForCompiled.getAbsolutePath()) + (directoryToCompile.getAbsolutePath()) + "/" + target.getData().replace(".", "/").concat(".java"));
            message.set(target.getName() + " starting process.");
            try {
                LocalDateTime startingTime = LocalDateTime.now();
                dtoTaskOnTarget.setStartingTime(startingTime);
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                LocalDateTime endingTime = LocalDateTime.now();
                dtoTaskOnTarget.setEndingTime(endingTime);
                myRunTask.insertDtoToConsumer("Compiler processed file for " + Duration.between(startingTime, endingTime).toMillis() + " ms.");
                if ((process.getErrorStream().read() != -1) || (process.exitValue() != 0)) {
                    target.setFinishStatus(Target.Finish.FAILURE);
                    myRunTask.insertDtoToConsumer(target.getName() + " failed to compile.");
                    List<Target> skippedList = new ArrayList<Target>();
                    myRunTask.makeSkipped(target, skippedList);
                    myRunTask.insertDtoToConsumer("Errors: " + process.getErrorStream());
                    dtoTaskOnTarget.setTargetsSkipped(skippedList);
                } else {
                    target.setFinishStatus(Target.Finish.SUCCESS);
                    myRunTask.insertDtoToConsumer(target.getName() + " succeeded to compile.");
                }
                target.setTargetStatus(Target.TargetStatus.FINISHED);
                dtoTaskOnTarget.addOpenedTarget(myRunTask.allOpenedTargets(target));
                dtoTaskOnTarget.setFinishStatus(target.getFinishStatus());
                dtoTaskOnTarget.setStatus(target.getTargetStatus());
                dtoTaskOnTarget.setFinishStatus(target.getFinishStatus());
                message.set(target.getName() + " finished with " + target.getFinishStatusValue());
                writeToFile(dtoTaskOnTarget);
            } catch (IOException | InterruptedException ioException) {}

        }
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
