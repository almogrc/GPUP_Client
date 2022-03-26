package engine.engineGpup;
import appController.mainAppController.AppController;
import engine.engineGpup.Tasks.MyRunTask;
import engine.graph.*;
import engine.dto.*;
import graphViz.GraphViz;
import task.SimulationTask;
import task.Task;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

@XmlRootElement
public class GpupExecution implements Execution {
    final int NO_NEED_TO_RUN_ON = -1;

    private Task task;
    private TargetGraph targetGraph;
    private boolean wasRunBefore = false;
    private String pathForTaskFolder;
    private String folder;
    private AppController appController;
    private List<WrapsTarget> wrapsTargetList;
    private boolean isRunTaskFromScratch;
    private DetailsForTask runDetails;
    //private  ExecutorService threadExecutor;

    private int numOfThreads;
    private ThreadPoolExecutor executor;


    private Thread gpupRunner;
    private javafx.concurrent.Task<Boolean> myRun;

    private BlockingQueue<Runnable> runQ;
    private Consumer<String> successSummary;
    private Consumer<String> successWWSummary;
    private Consumer<String> failureSummary;
    private Consumer<String> skippedSummary;
    private Consumer<String> summary;
    private Consumer<String> logData;
    private Runnable onFinish;

    /*
    public GpupExecution(){
        threadExecutor.shutdown();
    }
*/

    public GpupExecution(AppController appController) {
        //this.fileName = new SimpleStringProperty();
        task = null;
        this.appController =appController;
    }

    public void initGpupExecution(File file) throws JAXBException, FileNotFoundException {
        GpupGraphChecker graphChecker = new GpupGraphChecker(file);
        pathForTaskFolder = graphChecker.getPathForTaskFolder();
        task=null;
        targetGraph=new TargetGraph(graphChecker.getGraph(),file);
        wrapsTargetList= new ArrayList<WrapsTarget>();
        for(Target curr: targetGraph.getTargetList()){
            wrapsTargetList.add(new WrapsTarget(curr));
        }


    }

    public List<String> getListOfNamesSetsForTargetName(String targetName){
        for(WrapsTarget wrapsTarget: wrapsTargetList){
            if(wrapsTarget.getName().equals(targetName)){
                return wrapsTarget.getListOfNamesSets();
            }
        }
        return null;
    }

    public WrapsTarget getWrapsTargetByName(String name){
        for(WrapsTarget wrapsTarget: wrapsTargetList){
            if(wrapsTarget.getName().equals(name)){
                return wrapsTarget;
            }
        }
        return null;
    }

    private void setThreadPool(int numOfThreads) {
        //threadExecutor = Executors.newFixedThreadPool(numOfThreads);
        this.numOfThreads=numOfThreads;
        this.executor = new ThreadPoolExecutor(numOfThreads,numOfThreads,2, TimeUnit.HOURS, new LinkedBlockingQueue<Runnable>());

    }



    /*private Dto runTaskIncremental() throws InterruptedException, IOException {
        List<Target> realTargets=getChosenTargets();
        //the for update the indegree of the vertex
        List<Integer> indegree = new ArrayList<Integer>(targetGraph.getNumOfTargets());
        for (int i = 0; i < targetGraph.getNumOfTargets(); i++) {
            indegree.add(0);
            for(Target currTarget:targetGraph.getTarget(i).getDependingOn()){
                if((currTarget.getFinishStatus().equals(Target.Finish.FAILURE)||currTarget.getFinishStatus().equals(Target.Finish.NOTFINISHED))&&realTargets.contains(currTarget)){
                    indegree.set(i, indegree.get(i)+1);
                }
            }
        }

        for (int i = 0; i < targetGraph.getNumOfTargets(); i++){
            Target currTarget=targetGraph.getTarget(i);
            if(realTargets.contains(currTarget)) {
                //if target success/with warnings we don't want to run on him
                if (currTarget.getFinishStatus().equals(Target.Finish.SUCCESS) || currTarget.getFinishStatus().equals(Target.Finish.SUCCESS_WITH_WARNINGS)) {
                    indegree.set(i, NO_NEED_TO_RUN_ON);
                } else {//target was failure or no_finished so we want initialize him to frozen and NOTFINISHED
                    targetGraph.getTarget(i).setTargetStatus(Target.TargetStatus.FROZEN);
                    targetGraph.getTarget(i).setFinishStatus(Target.Finish.NOTFINISHED);
                }
            }else{
                indegree.set(i, NO_NEED_TO_RUN_ON);
            }

        }



        return runTask(indegree);
    }*/

