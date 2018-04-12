import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    }

    public void solveProblem(){
        oldGreedyAlgorithm();

        //TSP();
    }

    public int calculateDistance(int[][] matrix, int n, List<Integer> tab){
        int distance = matrix[tab.get(tab.size() - 1)][tab.get(0)];
        //System.out.println("halo dzik");
        for (int i = 0; i < tab.size()-1; i++){
            distance += matrix[tab.get(i)][tab.get(i + 1)];
        }
        return distance;
    }

    public void TSP() {
        //tworzymy nowa macierz ktorej wartosci wynosza 10 - [i][j] macierzy oryginalnej, aby moc minimalizowac
        int[][] newMat = new int[n][n];
        String newMatrixString = "";
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                newMat[i][j] = nucleotideLength - adjacencyMatrix.getMatrix()[i][j];
                newMatrixString += newMat[i][j] + " ";
            }
            newMatrixString += '\n';
        }
        System.out.println(newMatrixString);
        Random generator = new Random();
        //wczytujemy rozwiazanie zachlanne jako start metody
        List<Integer> indexesOfNodes = greedyAlgorithm();


        /*
        //random route
        Random generator = new Random();
        List<Integer> indexesOfNodes = new ArrayList<>();
        newMatrixString = "";
        while(indexesOfNodes.size() < n) {
            int next = generator.nextInt(n);

            if(!indexesOfNodes.contains(next)){
                indexesOfNodes.add(next);
                System.out.println(next);
            }
            else{
                System.out.println("było: " + next);
            }

        }
        */
        newMatrixString = "";
        for (int i = 0; i < indexesOfNodes.size(); i++){
            newMatrixString+= indexesOfNodes.get(i) + " ";
        }

        System.out.println("Wczytalem: "+ newMatrixString);
        int bestDistance = calculateDistance(newMat, n, indexesOfNodes);

        System.out.println("Dystans: " + bestDistance);
        System.out.println("Tera jedziemy z zamianami kurde");
        for (int i = 0; i < 1000000; i++){
            int first = generator.nextInt(n);
            int afterFirst;
            if (first == n-1){
                afterFirst = 0;
            }
            else{
                afterFirst = first + 1;
            }
            int second = generator.nextInt(n);
            int afterSecond;
            if (second == n-1){
                afterSecond = 0;
            }
            else{
                afterSecond = first + 1;
            }
            Integer tmp1 = indexesOfNodes.get(first);
            Integer tmp2 = indexesOfNodes.get(second);
            indexesOfNodes.set(first, tmp2);
            indexesOfNodes.set(second, tmp1);
            int currentDistance = calculateDistance(newMat, n, indexesOfNodes);
            if (currentDistance <= bestDistance){
                bestDistance = currentDistance;
                System.out.println("lepiej" + i);
            }
            else {
                indexesOfNodes.set(first, tmp1);
                indexesOfNodes.set(second, tmp2);
                //System.out.println("gorzej");
            }

        }
        System.out.println("best: " + bestDistance);
        System.out.println("prawdziwy: " + calculateDistance(newMat, n, indexesOfNodes));
        //tera kurde blaszka jedziemy od poczatku i pokazujemy rozwiazanie
        int lengthOfSequence = 0;
        int it = 0;
        for (int i = 0; i < indexesOfNodes.size()-1; i++) {
            //lengthOfSequence += nucleotideLength - adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext];
            if(lengthOfSequence +(nucleotideLength - adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(i + 1)]) <= maxLengthOfSequence - 9){
                lengthOfSequence+= (nucleotideLength - adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(i + 1)]);
                System.out.println(lengthOfSequence);
                System.out.println(i+1 + ".asd " + indexesOfNodes.get(i) + ": "
                        + adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(i+1)]);
            }
            else{
                break;
            }
        }


    }
    public boolean isNotVisited(boolean[] tab){
        for (boolean v: tab) {
            if(v == false){
                return false;
            }
        }
        return true;
    }


    public List<Integer> greedyAlgorithm(){
        //for(int i = 0; i < n; i++) {
        //    System.out.println(adjacencyMatrix.getRowSum(i));
        //}
        List<Integer> indexesOfNodes = new ArrayList<>();
        boolean[] visited = new boolean[n];
        System.out.println(n + "kurdeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
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
        //while(lengthOfSequence < maxLengthOfSequence)
        while (!isNotVisited(visited))
        {
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
            //if(lengthOfSequence + nucleotideLength - adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext] <= maxLengthOfSequence)
            {
                //visited[maxIndexNext] = true;
                markAsVisited(currentNodeIndex, maxIndexNext, visited, adjacencyMatrix);
                indexesOfNodes.add(maxIndexNext);
                lengthOfSequence += nucleotideLength - adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext];
                currentNodeIndex = maxIndexNext;
            }
            //else{
            //   break;
            //}
        }

        System.out.println("Number of nucleotides in a seq: " + indexesOfNodes.size()
                + "/" + maxLengthOfSequence);
        System.out.println("Nodes and weights: ");
        for(int i = 0; i < indexesOfNodes.size() -  1; i++){
            System.out.println(i+1 + ". " + indexesOfNodes.get(i) + ": "
                    + adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(i+1)]);
        }
        String ind = "";
        for (int i = 0; i < indexesOfNodes.size(); i++) {
            ind = ind + indexesOfNodes.get(i) + " ";
        }
        System.out.println("Greedy: " + ind);
        return indexesOfNodes;
    }

    public void oldGreedyAlgorithm(){
        //for(int i = 0; i < n; i++) {
        //    System.out.println(adjacencyMatrix.getRowSum(i));
        //}
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
        int pom = 1;
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
                System.out.println(pom + " " + lengthOfSequence + " " + adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext]);
                pom++;
                //kurde blaszka Oli ktoś z nas źle coś liczy kurde bela bo Ty masz 143 a ja 155
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
        //for(int i = 0; i < n; i++) {
        //    System.out.println(adjacencyMatrix.getRowSum(i));
        //}
        //return indexesOfNodes;
    }
}
