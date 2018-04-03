public class AdjacencyMatrix {
    private int n;
    private int[][] matrix;
    private int rowSum[];


    public AdjacencyMatrix(int n, int[][] matrix) {
        this.n = n;
        this.matrix = matrix;
        this.rowSum = new int[n];
        for (int i = 0; i < this.n; i++){
            this.rowSum[i] = 0;
            for (int j = 0; j < this.n; j++){
                this.rowSum[i] += this.matrix[i][j];
            }
            System.out.println(this.getRowSum(i));
        }
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

    public int getRowSum(int i) {
        return rowSum[i];
    }

    public void setRowSum(int i, int num) {
        this.rowSum[i] = num;
    }
}
