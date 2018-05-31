import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Problem {
    protected int maxLengthOfSequence;
    protected int nucleotideLength;
    protected int errorsNumber; // neg/ pos errors
    protected int n; //matrix size (n x n)
    protected AdjacencyMatrix adjacencyMatrix;
    protected List<Node> nodeList = new ArrayList<>();

    private boolean isPositive = false;

    // variables for algorithm
    private List<Integer> indexesOfNodes = new ArrayList<>();
    private boolean[] visited = new boolean[n];
    private int lengthOfSequence;


    public Problem(String fileName){
        readInputFromFile(fileName);
    }

    void readInputFromFile(String fileName) {
        isPositive = fileName.startsWith("pos");

        try {
            Scanner in = new Scanner(new FileReader("examples/" + fileName));
            this.maxLengthOfSequence = in.nextInt();
            this.nucleotideLength = in.nextInt();
            this.errorsNumber = in.nextInt();
            this.n = in.nextInt();

            //System.out.println("max: "+ maxLengthOfSequence + " l: " + nucleotideLength + " err: " + errorsNumber + " n: " +n);
            in.nextLine();
            for (int i = 0; i < n; i++) {
                String line = in.nextLine();
                //System.out.println(line);
                Node node = new Node(line);
                nodeList.add(node);
            }

            this.createAdjacencyMatrix();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

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


    public void initGreedy(){
        indexesOfNodes = new ArrayList<>();
        visited = new boolean[n];
        // find max elem
        int minIndexI = 0;
        int minIndexJ = 0;
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(adjacencyMatrix.getMatrix()[i][j] <= adjacencyMatrix.getMatrix()[minIndexI][minIndexJ]){
                    minIndexI = i;
                    minIndexJ = j;
                }
            }
        }
        // start Hamilton with cell(i, j), so first connection is i -> j
        indexesOfNodes.add(minIndexI);
        indexesOfNodes.add(minIndexJ);
        visited[minIndexI] = true;
        visited[minIndexJ] = true;
        lengthOfSequence = nucleotideLength + adjacencyMatrix.getMatrix()[minIndexI][minIndexJ];

    }

    private int rowSumAfter(int index){
        int sum = 0;
        for(int i = 0; i < n; i++) {
            if (!visited[i] && i != index) {
                sum += (nucleotideLength - adjacencyMatrix.getMatrix()[index][i])*(nucleotideLength - adjacencyMatrix.getMatrix()[index][i]);
            }
        }
        return sum;
    }

    private int rowSumBefore(int index){
        int sum = 0;
        for(int i = 0; i < n; i++) {
            if (!visited[i] && i != index) {
                sum += (nucleotideLength - adjacencyMatrix.getMatrix()[i][index])*(nucleotideLength - adjacencyMatrix.getMatrix()[i][index]);
            }
        }
        return sum;
    }

    public void tryToAddToSeq(){
        int currentNodeIndex;
        while(lengthOfSequence < maxLengthOfSequence){
            currentNodeIndex = indexesOfNodes.get(indexesOfNodes.size()-1);

            // init first index for comparing
            int minIndexNext = 0;
            for(int j =0 ; j < n; j++){
                if(!visited[j]){
                    minIndexNext = j;
                    break;
                }
            }
            // find min from current Node
            for(int j = 0; j < n; j++){
                if(adjacencyMatrix.getMatrix()[currentNodeIndex][j] < adjacencyMatrix.getMatrix()[currentNodeIndex][minIndexNext]
                        && !visited[j]){
                    minIndexNext = j;
                } else if(adjacencyMatrix.getMatrix()[currentNodeIndex][j] == adjacencyMatrix.getMatrix()[currentNodeIndex][minIndexNext]){
                    if(rowSumAfter(j) > rowSumAfter(minIndexNext)){
                        minIndexNext = j;
                    }
                }
            }

            // NEW- find min from the start
            // init first index for comparing
            int minIndexNextBeginning = 0;
            for(int j =0 ; j < n; j++){
                if(!visited[j]){
                    minIndexNextBeginning = j;
                    break;
                }
            }
            for(int j = 0; j < n; j++){
                if(adjacencyMatrix.getMatrix()[j][indexesOfNodes.get(0)] < adjacencyMatrix.getMatrix()[minIndexNextBeginning][indexesOfNodes.get(0)]
                        && !visited[j]){
                    minIndexNextBeginning = j;
                } else if(adjacencyMatrix.getMatrix()[j][indexesOfNodes.get(0)] < adjacencyMatrix.getMatrix()[minIndexNextBeginning][indexesOfNodes.get(0)]){
                    if(rowSumBefore(j) > rowSumBefore(minIndexNextBeginning)){
                        minIndexNextBeginning = j;
                    }
                }

            }
            boolean shouldBreak = false;
            if(adjacencyMatrix.getMatrix()[currentNodeIndex][minIndexNext]
                    <= adjacencyMatrix.getMatrix()[minIndexNextBeginning][indexesOfNodes.get(0)]) {

                if(lengthOfSequence + adjacencyMatrix.getMatrix()[currentNodeIndex][minIndexNext] <= maxLengthOfSequence) {
                    visited[minIndexNext] = true;
                    indexesOfNodes.add(minIndexNext);
                    lengthOfSequence += adjacencyMatrix.getMatrix()[currentNodeIndex][minIndexNext];
                    currentNodeIndex = minIndexNext;
                }
                else {
                    shouldBreak = true;
                }
            } else{
                //System.out.println("Hello " + adjacencyMatrix.getMatrix()[maxIndexNextBeginning][indexesOfNodes.get(0)]);
                if(lengthOfSequence + adjacencyMatrix.getMatrix()[minIndexNextBeginning][indexesOfNodes.get(0)] <= maxLengthOfSequence) {
                    visited[minIndexNextBeginning] = true;
                    lengthOfSequence += adjacencyMatrix.getMatrix()[minIndexNextBeginning][indexesOfNodes.get(0)];
                    indexesOfNodes.add(0, minIndexNextBeginning);
                }
                else {
                    shouldBreak = true;
                }
            }
            if(shouldBreak){
                break;
            }
        }

    }

    public void greedyAlgorithm(){
        initGreedy();
        tryToAddToSeq();
        swap();
        //System.out.println("\n\n");
        int optimumNumberOfNucleotides = this.n;

        /*System.out.println("Nodes and weights: ");
        for(int i = 0; i < indexesOfNodes.size() -  1; i++){
            System.out.println(i+1 + ". " + indexesOfNodes.get(i) + ": "
                    + adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(i+1)]);
        }*/

        System.out.println(indexesOfNodes.size()
                + "/" + getOptimum());
    }

    /**
     *
     * @param idx1
     * @param idx2
     * @return difference between distances before and after swap idx1 and idx2;
     * positive -> better
     * negative -> worse
     */
    public int calcDistDiff(int idx1, int idx2)
    {
        int currentDistance = adjacencyMatrix.getMatrix()[indexesOfNodes.get(idx1-1)][indexesOfNodes.get(idx1)] +
                adjacencyMatrix.getMatrix()[indexesOfNodes.get(idx1)][indexesOfNodes.get(idx1+1)] +
                adjacencyMatrix.getMatrix()[indexesOfNodes.get(idx2-1)][indexesOfNodes.get(idx2)] +
                adjacencyMatrix.getMatrix()[indexesOfNodes.get(idx2)][indexesOfNodes.get(idx2+1)];

        int newDistance = adjacencyMatrix.getMatrix()[indexesOfNodes.get(idx1-1)][indexesOfNodes.get(idx2)] +
                adjacencyMatrix.getMatrix()[indexesOfNodes.get(idx2)][indexesOfNodes.get(idx1+1)] +
                adjacencyMatrix.getMatrix()[indexesOfNodes.get(idx2-1)][indexesOfNodes.get(idx1)] +
                adjacencyMatrix.getMatrix()[indexesOfNodes.get(idx1)][indexesOfNodes.get(idx2+1)];

        //if(currentDistance - newDistance > -10)
            //System.out.println(currentDistance - newDistance);

        return currentDistance - newDistance;
    }

    public void swap(){
        for (int i = 1; i < indexesOfNodes.size()-1; i++) {
            for (int j = i+1; j < indexesOfNodes.size()-1; j++) {
                if (calcDistDiff(i, j) > 0){
                    System.out.println("Dokonano zamiany");
                    Integer tmp1 = indexesOfNodes.get(i);
                    Integer tmp2 = indexesOfNodes.get(j);
                    indexesOfNodes.set(i, tmp2);
                    indexesOfNodes.set(j, tmp1);
                }
            }
        }
    }
    private int getOptimum(){
        if(isPositive){
            return n - errorsNumber;
        } else{
            return n;
        }
    }
}
