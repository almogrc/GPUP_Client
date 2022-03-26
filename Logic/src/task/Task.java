package task;

import engine.dto.DTOTaskOnTarget;
import engine.graph.Target;
import engine.dto.Dto;
import javafx.beans.property.SimpleStringProperty;


public interface Task extends Runnable{

    public void setTarget(Target target, SimpleStringProperty message);
    public DTOTaskOnTarget getDTOTaskOnTarget();

        //DTOTaskOnTarget run(Target currentTarget);
}
