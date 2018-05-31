import java.util.ArrayList;
import java.util.List;

public abstract class Problem {
    protected int maxLengthOfSequence;
    protected int nucleotideLength;
    protected int errorsNumber; // neg/ pos errors
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

    public void solveProblem(){
        greedyAlgorithm();
    }


    public void greedyAlgorithm(){
        List<Integer> indexesOfNodes = new ArrayList<>();
        boolean[] visited = new boolean[n];
        // find max elem
        int maxIndexI = 0;
        int maxIndexJ = 0;
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(adjacencyMatrix.getMatrix()[i][j] >= adjacencyMatrix.getMatrix()[maxIndexI][maxIndexJ]){
                    maxIndexI = i;
                    maxIndexJ = j;
                }
            }
        }
        // start Hamilton with cell(i, j), so first connection is i -> j
        indexesOfNodes.add(maxIndexI);
        indexesOfNodes.add(maxIndexJ);
        visited[maxIndexI] = true;
        visited[maxIndexJ] = true;
        int lengthOfSequence = 2* nucleotideLength - adjacencyMatrix.getMatrix()[maxIndexI][maxIndexJ];
        int currentNodeIndex = maxIndexJ;
        while(lengthOfSequence < maxLengthOfSequence){
            currentNodeIndex = indexesOfNodes.get(indexesOfNodes.size()-1);

            // init first index for comparing
            int maxIndexNext = 0;
            for(int j =0 ; j < n; j++){
                if(!visited[j]){
                    maxIndexNext = j;
                    break;
                }
            }
            // find max from current Node
            for(int j = 0; j < n; j++){
                if(adjacencyMatrix.getMatrix()[currentNodeIndex][j] > adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext]
                        && !visited[j]){
                    maxIndexNext = j;
                }
            }

            // NEW- find max from the start
            // init first index for comparing
            int maxIndexNextBeginning = 0;
            for(int j =0 ; j < n; j++){
                if(!visited[j]){
                    maxIndexNextBeginning = j;
                    break;
                }
            }
            for(int j = 0; j < n; j++){
                if(adjacencyMatrix.getMatrix()[j][indexesOfNodes.get(0)] > adjacencyMatrix.getMatrix()[maxIndexNextBeginning][indexesOfNodes.get(0)]
                        && !visited[j]){
                    maxIndexNextBeginning = j;
                }

            }
            boolean shouldBreak = false;
            if(adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext]
               >= adjacencyMatrix.getMatrix()[maxIndexNextBeginning][indexesOfNodes.get(0)]) {

                if(lengthOfSequence + nucleotideLength - adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext] <= maxLengthOfSequence) {
                    visited[maxIndexNext] = true;
                    indexesOfNodes.add(maxIndexNext);
                    lengthOfSequence += nucleotideLength - adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext];
                    currentNodeIndex = maxIndexNext;
                }
                else {
                    shouldBreak = true;
                }
            } else{
                System.out.println("Hello " + adjacencyMatrix.getMatrix()[maxIndexNextBeginning][indexesOfNodes.get(0)]);
                if(lengthOfSequence + nucleotideLength - adjacencyMatrix.getMatrix()[maxIndexNextBeginning][indexesOfNodes.get(0)] <= maxLengthOfSequence) {
                    visited[maxIndexNextBeginning] = true;
                    lengthOfSequence += nucleotideLength - adjacencyMatrix.getMatrix()[maxIndexNextBeginning][indexesOfNodes.get(0)];
                    indexesOfNodes.add(0, maxIndexNextBeginning);
                }
                else {
                    shouldBreak = true;
                }
            }
            if(shouldBreak){
                break;
            }


        }
        int optimumNumberOfNucleotides = this.n;


        System.out.println("Nodes and weights: ");
        for(int i = 0; i < indexesOfNodes.size() -  1; i++){
            System.out.println(i+1 + ". " + indexesOfNodes.get(i) + ": "
                    + adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(i+1)]);
        }

        System.out.println("Number of nucleotides in a seq: " + indexesOfNodes.size()
                + "/" + optimumNumberOfNucleotides);
    }
}
