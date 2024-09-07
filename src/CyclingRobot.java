public class CyclingRobot extends Robot {
    CyclingRobot(MailRoom mailroom, int capacity) {
        super(mailroom, capacity);
    }

    @Override
    void tick() {
        {
            Building building = Building.getBuilding();
            if (items.isEmpty()) {
                if (room == building.NUMROOMS + 1) {
                    move(Direction.DOWN);
                } else {
                    move(Direction.RIGHT);
                }
            }
            else {
                if (floor == items.getFirst().myFloor()) {
                    if (room == items.getFirst().myRoom()) {
                        do {
                            MailItem firstItem = items.getFirst();
                            this.remainingCapacity += firstItem.myWeight();
                            Simulation.deliver(items.removeFirst());
                        } while (!items.isEmpty() && room == items.getFirst().myRoom());
                    }
                    else {
                        move(Direction.RIGHT);
                    }
                }
                else {
                    move(Direction.UP);
                }
            }
        }
    }


}
