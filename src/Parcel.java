public class Parcel extends MailItem{

    Parcel(int floor, int room, int arrival, int weight) {
        super(floor, room, arrival);
        this.weight = weight;
    }
}
