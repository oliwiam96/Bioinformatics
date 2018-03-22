public class Arc {

    private Node next;
    private int weight;

    public Arc(Node next, int weight) {
        this.next = next;
        this.weight = weight;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
