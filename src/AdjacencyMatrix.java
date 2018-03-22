import java.util.List;

public class AdjacencyMatrix {
    private int n;
    private int[][] matrix;

    public void test(){
        Node node1 = new Node("DFABCABC");
        Node node2 = new Node("ABCABCZZ");

        //System.out.println(getWieght(node1, node2));
        //readInputFromFile("neg0.txt");
    }

    public AdjacencyMatrix(int n, int[][] matrix) {
        this.n = n;
        this.matrix = matrix;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }
}
