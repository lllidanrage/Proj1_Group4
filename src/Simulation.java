// Check that maxweight (of parcel) is less than or equal to the maxcapacity of robot.

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Simulation {
    private static final Map<Integer, List<MailItem>> waitingToArrive = new HashMap<>();
    public static int time = 0;
    public final int endArrival;
    final public MailRoom mailroom;
    private static int timeout;

    private static int deliveredCount = 0;
    private static int deliveredTotalTime = 0;

    public static void deliver(MailItem mailItem) {
        System.out.println("Delivered: " + mailItem);
        deliveredCount++;
        deliveredTotalTime += now() - mailItem.myArrival();
    }
    void addToArrivals(int arrivalTime, MailItem item) {
        System.out.println(item.toString());
        if (waitingToArrive.containsKey(arrivalTime)) {
            waitingToArrive.get(arrivalTime).add(item);
        }
        else {
            LinkedList<MailItem> items = new LinkedList<>();
            items.add(item);
            waitingToArrive.put(arrivalTime, items);
        }
    }

    Simulation(Properties properties) {
        int seed = Integer.parseInt(properties.getProperty("seed"));
        Random random = new Random(seed);
        this.endArrival = Integer.parseInt(properties.getProperty("mail.endarrival"));
        int numLetters = Integer.parseInt(properties.getProperty("mail.letters"));
        int numParcels = Integer.parseInt(properties.getProperty("mail.parcels"));
        int maxWeight = Integer.parseInt(properties.getProperty("mail.parcelmaxweight"));
        int numFloors = Integer.parseInt(properties.getProperty("building.floors"));
        int numRooms = Integer.parseInt(properties.getProperty("building.roomsperfloor"));
        int numRobots = Integer.parseInt(properties.getProperty("robot.number"));
        int robotCapacity = Integer.parseInt(properties.getProperty("robot.capacity"));
        timeout = Integer.parseInt(properties.getProperty("timeout"));
        MailRoom.Mode mode = MailRoom.Mode.valueOf(properties.getProperty("mode"));

        Building.initialise(numFloors, numRooms);
        Building building = Building.getBuilding();
        mailroom = new MailRoom(building.NUMFLOORS, numRobots, robotCapacity,numRooms,mode);

        for (int i = 0; i < numLetters; i++) {
            int arrivalTime = random.nextInt(endArrival)+1;
            int floor = random.nextInt(building.NUMFLOORS)+1;
            int room = random.nextInt(building.NUMROOMS)+1;
            addToArrivals(arrivalTime, new Letter(floor, room, arrivalTime));
        }

        for (int i = 0; i < numParcels; i++) {
            int arrivalTime = random.nextInt(endArrival)+1;
            int floor = random.nextInt(building.NUMFLOORS)+1;
            int room = random.nextInt(building.NUMROOMS)+1;
            int weight = random.nextInt(maxWeight)+1;
            addToArrivals(arrivalTime, new Parcel(floor, room, arrivalTime,weight));
        }
    }

    public static int now() { return time; }

    void step() {
        if (waitingToArrive.containsKey(time))
            mailroom.arrive(waitingToArrive.get(time));
        mailroom.tick();
    }

    void run() {
        while (time++ <= endArrival || mailroom.someItems()) {
            step();
            try {
                TimeUnit.MILLISECONDS.sleep(timeout);
            } catch (InterruptedException e) {
                // System.out.printf("Sleep interrupted!\n");
            }
        }
        System.out.printf("Finished: Items delivered = %d; Average time for delivery = %.2f%n",
                deliveredCount, (float) deliveredTotalTime/deliveredCount);
    }

}
