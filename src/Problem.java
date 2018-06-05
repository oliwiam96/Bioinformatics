import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TransferQueue;

import static java.lang.Integer.bitCount;
import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class Problem {
    protected int maxLengthOfSequence;
    protected int nucleotideLength;
    protected int errorsNumber; // neg/ pos errors
    protected int n; //matrix size (n x n)
    protected AdjacencyMatrix adjacencyMatrix;
    protected List<Node> nodeList = new ArrayList<>();

    private boolean isPositive = false;
    private boolean isOurProblem = false;


    // variables for algorithm
    private List<Integer> indexesOfNodes = new ArrayList<>();
    private boolean[] visited = new boolean[n];
    private int lengthOfSequence;


    public Problem(String fileName){
        readInputFromFile(fileName);
    }

    void readInputFromFile(String fileName) {
        isPositive = fileName.startsWith("pos");
        isOurProblem = fileName.startsWith("our");

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

        int before = 0;
        int after = 0;

        standardAlgorithm();
        List<Integer> indexesOfNodes1 = new ArrayList<>(indexesOfNodes);

        rowSumMin();
        List<Integer> indexesOfNodes2 = new ArrayList<>(indexesOfNodes);

        rowSumMax();
        List<Integer> indexesOfNodes3 = new ArrayList<>(indexesOfNodes);


        List<Integer> best;
        if(indexesOfNodes1.size() >= indexesOfNodes2.size() && indexesOfNodes1.size() >= indexesOfNodes3.size()){
            // 1 is best
            best = indexesOfNodes1;
            /*standardAlgorithm();
            before = indexesOfNodes.size();
            swapSubSeq();
            //tryToAddToSeqStandard();
            after = indexesOfNodes.size();
            isSolutionCorrect();*/

        } else if(indexesOfNodes2.size() >= indexesOfNodes1.size() && indexesOfNodes2.size() >= indexesOfNodes3.size()){
            // 2 is best
            best = indexesOfNodes2;
            /*rowSumMin();
            before = indexesOfNodes.size();
            swapSubSeq();
            //tryToAddToSeqMin();
            after = indexesOfNodes.size();
            isSolutionCorrect();*/
        } else{
            // 3 is best
            best = indexesOfNodes3;
            /*rowSumMax();
            before = indexesOfNodes.size();
            swapSubSeq();
            //tryToAddToSeqMax();
            after = indexesOfNodes.size();
            isSolutionCorrect();*/

        }
        /*if(after > before){
            System.out.println("sucess! before: " + before + "after: " + after);
        }*/

        //System.out.println(best.size()+ "/" + getOptimum() + "better: " + indexesOfNodes.size());
        //standardAlgorithm2();
       // List<Integer> indexesOfNodes4 = new ArrayList<>(indexesOfNodes);
        //System.out.println(indexesOfNodes1.size() + ";"+ indexesOfNodes2.size() + ";" + indexesOfNodes3.size() +";" + indexesOfNodes4.size() + ";" + getOptimum());
       System.out.println(indexesOfNodes1.size() + ";"+ indexesOfNodes2.size() + ";" + indexesOfNodes3.size() + ";" + getOptimum());

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
                } else if(adjacencyMatrix.getMatrix()[j][indexesOfNodes.get(0)] == adjacencyMatrix.getMatrix()[minIndexNextBeginning][indexesOfNodes.get(0)]){
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
                } else if(adjacencyMatrix.getMatrix()[j][indexesOfNodes.get(0)] == adjacencyMatrix.getMatrix()[minIndexNextBeginning][indexesOfNodes.get(0)]){
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

    private void swapSubSeq(){
        int maxIndex1 = 0; // we don't like big weights (0,1)
        int maxIndex2 = 1; // (1,2)
        for(int i = 1; i <indexesOfNodes.size() - 2; i++){
            if(adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(i+1)] >=
                    adjacencyMatrix.getMatrix()[indexesOfNodes.get(maxIndex1)][indexesOfNodes.get(maxIndex1+1)]){
                maxIndex1 = i;
            }
        }
        for(int i = 1; i <indexesOfNodes.size() - 2; i++){
            if(i != maxIndex1) {
                if (adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(i + 1)] >=
                        adjacencyMatrix.getMatrix()[indexesOfNodes.get(maxIndex2)][indexesOfNodes.get(maxIndex2 + 1)]) {
                    maxIndex2 = i;
                }
            }
        }
        int first = min(maxIndex1, maxIndex2);
        int second = max(maxIndex1, maxIndex2);
        int initCost = adjacencyMatrix.getMatrix()[indexesOfNodes.get(first)][indexesOfNodes.get(first+1)]
                     + adjacencyMatrix.getMatrix()[indexesOfNodes.get(second)][indexesOfNodes.get(second + 1)];
        //System.out.println("init cost: " + initCost);
        if(second - first > 1){
            int myFavIndex = -1;
            int myFavInsertCost = 1000;
            int linkCost = adjacencyMatrix.getMatrix()[indexesOfNodes.get(first)][indexesOfNodes.get(second)];
            //System.out.println("Link cost: " + linkCost);
            for(int i = 0; i < first - 1; i++){
                int insertCost = adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(first)]
                        + adjacencyMatrix.getMatrix()[indexesOfNodes.get(second)][indexesOfNodes.get(i+1)]
                        - adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(i+1)];
                if(insertCost + linkCost < initCost){
                    System.out.println("HURRRAY!!!" + (initCost - insertCost + linkCost));
                    if(insertCost < myFavInsertCost){
                        myFavIndex = i;
                        myFavInsertCost = insertCost;
                    }
                }
            }

            for(int i = second+1; i < indexesOfNodes.size() - 1; i++){
                int insertCost = adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(first)]
                        + adjacencyMatrix.getMatrix()[indexesOfNodes.get(second)][indexesOfNodes.get(i+1)]
                        - adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(i+1)];
                if(insertCost + linkCost < initCost){
                    System.out.println("HURRRAY!!!" + (initCost - insertCost + linkCost));
                    if(insertCost < myFavInsertCost){
                        myFavIndex = i;
                        myFavInsertCost = insertCost;
                    }
                }
            }

            if(myFavIndex > -1){
                // paste subsequence from (first, second) between (myFavIndex, myFavIndex+1)
                List<Integer> firstSeq = new ArrayList<>(indexesOfNodes.subList(0, first));
                List<Integer> subSeq = new ArrayList<>(indexesOfNodes.subList(first, second));
                List<Integer> secondSeq = new ArrayList<>(indexesOfNodes.subList(second, indexesOfNodes.size()));

                firstSeq.addAll(secondSeq);
                //firstSeq = new ArrayList<>(firstSeq);
                //List<Integer> subSeq = new ArrayList<>(subSeq);

                if(myFavIndex < first){
                    firstSeq.addAll(myFavIndex, subSeq);
                } else{
                    // myFavIndex > second
                    firstSeq.addAll(myFavIndex - subSeq.size(), subSeq);
                }
                System.out.println(firstSeq.size() + " " + indexesOfNodes.size());
                System.out.println("first: "+ first + "second: "+ second + "favInd: "+ myFavIndex);
                indexesOfNodes = firstSeq;
                //System.out.println("here");
            }
        }


    }
    private int getOptimum(){
        if(isPositive){
            return n - errorsNumber;
        } else if(isOurProblem){
            return errorsNumber;
        }
        else{
            return n;
        }
    }

    private boolean isSolutionCorrect(){
        int myLength = nucleotideLength;
        for(int i = 0; i < indexesOfNodes.size() - 1; i++){
            myLength += adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(i+1)];
        }
        //System.out.println("Length: "+  myLength + "/" + maxLengthOfSequence);
        if(myLength > maxLengthOfSequence){
            System.out.println("ERROR!!!");
        }
        return myLength <= maxLengthOfSequence;
    }
}
