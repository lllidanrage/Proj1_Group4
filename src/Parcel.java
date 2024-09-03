public class Parcel extends MailItem{
    private final int weight;
    public Parcel(int arrival, int floor, int room, int weight) {
        super(arrival, floor, room);
        this.weight = weight;
    }
    public int getWeight() {
        return weight;
    }

    public String toString() {
        return "Parcel: " + super.toString() + " Weight = " + weight;
    }
}
