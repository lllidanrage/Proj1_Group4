import java.util.Comparator;
import java.util.List;

public class FloorRobot extends Robot{

    public Direction getTransferPosition() {return transferPosition;}

    public void setTransferPosition(Direction transferPosition) {
        this.transferPosition = transferPosition;
    }

    private Direction transferPosition = Direction.NONE;

    private final int numRooms;

    FloorRobot(MailRoom mailroom, int capacity, int numRooms, int floor, int room) {
        super(mailroom, capacity);
        this.numRooms = numRooms;
        this.floor = floor;
        this.room = room;
    }

    /*
    Check if there are any column robots waiting on both sides.
     */
    public Direction isWaiting(Robot ref) {
        // check there is robot is waiting on current floor.
        boolean left = false;
        boolean right = false;
        Robot rightRobot = null;
        Robot leftRobot = null;

        for (Robot robot : mailroom.getActiveRobotsColumn()) {
            if (robot.floor == ref.floor && !robot.items.isEmpty() && robot.items.getFirst().myFloor() == ref.floor) {
                if (robot.room == 0) {
                    left = true;
                    leftRobot = robot;
                } else if (robot.room == (numRooms + 1)) {
                    right = true;
                    rightRobot = robot;
                }
            }
        }

        if (left  && !right ) {
            return Direction.LEFT;
        } else if (!left  && right ) {
            return Direction.RIGHT;
        }else if (left && right ) {
            if (leftDeliEarlier(leftRobot, rightRobot) == true) {
                return Direction.LEFT;
            }
            else {
                return Direction.RIGHT;
            }
        }
        return Direction.NONE;
    }

    /*
    If there are column robots in both left and right side,check items in witch side arrival earlier
     */
    public boolean leftDeliEarlier(Robot leftRobot, Robot rightRobot) {
        List<MailItem> items1 = leftRobot.items;
        List<MailItem> items2 = rightRobot.items;

        items1.sort(Comparator.comparingInt(MailItem::myArrival));
        items2.sort(Comparator.comparingInt(MailItem::myArrival));

        int earlyLeft = items1.getFirst().myArrival();
        int earlyRight = items2.getFirst().myArrival();

        if (earlyLeft < earlyRight) {
            return true;
        }else if (earlyLeft > earlyRight) {
            return false;
        }else {
            return true;
        }
    }

    void tick(){
        //write the overridden method for tick in here
        // when there`s nothing in the flooring robot`s bag, the robot will stay or
        // move to column robot to take the item
        if (items.isEmpty()) {
            if (isWaiting(this) == Direction.LEFT && this.room == 1) {
                Robot sourceRobot = null;
                for (Robot r : mailroom.getActiveRobotsColumn()) {
                    if (r.getId().equals("R1")) {
                        sourceRobot = r;
                        break;
                    }
                }
                if (sourceRobot != null) {
                    this.transfer(sourceRobot);
                }
                this.setTransferPosition(Direction.LEFT);
            }
            else if (isWaiting(this) == Direction.LEFT && this.room != 1) {
                this.move(Direction.LEFT);
            }

            else if (isWaiting(this) == Direction.RIGHT && this.room == numRooms) {
                Robot sourceRobot = null;
                for (Robot r : mailroom.getActiveRobotsColumn()) {
                    if (r.getId().equals("R2")) {
                        sourceRobot = r;
                        break;
                    }
                }
                if (sourceRobot != null) {
                    this.transfer(sourceRobot);
                }
                this.setTransferPosition(Direction.RIGHT);

            }

            else if (isWaiting(this) == Direction.RIGHT && this.room != numRooms) {
                this.move(Direction.RIGHT);
            }
        } else {
            // Items to deliver
            if (this.floor == this.items.getFirst().myFloor() && this.room == this.items.getFirst().myRoom()) {
                do {
                    MailItem firstItem = this.items.getFirst();
                    this.remainingCapacity += firstItem.myWeight();
                    Simulation.deliver(this.items.removeFirst());
                } while (!this.items.isEmpty() && this.room == this.items.getFirst().myRoom());
            }
            else if (this.getTransferPosition() == Direction.LEFT) {
                this.move(Direction.RIGHT);
            }
            else if (this.getTransferPosition() == Direction.RIGHT) {
                this.move(Direction.LEFT);
            }
        }
    }
}
