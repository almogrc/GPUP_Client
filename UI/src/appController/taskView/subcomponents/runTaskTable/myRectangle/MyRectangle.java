package appController.taskView.subcomponents.runTaskTable.myRectangle;

import engine.graph.Target;
import engine.graph.WrapsTarget;
import javafx.animation.FillTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;



public class MyRectangle extends SimpleStringProperty {

    private Text text;
    private Rectangle rectangle;
    private Target target;
    private StackPane myRectangle;
    private Color endColor=null;
    private Color startColor=null;

    public MyRectangle(Target target)
    {
        myRectangle=new StackPane();
        this.target=target;
        rectangle=new Rectangle();
        rectangle.setHeight(50);
        rectangle.setWidth(70);
        text=new Text(target.getName());
        myRectangle.getChildren().addAll(rectangle,text);
        StackPane.setAlignment(rectangle, Pos.CENTER); //set it to the Center Left(by default it's on the center)
        StackPane.setAlignment(text, Pos.CENTER); //set it to the Center Left(by default it's on the center)


    }


    public String getStatus(){
        return target.getTargetStatusValue();
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public StackPane getMyRectangle(){
        return myRectangle;
    }

    public boolean setAnimation(int millisForAnimations) {
        Color newEnd = null;

        switch(target.getTargetStatusValue()) {
            case "Frozen":{
                newEnd= Color.rgb(194,216,242,1);
            }
                break;
            case "Skipped":{
                newEnd=Color.rgb(159, 159, 159,1);
            }
                break;
            case "Waiting": {
                newEnd=Color.rgb(148,112,205,1);
            }
            break;
            case "In Process": {
                newEnd=Color.rgb(255, 138, 101,1);
            }
            break;
            case "Finished":{
                switch(target.getFinishStatusValue()) {
                    case "Success":
                        newEnd=Color.rgb(165, 214, 167,1);
                        break;
                    case "Failure":
                        newEnd=Color.rgb(229, 115, 115,1);
                        break;
                    case "Success With Warnings":
                        newEnd=Color.rgb(255, 245, 157,1);
                        break;
                    case "Not Finished":
                        newEnd=Color.rgb(77, 182, 172,1);
                        break;
                    default:
                        System.out.println("G.P.U.P/UI/src/appController/taskView/subcomponents/runTable/myRectangle/MyRectangle.java  setAnimation  Small Switch Case");
                }
            }
            break;
            default:
                System.out.println("G.P.U.P/UI/src/appController/taskView/subcomponents/runTable/myRectangle/MyRectangle.java  setAnimation  Big Switch Case");
        }
        endColor=newEnd;
        FillTransition ft = new FillTransition(Duration.millis(millisForAnimations), this.rectangle, startColor, endColor);
        //ft.setCycleCount(2);
        //ft.setAutoReverse(true);
        ft.play();

        startColor =endColor;
        return true;
    }

    public SimpleStringProperty getStatusProperty(){return target.targetStatusProperty();}

    public Target getTarget() {
        return target;
    }
}

