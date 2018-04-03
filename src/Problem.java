import java.util.ArrayList;
import java.util.List;

public abstract class Problem {
    protected int maxLengthOfSequence;
    protected int nucleotideLength;
    protected int errorsNumber; // neg errors
    protected int n; //matrix size (n x n)
    protected AdjacencyMatrix adjacencyMatrix;
    protected List<Node> nodeList = new ArrayList<>();

    public Problem(String fileName){
        readInputFromFile(fileName);
    }

    abstract void readInputFromFile(String fileName);

    /**
     * Warning! nodeList and n must be valid
     */
    protected void createAdjacencyMatrix(){
        int[][] matrix = new int[n][n];

        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                matrix[i][j] = Node.getWeight(nodeList.get(i), nodeList.get(j));
            }
        }

        this.adjacencyMatrix = new AdjacencyMatrix(this.n, matrix);
    }

    @Override
    public String toString() {
        String matrixString = "";

        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                matrixString += this.adjacencyMatrix.getMatrix()[i][j] + " ";
            }
            matrixString += '\n';
        }
        return "Problem{" +
                "maxLengthOfSequence=" + maxLengthOfSequence +
                ", nucleotideLength=" + nucleotideLength +
                ", errorsNumber=" + errorsNumber +
                ", n=" + n + "}\n"
                + matrixString;
    }

    public void markAsVisited(int i, int j, boolean[] visited, AdjacencyMatrix mat){
        visited[i] = true;
        visited[j] = true;
        for(int k = 0; k < n; k++) {
            mat.setRowSum(k, mat.getRowSum(k) - mat.getMatrix()[k][i]);
        }
        //System.out.println("Witam kurde" + mat.getRowSum(i));
    }

    public void solveProblem(){
        greedyAlgorithm();
    }


    public void greedyAlgorithm(){
        for(int i = 0; i < n; i++) {
            System.out.println(adjacencyMatrix.getRowSum(i));
        }
        List<Integer> indexesOfNodes = new ArrayList<>();
        boolean[] visited = new boolean[n];
        int[] rowSum = new int[n];
        // find max elem
        int maxIndexI = 0;
        int maxIndexJ = 0;
        for (int i = 0; i < this.n; i++){
            rowSum[i] = 0;
        }
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(adjacencyMatrix.getMatrix()[i][j] > adjacencyMatrix.getMatrix()[maxIndexI][maxIndexJ]){
                    maxIndexI = i;
                    maxIndexJ = j;
                }
                else if(adjacencyMatrix.getMatrix()[i][j] == adjacencyMatrix.getMatrix()[maxIndexI][maxIndexJ]){
                    if(adjacencyMatrix.getRowSum(i) < adjacencyMatrix.getRowSum(maxIndexI)){
                        maxIndexI = i;
                        maxIndexJ = j;
                    }
                }
            }
        }
        // start Hamilton with cell(i, j), so first connection is i -> j
        indexesOfNodes.add(maxIndexI);
        indexesOfNodes.add(maxIndexJ);
        markAsVisited(maxIndexI, maxIndexJ, visited, adjacencyMatrix);
        //markAsVisited(maxIndexJ, visited);
        //visited[maxIndexI] = true;
        //visited[maxIndexJ] = true;
        int lengthOfSequence = nucleotideLength + adjacencyMatrix.getMatrix()[maxIndexI][maxIndexJ];
        int currentNodeIndex = maxIndexJ;
        while(lengthOfSequence < maxLengthOfSequence){
            // find max from current Node
            int maxIndexNext = 0;
            for(int j = 0; j < n; j++){
                // must be >= because 0 index might be already visited
                //szefie czemu >= ???
                //if(adjacencyMatrix.getMatrix()[currentNodeIndex][j] >= adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext]
                //        && !visited[j]){
                if(adjacencyMatrix.getMatrix()[currentNodeIndex][j] > adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext]
                        && !visited[j]){
                    maxIndexNext = j;
                }
                else if(adjacencyMatrix.getMatrix()[currentNodeIndex][j] > adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext]
                        && !visited[j]){
                    if(adjacencyMatrix.getRowSum(j) < adjacencyMatrix.getRowSum(maxIndexNext)){
                        maxIndexNext = j;
                    }
                }
            }
            if(lengthOfSequence + nucleotideLength - adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext] <= maxLengthOfSequence) {
                //visited[maxIndexNext] = true;
                markAsVisited(currentNodeIndex, maxIndexNext, visited, adjacencyMatrix);
                indexesOfNodes.add(maxIndexNext);
                lengthOfSequence += nucleotideLength - adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext];
                currentNodeIndex = maxIndexNext;
            }
            else{
                break;
            }
        }

        System.out.println("Number of nucleotides in a seq: " + indexesOfNodes.size()
                + "/" + maxLengthOfSequence);
        System.out.println("Nodes and weights: ");
        for(int i = 0; i < indexesOfNodes.size() -  1; i++){
            System.out.println(i+1 + ". " + indexesOfNodes.get(i) + ": "
                    + adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(i+1)]);
        }
        for(int i = 0; i < n; i++) {
            System.out.println(adjacencyMatrix.getRowSum(i));
        }
    }
}
