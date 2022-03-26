package engine.engineGpup;

import java.io.File;

public class DetailsForTask {
    private int processingTime;
    private int numOfThreads;
    private double probabilityForSuccess;
    private double probabilityForSuccessWithWarnings;
    private boolean isRandom;
    private boolean isIncremental;
    private boolean isSimulation;

    private File directoryToCompile;
    private File directoryForCompiled;
    public DetailsForTask(int processingTime, double probabilityForSuccess, double probabilityForSuccessWithWarnings, boolean isRandom, boolean isIncremental, boolean isSimulation, int numOfThreads) {
        this.processingTime=processingTime;
        this.probabilityForSuccess=probabilityForSuccess;
        this.probabilityForSuccessWithWarnings=probabilityForSuccessWithWarnings;
        this.isRandom=isRandom;
        this.isIncremental=isIncremental;
        this.isSimulation=isSimulation;
        this.numOfThreads=numOfThreads;
    }

    public DetailsForTask(File directoryToCompile, File directoryForCompiled, boolean isIncremental, boolean isSimulation, int numOfThreads) {
        this.directoryForCompiled=directoryForCompiled;
        this.directoryToCompile=directoryToCompile;
        this.isIncremental=isIncremental;
        this.isSimulation=isSimulation;
        this.numOfThreads=numOfThreads;

    }

    public double getProbabilityForSuccess() {
        return probabilityForSuccess;
    }

    public double getProbabilityForSuccessWithWarnings() {
        return probabilityForSuccessWithWarnings;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public int getNumOfThreads() {
        return numOfThreads;
    }

    public boolean isIncremental() {
        return isIncremental;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public boolean isSimulation() {
        return isSimulation;
    }

    public void setNumOfThreads(int numOfThreads) {
        this.numOfThreads = numOfThreads;
    }

    public void setIncremental(boolean incremental) {
        isIncremental = incremental;
    }

    public void setProbabilityForSuccess(double probabilityForSuccess) {
        this.probabilityForSuccess = probabilityForSuccess;
    }

    public void setProbabilityForSuccessWithWarnings(double probabilityForSuccessWithWarnings) {
        this.probabilityForSuccessWithWarnings = probabilityForSuccessWithWarnings;
    }

    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }

    public void setSimulation(boolean simulation) {
        isSimulation = simulation;
    }

    public File getDirectoryForCompiled() {
        return directoryForCompiled;
    }

    public File geDirectoryToCompile() {
        return directoryToCompile;
    }
}
