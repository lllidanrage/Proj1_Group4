public abstract class MailItem implements Comparable<MailItem> {

    private final int myArrival;
    private final int myFloor;
    private final int myRoom;

    protected int myWeight = 0;

    public MailItem(int floor, int room, int arrival) {
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
    public int myWeight() { return myWeight; }


    @Override
    public String toString() {
        return String.format("Time = %d Floor = %d Room = %d", myArrival, myFloor, myRoom);
    }
}

//NOTE, need to update this in classes where Letter was originally used to accommodate for new MailItem

