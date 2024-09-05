public class Parcel extends MailItem{

    public Parcel( int floor, int room, int arrival,int weight) {
        super( floor, room,arrival);
        this.myWeight = weight;
    }
    public int getWeight() {
        return myWeight;
    }

    public String toString() {
        return "Parcel: " + super.toString() + " Weight = " + myWeight;
    }
}
