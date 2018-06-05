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

        standardAlgorithm();
        List<Integer> indexesOfNodes1 = new ArrayList<>(indexesOfNodes);
        rowSumMin();
        List<Integer> indexesOfNodes2 = new ArrayList<>(indexesOfNodes);
        rowSumMax();
        List<Integer> indexesOfNodes3 = new ArrayList<>(indexesOfNodes);


        List<Integer> best = indexesOfNodes1;

        if(indexesOfNodes2.size() > best.size()){
            best = indexesOfNodes2;
        }
        if(indexesOfNodes3.size() > best.size()){
            best = indexesOfNodes3;
        }

        System.out.println(best.size()+ "/" + getOptimum());
        //standardAlgorithm2();
       // List<Integer> indexesOfNodes4 = new ArrayList<>(indexesOfNodes);
        //System.out.println(indexesOfNodes1.size() + ";"+ indexesOfNodes2.size() + ";" + indexesOfNodes3.size() +";" + indexesOfNodes4.size() + ";" + getOptimum());
        //System.out.println(indexesOfNodes1.size() + ";"+ indexesOfNodes2.size() + ";" + indexesOfNodes3.size() + ";" + getOptimum());

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

    private int rowSumAfterMax(int index){
        int sum = 0;
        for(int i = 0; i < n; i++) {
            if (!visited[i] && i != index) {
                sum += (nucleotideLength - adjacencyMatrix.getMatrix()[index][i])*(nucleotideLength - adjacencyMatrix.getMatrix()[index][i]);
            }
        }
        return sum;
    }

    private int rowSumBeforeMax(int index){
        int sum = 0;
        for(int i = 0; i < n; i++) {
            if (!visited[i] && i != index) {
                sum += (nucleotideLength - adjacencyMatrix.getMatrix()[i][index])*(nucleotideLength - adjacencyMatrix.getMatrix()[i][index]);
            }
        }
        return sum;
    }

    private int rowSumAfterMin(int index){
        int sum = 0;
        for(int i = 0; i < n; i++) {
            if (!visited[i] && i != index) {
                sum += adjacencyMatrix.getMatrix()[index][i]*adjacencyMatrix.getMatrix()[index][i];
            }
        }
        return sum;
    }

    private int rowSumBeforeMin(int index){
        int sum = 0;
        for(int i = 0; i < n; i++) {
            if (!visited[i] && i != index) {
                sum += adjacencyMatrix.getMatrix()[i][index]*nucleotideLength - adjacencyMatrix.getMatrix()[i][index];
            }
        }
        return sum;
    }

    public void tryToAddToSeqMax(){
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
                    if(rowSumAfterMax(j) > rowSumAfterMax(minIndexNext)){
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
                    if(rowSumBeforeMax(j) > rowSumBeforeMax(minIndexNextBeginning)){
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
                }
                else {
                    shouldBreak = true;
                }
            } else{
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

    public void tryToAddToSeqMin(){
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
                    if(rowSumAfterMin(j) < rowSumAfterMin(minIndexNext)){
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
                    if(rowSumBeforeMin(j) < rowSumBeforeMin(minIndexNextBeginning)){
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
                }
                else {
                    shouldBreak = true;
                }
            } else{
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

    public void tryToAddToSeqStandard(){
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
                }

            }
            boolean shouldBreak = false;
            if(adjacencyMatrix.getMatrix()[currentNodeIndex][minIndexNext]
                    <= adjacencyMatrix.getMatrix()[minIndexNextBeginning][indexesOfNodes.get(0)]) {

                if(lengthOfSequence + adjacencyMatrix.getMatrix()[currentNodeIndex][minIndexNext] <= maxLengthOfSequence) {
                    visited[minIndexNext] = true;
                    indexesOfNodes.add(minIndexNext);
                    lengthOfSequence += adjacencyMatrix.getMatrix()[currentNodeIndex][minIndexNext];
                }
                else {
                    shouldBreak = true;
                }
            } else{
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

    public void tryToAddToSeqStandard2(){
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
                if(adjacencyMatrix.getMatrix()[currentNodeIndex][j] <= adjacencyMatrix.getMatrix()[currentNodeIndex][minIndexNext]
                        && !visited[j]){
                    minIndexNext = j;
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
                if(adjacencyMatrix.getMatrix()[j][indexesOfNodes.get(0)] <= adjacencyMatrix.getMatrix()[minIndexNextBeginning][indexesOfNodes.get(0)]
                        && !visited[j]){
                    minIndexNextBeginning = j;
                }

            }
            boolean shouldBreak = false;
            if(adjacencyMatrix.getMatrix()[currentNodeIndex][minIndexNext]
                    <= adjacencyMatrix.getMatrix()[minIndexNextBeginning][indexesOfNodes.get(0)]) {

                if(lengthOfSequence + adjacencyMatrix.getMatrix()[currentNodeIndex][minIndexNext] <= maxLengthOfSequence) {
                    visited[minIndexNext] = true;
                    indexesOfNodes.add(minIndexNext);
                    lengthOfSequence += adjacencyMatrix.getMatrix()[currentNodeIndex][minIndexNext];
                }
                else {
                    shouldBreak = true;
                }
            } else{
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

    public void rowSumMax(){
        initGreedy();
        tryToAddToSeqMax();
        swap();
        //System.out.println("\n\n");
        int optimumNumberOfNucleotides = this.n;

        /*System.out.println("Nodes and weights: ");
        for(int i = 0; i < indexesOfNodes.size() -  1; i++){
            System.out.println(i+1 + ". " + indexesOfNodes.get(i) + ": "
                    + adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(i+1)]);
        }*/

        //System.out.println(indexesOfNodes.size() + "/" + getOptimum());
    }
    private void rowSumMin(){
        initGreedy();
        tryToAddToSeqMin();
    }


    private void standardAlgorithm(){
        initGreedy();
        tryToAddToSeqStandard();
    }

    private void standardAlgorithm2(){
        initGreedy();
        tryToAddToSeqStandard2();
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
