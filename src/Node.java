import java.util.List;

public class Node {

    private String nucleortide;
    List<Arc> neighbours;

    public Node(String nucleortide, List<Arc> neighbours) {
        this.nucleortide = nucleortide;
        this.neighbours = neighbours;
    }

    public String getNucleortide() {
        return nucleortide;
    }

    public void setNucleortide(String nucleortide) {
        this.nucleortide = nucleortide;
    }

    public List<Arc> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(List<Arc> neighbours) {
        this.neighbours = neighbours;
    }
}
