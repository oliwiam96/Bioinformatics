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

    public int[] getRowSum() {
        return rowSum;
    }

    public void setRowSum(int[] rowSum) {
        this.rowSum = rowSum;
    }
}
