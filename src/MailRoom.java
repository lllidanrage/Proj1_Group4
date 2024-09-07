import java.util.*;

import static java.lang.String.format;

public class MailRoom {
    public enum Mode {CYCLING, FLOORING}
    List<MailItem>[] waitingForDelivery;
    private final int numRobots;

    Queue<Robot> idleRobots;
    List<Robot> activeRobots;
    List<Robot> activeRobotsColumn;
    List<Robot> deactivatingRobots;

    private int robotCapacity;
    private final int numRooms;
    private final Mode mode;
    private int initial = 0;

    public boolean someItems() {
        for (int i = 0; i < Building.getBuilding().NUMFLOORS; i++) {
            if (!waitingForDelivery[i].isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private int floorWithEarliestItem() {
        int floor = -1;
        int earliest = Simulation.now() + 1;
        for (int i = 0; i < Building.getBuilding().NUMFLOORS; i++) {
            if (!waitingForDelivery[i].isEmpty()) {
                int arrival = waitingForDelivery[i].getFirst().myArrival();

                if (earliest > arrival) {
                    floor = i;
                    earliest = arrival;
                }
            }
        }
        return floor;
    }

    MailRoom(int numFloors, int numRobots,int robotCapacity, int numRooms, Mode mode) {
        this.robotCapacity = robotCapacity;
        this.numRooms = numRooms;
        this.mode = mode;

        waitingForDelivery = new List[numFloors];
        for (int i = 0; i < numFloors; i++) {
            waitingForDelivery[i] = new LinkedList<>();
        }
        this.numRobots = numRobots;

        idleRobots = new LinkedList<>();

        activeRobots = new ArrayList<>();
        activeRobotsColumn = new ArrayList<>();
        deactivatingRobots = new ArrayList<>();

        if (mode == Mode.CYCLING) {
            for (int i = 0; i < numRobots; i++)
                idleRobots.add(new CyclingRobot(MailRoom.this, robotCapacity));
        }else if (mode == Mode.FLOORING) {
            idleRobots.add(new ColumnRobot(MailRoom.this, robotCapacity));
            idleRobots.add(new ColumnRobot(MailRoom.this, robotCapacity));
            Building building = Building.getBuilding();
            for (int i = 0; i < building.NUMFLOORS; i++)
                activeRobots.add(new FloorRobot(MailRoom.this, robotCapacity,activeRobotsColumn,numRooms,i+1,1));
        }
    }

    void arrive(List<MailItem> items) {
        // add item into waitingForDelivery based on the floor number
        for (MailItem item : items) {
            waitingForDelivery[item.myFloor()-1].add(item);

            System.out.printf("Item: Time = %d Floor = %d Room = %d Weight = %d\n",
                    item.myArrival(), item.myFloor(), item.myRoom(), item.myWeight());
        }
    }

    public void tick() {
        if (this.mode == Mode.FLOORING) {
            for (Robot robotR : activeRobots) {
                System.out.printf("About to tick: " + robotR.toString() + "\n");
                robotR.tick();
            }
            robotDispatch();

            for (Robot robotC : activeRobotsColumn) {
                robotC.tick();
            }

            int length = idleRobots.size();
            while (length > 0) {
                System.out.println("Dispatch at time = " + Simulation.now());
                int fwei = floorWithEarliestItem();
                if (fwei >= 0) {
                    Robot robot = idleRobots.remove();
                    length -= 1;
                    loadRobot(fwei, robot);
                    if (robot.getId().equals("R1")) {
                        robot.sort();
                    }
                    else if (robot.getId().equals("R2")) {
                        robot.reverseSort();
                    }

                    activeRobotsColumn.add(robot);
                    System.out.println("Dispatch @ " + Simulation.now() +
                            " of Robot " + robot.getId() + " with " + robot.numItems() + " item(s)");
                    if (robot.getId().equals("R1")) {
                        robot.place(0, 0);
                    } else if (robot.getId().equals("R2")) {
                        robot.place(0, numRooms + 1);
                    }
                }

            }

            ListIterator<Robot> iter = deactivatingRobots.listIterator();
            while (iter.hasNext()) {
                Robot robot = iter.next();
                iter.remove();
                activeRobotsColumn.remove(robot);
                idleRobots.add(robot);
            }
        }

        if (this.mode == Mode.CYCLING ) {
            for (Robot activeRobot : activeRobots) {
                System.out.printf("About to tick: " + activeRobot.toString() + "\n"); activeRobot.tick();
            }
            robotDispatch();

            ListIterator<Robot> iter = deactivatingRobots.listIterator();
            while (iter.hasNext()) {
                Robot robot = iter.next();
                iter.remove();
                activeRobots.remove(robot);
                idleRobots.add(robot);
            }
        }
    }

    void robotDispatch() {
        if (this.mode == Mode.CYCLING) {
            System.out.println("Dispatch at time = " + Simulation.now());
            if (!idleRobots.isEmpty() && !Building.getBuilding().isOccupied(0,0)) {
                int fwei = floorWithEarliestItem();
                if (fwei >= 0) {
                    Robot robot = idleRobots.remove();
                    loadRobot(fwei, robot);
                    robot.sort();
                    activeRobots.add(robot);
                    System.out.println("Dispatch @ " + Simulation.now() +
                            " of Robot " + robot.getId() + " with " + robot.numItems() + " item(s)");
                    robot.place(0, 0);
                }
            }
        }

        else if (mode == Mode.FLOORING) {
            if (initial == 0) {
                initial = 1;
                int floorNum = 1;
                for (Robot robot : activeRobots) {
                    robot.place(floorNum, 1);
                    floorNum += 1;
                }
            }
        }

    }

    void robotReturn(Robot robot) {
        Building building = Building.getBuilding();
        int floor = robot.getFloor();
        int room = robot.getRoom();
        assert floor == 0 && room == building.NUMROOMS+1: format("robot returning from wrong place - floor=%d, room ==%d", floor, room);
        assert robot.isEmpty() : "robot has returned still carrying at least one item";
        building.remove(floor, room);
        deactivatingRobots.add(robot);
    }

    void loadRobot(int floor, Robot robot) {
        Collections.sort(waitingForDelivery[floor], Comparator.comparingInt(MailItem::myArrival));

        ListIterator<MailItem> iter = waitingForDelivery[floor].listIterator();

        int remainingCapacity = robot.getRemainingCapacity();
        while (iter.hasNext()) {
            MailItem item = iter.next();

            // load the letter
            if (item.myWeight() == 0) {
                robot.add(item);
                iter.remove();
            }
            else if (item.myWeight() > 0 && item.myWeight() < remainingCapacity) {
                robot.add(item);
                remainingCapacity -= item.myWeight();
                iter.remove();
                robot.setRemainingCapacity(remainingCapacity);
            }
        }
    }
}