    /*private Dto runTaskFromScratch() throws InterruptedException, IOException {
        List<Target> realTargets=getChosenTargets();
        //the for update the indegree of the vertex
        List<Integer> indegree = new ArrayList<Integer>(targetGraph.getNumOfTargets());
        Target currTarget;
        int realDependOn;
        for (int i = 0; i < targetGraph.getNumOfTargets(); i++) {
            realDependOn=0;
            currTarget=targetGraph.getTarget(i);
            if(realTargets.contains(currTarget)){
                for(Target depTarget:currTarget.getDependingOn()){
                    if(realTargets.contains(depTarget)){
                        realDependOn++;
                    }
                }
                indegree.add(realDependOn);
            }else{
                indegree.add(NO_NEED_TO_RUN_ON);
            }
        }
        return runTask(indegree);
    }*/

   // private Dto runTask(List<Integer> indegree) throws InterruptedException, IOException {
   //     Date startDate = new Date();
//
   //     List<DTOTaskOnTarget> dtoArr=new ArrayList<DTOTaskOnTarget>();
   //     Queue<Target> queue = new ArrayDeque<Target>();
//
   //     //this for insert to the queue all the vertex that there inDegree is 0
   //     for (int i = 0; i < targetGraph.getNumOfTargets(); i++) {
   //         if (indegree.get(i) == 0) {
   //             targetGraph.getTarget(i).setTargetStatus(Target.TargetStatus.WAITING);
   //             queue.add(targetGraph.getTarget(i));
   //         }
   //     }
//
   //     ExecutorService threadExecutor = Executors.newFixedThreadPool(3);
//
   //     while (!queue.isEmpty()) {
   //         Target currentTarget = queue.poll();
//
//
   //         threadExecutor.execute(task);
//
   //         //change *all* the dependent on currentTarget to skipped
   //         if (currentTarget.getFinishStatus().equals(Target.Finish.FAILURE)) {
   //             List<Target> skippedList=new ArrayList<Target>();
   //             makeSkipped(currentTarget,skippedList);
   //             newDto.setTargetsSkipped(skippedList);
   //         }
//
   //         //foreach neighbor of currentTarget
   //         for (Target neighbor : currentTarget.getRequiredTo()) {
   //             //indegree[i]=indegree[i]-1
   //             int indexOfNeighbor = targetGraph.getIndex(neighbor);
   //             indegree.set(indexOfNeighbor, indegree.get(indexOfNeighbor) - 1);
//
   //             //if indegree's neighbor is 0 push to queue
   //             //neighbor can't finish, because only a target that has been run on them can have a finish, and it is not possible to run on a target unless its indegree is 0, and only now the target 'neighbor' get to 0
   //             if (indegree.get(indexOfNeighbor) == 0) {
   //                 //if neighbor status is frozen it means it is possible to run him so his status changed to frozen
   //                 if (neighbor.getTargetStatus().equals(Target.TargetStatus.FROZEN)) {
   //                     neighbor.setTargetStatus(Target.TargetStatus.WAITING);
   //                 }
   //                 queue.add(neighbor);
   //                 newDto.addOpenedTarget(neighbor);
   //             }
   //         }
   //         dtoArr.add(newDto);
   //         writeToFile(newDto);
   //     }
//
   //     for (int i =0 ; i <indegree.size() ;i++) {
   //         if(indegree.get(i)>0){
   //             //there is a circle
   //             dtoArr.add(new DTOTaskOnTarget(targetGraph.getTarget(i).getName(),targetGraph.getTarget(i).getData(),targetGraph.getTarget(i).getTargetStatus() ));
   //         }
   //     }
   //     Date endDate = new Date();
   //     //
   //     return new DtoTaskOnTargets(LocalTime.ofSecondOfDay((endDate.getTime() - startDate.getTime())/ 1000),dtoArr);
   // }

