import java.util.List;

public class FloorRobot extends Robot{

    public int getTransferPosition() {
        return transferPosition;
    }

    public void setTransferPosition(int transferPosition) {
        this.transferPosition = transferPosition;
    }

    // 0 means 在最左边接收的item， 1 means在最右边接收的item
    private int transferPosition = -1;

    private List<Robot> activeRobots;

    private int numRooms;

    FloorRobot(MailRoom mailroom, int capacity, List<Robot> activeRobots, int numRooms, int floor, int room) {
        super(mailroom, capacity);
        this.activeRobots = activeRobots;
        this.numRooms = numRooms;
        this.floor = floor;
        this.room = room;
    }



    public List<Robot> getActiveRobots() {
        return activeRobots;
    }

    public void setActiveRobots(List<Robot> activeRobots) {
        this.activeRobots = activeRobots;
    }

    public int checkWaiting(Robot ref) {
        int left = 0;
        int right = 0;
        Robot leftRobot = null;
        Robot rightRobot = null;
        for (Robot robot : activeRobots) {
            if (robot.floor == ref.floor && !robot.items.isEmpty() && robot.items.getFirst().myFloor() == ref.floor) {
                if (robot.room == 0) {
                    left = 1;
                    leftRobot = robot;
                } else if (robot.room == (numRooms + 1)) {
                    right = 1;
                    rightRobot = robot;
                }
            }
        }

        if (ref.getId().equals("R5")) {
        }
        if (left == 1 && right == 0) {
            return 0;
        } else if (left == 0 && right == 1) {
            return 1;
        }
        return -1;
    }

    void tick(){
        //write the overridden method for tick in here
        // when there`s nothing in the flooring robot`s bag, the robot will stay or
        // move to column robot to take the item
        if (items.isEmpty()) {
            if (checkWaiting(this) == 0 && this.room == 1) {
                Robot sourceRobot = null;
                for (Robot r : activeRobots) {
                    if (r.getId().equals("R1")) {
                        sourceRobot = r;
                        break;
                    }
                }
                if (sourceRobot != null) {
                    this.transfer(sourceRobot);
                }
                this.setTransferPosition(0);
            }
            else if (checkWaiting(this) == 0 && this.room != 1) {
                this.move(Building.Direction.LEFT);
            }

            else if (checkWaiting(this) == 1 && this.room == numRooms) {
                Robot sourceRobot = null;
                for (Robot r : activeRobots) {
                    if (r.getId().equals("R2")) {
                        sourceRobot = r;
                        break;
                    }
                }
                if (sourceRobot != null) {
                    this.transfer(sourceRobot);
                }
                this.setTransferPosition(1);

            }

            else if (checkWaiting(this) == 1 && this.room != numRooms) {
                this.move(Building.Direction.RIGHT);
            }
        } else {
            // Items to deliver
            if (this.floor == this.items.getFirst().myFloor() && this.room == this.items.getFirst().myRoom()) {
                do {
                    MailItem firstItem = this.items.get(0);
                    this.remainingCapacity += firstItem.myWeight();
                    Simulation.deliver(this.items.removeFirst());


                } while (!this.items.isEmpty() && this.room == this.items.getFirst().myRoom());
            }
            else if (this.getTransferPosition() == 0) {
                this.move(Building.Direction.RIGHT);
            }
            else if (this.getTransferPosition() == 1) {
                this.move(Building.Direction.LEFT);
            }
        }
    }
}
