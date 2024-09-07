public class ColumnRobot extends Robot{
    ColumnRobot(MailRoom mailroom, int capacity) {
        super(mailroom, capacity);
    }

    void tick(){
        if (!this.items.isEmpty() && this.floor != this.items.getFirst().myFloor()) {
            this.move(Direction.UP);
        }else if (this.items.isEmpty() && this.floor != 0) {
            this.move(Direction.DOWN);
        }
    }
}
