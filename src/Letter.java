public class Letter extends MailItem {


    Letter(int floor, int room, int arrival) {
        super(floor, room, arrival);
    }

    public String toString() {
        return "Letter: " +super.toString();
    }

}
