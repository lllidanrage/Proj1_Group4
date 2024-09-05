import java.util.*;

abstract public class Robot {
    private static int count = 1;
    final private String id;

    protected int floor;
    protected int room;
    final private MailRoom mailroom;
    final protected List<MailItem> items = new ArrayList<>();
    private int load;
    protected int remainingCapacity;

    public String toString() {
        return "Id: " + id + " Floor: " + floor + ", Room: " + room + ", #items: " + numItems() + ", Load: " + calLoad() ;
    }

    Robot(MailRoom mailroom, int capacity) {
        this.id = "R" + count++;
        this.mailroom = mailroom;
        this.remainingCapacity = capacity;
    }

    int getFloor() { return floor; }
    int getRoom() { return room; }
    int calLoad() {
        load = 0;
        for (MailItem item : items) {
            load+= item.myWeight();
        }
        return load;
    }

    boolean isEmpty() { return items.isEmpty(); }

    public void place(int floor, int room) {
        Building building = Building.getBuilding();
        building.place(floor, room, id);
        this.floor = floor;
        this.room = room;
    }

    public void move(Building.Direction direction) {
        Building building = Building.getBuilding();
        int dfloor, droom;
        switch (direction) {
            case UP    -> {dfloor = floor+1; droom = room;}
            case DOWN  -> {dfloor = floor-1; droom = room;}
            case LEFT  -> {dfloor = floor;   droom = room-1;}
            case RIGHT -> {dfloor = floor;   droom = room+1;}
            default -> throw new IllegalArgumentException("Unexpected value: " + direction);
        }
        if (!building.isOccupied(dfloor, droom)) {
            building.move(floor, room, direction, id);
            floor = dfloor; room = droom;
            if (floor == 0) {
                System.out.printf("About to return: " + this + "\n");
                mailroom.robotReturn(this);
            }
        }
    }

    void transfer(Robot robot) {
        ListIterator<MailItem> iter = robot.items.listIterator();
        while(iter.hasNext()) {
            MailItem item = iter.next();
            this.add(item);
            this.remainingCapacity-= item.myWeight();
            iter.remove();
            robot.setRemainingCapacity((robot.getRemainingCapacity()+item.myWeight()));
        }
    }

    abstract void tick();

    public String getId() {
        return id;
    }

    public int numItems () {
        return items.size();
    }

    public void add(MailItem item) {
        items.add(item);
    }

    void sort() {
        Collections.sort(items);
    }

    // $
    void reverseSort() {Collections.sort(items, Comparator.reverseOrder());}

    public int getRemainingCapacity() {
        return remainingCapacity;
    }

    public void setRemainingCapacity(int remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }

}
