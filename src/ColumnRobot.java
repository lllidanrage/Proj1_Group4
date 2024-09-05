import java.util.List;

public class ColumnRobot extends Robot{
    ColumnRobot(MailRoom mailroom, int capacity) {
        super(mailroom, capacity);
    }

    void tick(){
        //write the overridden method for tick in here
        if (!this.items.isEmpty() && this.floor != this.items.get(0).myFloor()) {
            this.move(Building.Direction.UP);
        }
        else if (!this.items.isEmpty() && this.floor == this.items.get(0).myFloor()) {
            // do nothing, just waiting
        }
        else if (this.items.isEmpty() && this.floor != 0) {
            this.move(Building.Direction.DOWN);
        }
    }
}
