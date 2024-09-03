public abstract class MailItem implements Comparable<MailItem> {

    private final int myArrival;
    private final int myFloor;
    private final int myRoom;

    public MailItem(int arrival, int floor, int room) {
        this.myArrival = arrival;
        this.myFloor = floor;
        this.myRoom = room;
    }

    public int compareTo(MailItem other) {
        return Integer.compare(this.myArrival, other.myArrival);
    }
    public int myArrival() { return myArrival; }
    public int myFloor() { return myFloor; }
    public int myRoom() { return myRoom; }

    @Override
    public String toString() {
        return String.format("Time = %d Floor = %d Room = %d", myArrival, myFloor, myRoom);
    }
}

//NOTE, need to update this in classes where Letter was originally used to accommodate for new MailItem