    private void writeToFile(DTOTaskOnTarget newDto) throws IOException {
        try (Writer out1 = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(folder+"//"+newDto.getName()+".log"), "UTF-8"))) {
            out1.write(newDto.toString());
        }
        catch (Exception e){
             throw e;
        }
    }


    ////maybe not needed
    private void makeSkipped(Target currentTarget,List<Target> skippedList,List<Target> realRun) {
        if(realRun.contains(currentTarget)){
            if(currentTarget.getLocation().equals(Target.Location.ROOT)){
                return;
            }
            else{
                for(Target neighbor:currentTarget.getRequiredTo()) {
                    if(realRun.contains(neighbor)){
                        neighbor.setTargetStatus(Target.TargetStatus.SKIPPED);
                        if (!skippedList.contains(neighbor)) {
                            skippedList.add(neighbor);
                            makeSkipped(neighbor, skippedList, realRun);
                        }
                    }
                }
            }
        }
    }



    public Dto getCircleOfTarget(String targetName){
        Target target= targetGraph.getTarget(targetName);

        List<String> res=new ArrayList<String>();
        boolean cont=true;
        for(Target neighbor:target.getRequiredTo()){
            List<ArrayList<String>> allPathsFromNeighbor=targetGraph.getAllPaths(neighbor.getName(),target.getName());

            //for all paths
            for(ArrayList<String> arr : allPathsFromNeighbor){
                //if target name contain in arr there circle that target in
                if(arr.contains(target.getName())){

                    //move all target name to res until see target's name
                    for(int i=0;i<arr.size() && cont;i++){
                        if(arr.get(i).equals(target.getName())){
                            cont=false;

                        }
                        res.add(arr.get(i));

                    }
                }
            }
        }

        return new DtoTargetCircle(res,target.getName());
    }

    @Override
    public void readObjectFromFile(String filePath){
        File file=new File(filePath+".dat");
        if(!file.exists()){
            throw new RuntimeException("file does not exists");
        }
        try(ObjectInputStream in=new ObjectInputStream(new FileInputStream(filePath+".dat"))){
            this.targetGraph=(TargetGraph) in.readObject();
            this.wasRunBefore=(boolean) in.readObject();
            this.pathForTaskFolder=(String)in.readObject();
            this.task=(Task) in.readObject();
            this.folder=(String) in.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.out.println(e.getMessage());
            throw new IllegalArgumentException(" couldn't load instance from file");
        }
    }

    @Override
    public void writeObjectToFile(String path){
        checkIfGraphIsLoaded();

        path +=".dat";
        validatePath(path);
        try(ObjectOutputStream out=
                    new ObjectOutputStream(
                            new FileOutputStream(path))){
            out.writeObject(targetGraph);
            out.writeObject(wasRunBefore);
            out.writeObject(pathForTaskFolder);
            out.writeObject(task);
            out.writeObject(folder);
            out.flush();
        }catch (IOException e){
            throw new IllegalArgumentException("couldn't write instance to file " + e.getMessage());
        }
    }

    private void validatePath(String path) {
        File tempFile = new File(path);
        if(!tempFile.getParentFile().exists()){
            throw new RuntimeException("file does not exist");
        }
    }

    private void checkIfGraphIsLoaded() {
        if(targetGraph==null){
            throw new RuntimeException("There's no any data to save.");
        }
    }



    /** Creates parent directories if necessary. Then returns file */
    private static File fileWithDirectoryAssurance(String directory, String filename) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        return new File(directory + "/" + filename);
    }

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


    public int getNumOfThreads() {
        return targetGraph.getMaxParallelism();
    }

    /*
    @Override
    public void run() {
        Dto res =null;
        setThreadPool(runDetails.getNumOfThreads());
        if(runDetails.isIncremental()){
            handleIncremental();
        }else{
            handleScratch();
        }
        runTask();
        wasRunBefore = true;
        //return res;
    }
    */


    /*
    private void runTask(){
        List<Target> realRun=getChosenTargets();
        Set<Target>noRunYet=new HashSet<Target>();
        Set<Target>runningSet=new HashSet<Target>();

        for(Target curr:realRun){
            noRunYet.add(curr);
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
                        System.out.println("starting "+ curr.getName() + " " + curr.getTargetStatusValue());
                        boolean nextIteration=false;
                        //אם אחד הקודקודים שאני תלוי בהם, וקיים ברשימת קודקודים להרצה שלי עדין לא סיים אז אני אדלג לאיטרציה הבאה
                        //if frozen-It is obvious
                        //if waiting-It is obvious
                        //if in process-It is obvious
                        //if skipped-Extreme case: In practice the curr can not be frozen because it depends on someone who skipped!
                        //We will move on because sometime the curr will change to skipped and and otherwise iteration will be handled
                        for(Target iNeedHim:curr.getDependingOn()){
                            if(realRun.contains(iNeedHim)){
                                if((!iNeedHim.getFinishStatus().equals(Target.Finish.SUCCESS_WITH_WARNINGS))||(!iNeedHim.getFinishStatus().equals(Target.Finish.SUCCESS))){
                                    nextIteration=true;
                                }
                                //if(iNeedHim.getTargetStatus().equals(Target.TargetStatus.FROZEN)||iNeedHim.getTargetStatus().equals(Target.TargetStatus.WAITING)||iNeedHim.getTargetStatus().equals(Target.TargetStatus.INPROCESS)||iNeedHim.getTargetStatus().equals(Target.TargetStatus.SKIPPED)){
                                //    nextIteration=true;
                                //    break;
                                //}
                                //if(iNeedHim.getTargetStatus().equals(Target.TargetStatus.FINISHED)&&iNeedHim.getFinishStatus().equals(Target.Finish.FAILURE)){
                                //    nextIteration=true;
                                //   break;
                                //}
                            }
                        }
                        if(nextIteration){
                            break;
                        }
                        System.out.println("1. turning "+ curr.getName() + " " + curr.getTargetStatusValue());
                        curr.setTargetStatus(Target.TargetStatus.WAITING);
                        System.out.println("2. turned "+ curr.getName() + " " + curr.getTargetStatusValue());

                    }
                    case WAITING: {
                        if(!runningSet.contains(curr)) {
                            List<SerialSet> currListOfSets = curr.getListOfSets();
                            boolean breaking = false;
                            boolean canRun = true;
                            for (SerialSet currSet : currListOfSets) {
                                System.out.println("1. " + curr.getName() + " inside " + currSet.getName());
                                for (Target currTargetInCurrSet : currSet.getTargetSetOfSerialSet()) {
                                    System.out.println("2. " + curr.getName() + " in the same with running" + currTargetInCurrSet.getName() + " ?");
                                    //if(realRun.contains(currTargetInCurrSet)){
                                    if (runningSet.contains(currTargetInCurrSet) && !curr.getName().equals(currTargetInCurrSet.getName())) {
                                        System.out.println("yes");
                                        canRun = false;
                                        System.out.println("1. locked- " + curr.getName() + " " + curr.getTargetStatusValue());
                                        break;
                                    } else {
                                        System.out.println("no");
                                    }
                                }
                            }
                            if (canRun) {
                                curr.setTargetStatus(Target.TargetStatus.WAITING);//not need to
                                runningSet.add(curr);
                                Task task;
                                if (runDetails.isSimulation()) {
                                    task = new SimulationTask(runDetails);
                                } else {
                                    task = new SimulationTask(runDetails);
                                    //task = new CompilationTask();
                                }
                                task.setTarget(curr);
                                threadExecutor.execute(task);
                                //System.out.println("2. "+ curr.getName() + " " + curr.getTargetStatusValue());
                            }
                        }

                    }
                    break;
                    case SKIPPED:{
                        numOfEnd++;
                        noRunYet.remove(curr);
                        System.out.println(curr.getName() + " " + curr.getTargetStatusValue());
                    }
                    break;
                    case INPROCESS: {

                        System.out.println(curr.getName() + " " + curr.getTargetStatusValue());
                        break;
                    }
                    case FINISHED:{
                        System.out.println(curr.getName()+" stoped "+ curr.getFinishStatus()+" "+curr.getFinishStatusValue());

                        if(curr.getFinishStatus().equals(Target.Finish.FAILURE)){
                            List<Target> skippedList=new ArrayList<Target>();
                            makeSkipped(curr,skippedList,realRun);
                        }
                        numOfEnd++;
                        noRunYet.remove(curr);
                        runningSet.remove(curr);
                    }
                    break;
                    default:

                        // code block

                }
            }


        }

    }
*/

    @Override
    public void runTask(DetailsForTask details,
                        Consumer<String> successSummary,
                        Consumer<String> successWWSummary,
                        Consumer<String> failureSummary,
                        Consumer<String> skippedSummary,
                        Consumer<String> summary,
                        Consumer<String> logData,
                        Runnable onFinish) {
        this.runDetails=details;
        this.successSummary=successSummary;
        this.successWWSummary=successWWSummary;
        this.failureSummary=failureSummary;
        this.skippedSummary=skippedSummary;
        this.summary=summary;
        this.onFinish=onFinish;
        this.logData=logData;
        // createFileForTask("Simulation");

        Dto res =null;
        setThreadPool(runDetails.getNumOfThreads());
        if(runDetails.isIncremental()){
            handleIncremental();
        }else{
            handleScratch();
        }

        if(runDetails.isSimulation()){
            createFileForTask("Simulation");
        }else if(!runDetails.isSimulation()){
            createFileForTask("Compilation");
        }
        myRun=new MyRunTask(details,
                getChosenTargets(),
                executor,
                successSummary,
                successWWSummary,
                failureSummary,
                skippedSummary,
                summary,
                folder,
                logData);
        appController.bindTaskToUIComponents(myRun,onFinish);

        gpupRunner = new Thread(myRun);
        gpupRunner.setName("gpupRunner");
        gpupRunner.start();

    }


    private void handleIncremental() {
        List<Target> curList= getChosenTargets();
        for(Target curr: curList){
            if(curr.getFinishStatus().equals(Target.Finish.FAILURE)||curr.getTargetStatus().equals(Target.TargetStatus.SKIPPED)){
                curr.setTargetStatus(Target.TargetStatus.FROZEN);
                curr.setFinishStatus(Target.Finish.NOTFINISHED);
            }
        }
    }

    private void handleScratch() {
        List<Target> curList= getChosenTargets();
        for(Target curr: curList){
            curr.setTargetStatus(Target.TargetStatus.FROZEN);
            curr.setFinishStatus(Target.Finish.NOTFINISHED);
        }
    }

    public void stopRunning(){
        if(gpupRunner!=null){
            gpupRunner.stop();
        }
    }

    public Dto getTargetsData(){
        return new DTOTargetsData(targetGraph.getNumOfTargets(),
                targetGraph.getNumOfLeaf(),
                targetGraph.getNumOfMiddle(),
                targetGraph.getNumOfRoot(),
                targetGraph.getNumOfIndependent());
    }

    @Override
    public Dto getTargetData(String targetName) {
        Target target = targetGraph.getTarget(targetName);
        return new DTOTargetData(target.getName(),
                target.getLocation(),
                target.getRequiredToTargetsName(),
                target.getDependingOnTargetsName(),
                target.getData());

    }

    @Override
    public Dto getAllPathsBetweenTargets(String targetFrom, String targetTo,boolean isRequired) {
        if(isRequired){
            return new DTOPathsBetweenTargets(targetGraph.getTarget(targetFrom).getName(), targetGraph.getTarget(targetTo).getName(),targetGraph.getAllPaths(targetFrom, targetTo), isRequired);
        }else{
            return new DTOPathsBetweenTargets(targetGraph.getTarget(targetFrom).getName(), targetGraph.getTarget(targetTo).getName(),targetGraph.getAllPaths(targetTo, targetFrom), isRequired);
        }

    }

    public boolean getWasRunBefore(){ return wasRunBefore;}

    public List<Target> getChosenTargets(){
        List<Target> res= new ArrayList<Target>();
        for(WrapsTarget wrapsTarget:wrapsTargetList){
            if(wrapsTarget.getCheckbox().isSelected()){
                res.add(wrapsTarget.getTarget());
            }
        }
        return res;
    }

    public List<SerialSet> getListOfSerialSet(){return targetGraph.getListOfSerialSet(); }

    public List<String> getAllOfIndirect(String targetName, boolean isRequired) {
        Target target= targetGraph.getTarget(targetName);
        Set<Target> targetSet;
        List<String> res=new ArrayList<String>();
        if(isRequired)
        {
            targetSet = target.getAllOfIndirectRequiredFor();
            for(Target targetTemp:targetSet){
                res.add(targetTemp.getName());
            }
        }
        else {
            targetSet = target.getAllOfIndirectDependsOn();;
            for(Target targetTemp:targetSet){
                res.add(targetTemp.getName());
            }
        }
        if(res.size()==0)
        {
            if(isRequired){
                res.add("There are no targets that directly/indirectly required for "+targetName+".");
            }
            else {
                res.add("There are no targets that directly/indirectly depends on "+targetName+".");
            }
        }

        return res;
    }

    public List<WrapsTarget> getWrapsTargetList(){
        return wrapsTargetList;
    }

    public List<Target> getTargetList(){return targetGraph.getTargetList();}

    public List<String> getTargetNamesList(){return targetGraph.getTargetNamesList(); }

    public int getNumOfTargets(){ return targetGraph.getNumOfTargets();}

    public int getNumOfIndependent(){ return targetGraph.getNumOfIndependent();}

    public int getNumOfLeaf(){return targetGraph.getNumOfLeaf();}

    public int getNumOfMiddle(){return targetGraph.getNumOfMiddle();}

    public int getNumOfRoot(){return targetGraph.getNumOfRoot();}

    public void stopTask(boolean isStopTask){
        if(isStopTask){
            gpupRunner.suspend();

            runQ =executor.getQueue();

            executor.shutdown();

        }else{
            gpupRunner.resume();
            this.executor = new ThreadPoolExecutor(numOfThreads, numOfThreads, 2, TimeUnit.HOURS, runQ);

            myRun=new MyRunTask(runDetails,
                    getChosenTargets(),
                    executor,
                    successSummary,
                    successWWSummary,
                    failureSummary,
                    skippedSummary,
                    summary,
                    folder,
                    logData);
            gpupRunner = new Thread(myRun);
            gpupRunner.setName("gpupRunner");
            gpupRunner.start();

            appController.bindTaskToUIComponents(myRun,onFinish);
            //myRun.setThreadPoolExecutor(this.executor);
            //myRun.setThreadPoolExecutor(temp);

        }
    }

    public void setNumOfThreads(int numOfThreads){
        this.numOfThreads=numOfThreads;
    }







    public void getGraphUsingGraphViz(String outPutPath, String filesNames) {

        try {
            GraphViz gv = new GraphViz(getExeFromEnvironmentVar(), outPutPath);
            gv.addln(gv.start_graph());
            gv.addln(targetGraph.getStringGraph());
            gv.addln(gv.end_graph());
            gv.increaseDpi(); // 106 dpi
            System.out.println(gv.getDotSource());
            String type = "png";

            String representationType = "dot";

            File out = new File(outPutPath + File.separator + filesNames + "." + type);
            gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type, representationType), out);
            saveDotTextFile(outPutPath, filesNames, gv);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error - dot executable not found");
        }
    }

    private void saveDotTextFile(String outPutPath, String filesNames, GraphViz gv) {
        try {
            FileWriter fw = new FileWriter(outPutPath + File.separator + filesNames + ".viz");
            fw.write(gv.getDotSource());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error - could not write the dot txt file");
        }
    }

    private String getExeFromEnvironmentVar() {
        return Arrays.stream(System.getenv("Path").split(";"))
                .filter(path -> path.contains("Graphviz"))
                .map(path -> path.concat("\\dot.exe"))
                .findFirst()
                .orElse("C:\\Program Files\\Graphviz\\bin\\dot.exe");
    }

}


