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

    //this method does all the nessessary stuff when a row is visited
    public void markAsVisited(int i, int j, boolean[] visited, AdjacencyMatrix mat){
        visited[i] = true;
        visited[j] = true;
        // TODO check if i is a correct index in the loop below
        for(int k = 0; k < n; k++) {
            mat.setRowSum(k, mat.getRowSum(k) - mat.getMatrix()[k][i]);
        }
    }

    //here we use our final method to solve the problem
    public void solveProblem(){
        oldGreedyAlgorithm();
        //greedyAlgorithm();
        //TSP();
    }

    //method calculates distance in the modified matrix
    public int calculateDistance(int[][] matrix, int n, List<Integer> tab){
        // TODO check if it is correct
        int distance = matrix[tab.get(tab.size() - 1)][tab.get(0)];
        for (int i = 0; i < tab.size()-1; i++){
            distance += matrix[tab.get(i)][tab.get(i + 1)];
        }
        return distance;
    }

    //Traveling Salesman Problem
    public void TSP() {
        //tworzymy nowa macierz ktorej wartosci wynosza
        // nucleotideLength(w naszym przypadku 10) minus wartosc pola [i][j] macierzy oryginalnej,
        // aby moc minimalizowac zamiast maksymalizowac (bardziej intuicyjne wg mnie)
        int[][] newMat = new int[n][n];
        //String newMatrixString sluzy do debugowania mozna podejrzec sobie co jest w nowej macierzy,
        // a wszystkie linie ktore go dotycza mozna sobie zakomentowac lub odkomentowac
        String newMatrixString = "";
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                newMat[i][j] = nucleotideLength - adjacencyMatrix.getMatrix()[i][j];
                //newMatrixString += newMat[i][j] + " ";
            }
            //newMatrixString += '\n';
        }
        //System.out.println(newMatrixString);


        //wczytujemy rozwiazanie zachlanne jako start metody
        List<Integer> indexesOfNodes = greedyAlgorithm();


        /*
        //mozna tu podejrzec co zostalo wczytane przez algorytm zachlanny
        String TabString = "";
        for (int i = 0; i < indexesOfNodes.size(); i++){
            TabString+= indexesOfNodes.get(i) + " ";
        }
        System.out.println("Wczytalem: "+ TabString);
        */


        //obliczamy obecny dystans i przechowujemy go jako bestDistance
        int bestDistance = calculateDistance(newMat, n, indexesOfNodes);
        //System.out.println("bestDistance: " + bestDistance);


        //System.out.println("Tera jedziemy z zamianami kurde");
        Random generator = new Random();
        for (int i = 0; i < 1000; i++){  //liczbe iteracji petli mozna zmieniac
            //losujemy 2 pozycje w tablicy
            int first = generator.nextInt(n);
            int second = generator.nextInt(n);
            //zamieniamy elementy miejscami
            Integer tmp1 = indexesOfNodes.get(first);
            Integer tmp2 = indexesOfNodes.get(second);
            indexesOfNodes.set(first, tmp2);
            indexesOfNodes.set(second, tmp1);
            //liczymy dystans po zamianie
            int currentDistance = calculateDistance(newMat, n, indexesOfNodes);
            //jezeli jest krocej (moze byc < lub <=) zostawiamy i aktualizujemy bestDistance
            if (currentDistance <= bestDistance){
                bestDistance = currentDistance;
                System.out.println("lepiej " + i);
            }
            //w przeciwnym przypadku zamieniamy z powrotem
            else {
                indexesOfNodes.set(first, tmp1);
                indexesOfNodes.set(second, tmp2);
                //System.out.println("gorzej");
            }

        }


        /*
        //wartosci zmiennej bestDistance oraz obecnego dystansu powinny byc sobie rowne
        // mozna tez patrzec na ktoras z nich zeby podejrzec jak sie zmienila po przemieleniu calej petli
        System.out.println("best: " + bestDistance);
        System.out.println("prawdziwy: " + calculateDistance(newMat, n, indexesOfNodes));
        */


        //tera kurde blaszka jedziemy od poczatku i pokazujemy rozwiazanie
        int lengthOfSequence = nucleotideLength;
        //zaczynamy od dlugosci pelnego nukleotydu (10), a liczenie dodatkowych rzeczy zaczynamy od drugiego elementu tablicy
        for (int i = 1; i < indexesOfNodes.size()-1; i++) {
            if(lengthOfSequence +(nucleotideLength - adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(i + 1)]) <= maxLengthOfSequence){
                lengthOfSequence+= (nucleotideLength - adjacencyMatrix.getMatrix()[indexesOfNodes.get(i)][indexesOfNodes.get(i + 1)]);
                System.out.println(i + ". " + indexesOfNodes.get(i) + ": "
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

    //returns a list of all nodes ordered by greedy algorithm even if they don't fit into maxNuclotideLength
    public List<Integer> greedyAlgorithm(){
        List<Integer> indexesOfNodes = new ArrayList<>();
        boolean[] visited = new boolean[n];
        // find max elem
        int maxIndexI = 0;
        int maxIndexJ = 0;
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(adjacencyMatrix.getMatrix()[i][j] > adjacencyMatrix.getMatrix()[maxIndexI][maxIndexJ]){
                    maxIndexI = i;
                    maxIndexJ = j;
                }
                //with a minor improvement using rowSums
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
        //wg mnie to jest zle:
        // int lengthOfSequence = nucleotideLength + adjacencyMatrix.getMatrix()[maxIndexI][maxIndexJ];
        //zrobilnym tak:
        int lengthOfSequence = nucleotideLength + (nucleotideLength - adjacencyMatrix.getMatrix()[maxIndexI][maxIndexJ]);
        int currentNodeIndex = maxIndexJ;
        //while(lengthOfSequence < maxLengthOfSequence)
        // to bylo w rozwiazaiu zwyklym ale tu interesuja nas nawet te rzeczy ktore sie nie mieszcza zeby potem je przekazac do TSP
        // zamiast tego lecimy dopoki wszystkie nie zostana odwiedzone
        while (!isNotVisited(visited))
        {
            // find max from current Node
            int maxIndexNext = 0;
            for(int j = 0; j < n; j++){
                if(adjacencyMatrix.getMatrix()[currentNodeIndex][j] > adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext]
                        && !visited[j]){
                    maxIndexNext = j;
                }
                else if(adjacencyMatrix.getMatrix()[currentNodeIndex][j] == adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext]
                        && !visited[j]){
                    if(adjacencyMatrix.getRowSum(j) < adjacencyMatrix.getRowSum(maxIndexNext)){
                        maxIndexNext = j;
                    }
                }
            }
            markAsVisited(currentNodeIndex, maxIndexNext, visited, adjacencyMatrix);
            indexesOfNodes.add(maxIndexNext);
            currentNodeIndex = maxIndexNext;
        }


        //rzeczy do wypisywania, tutaj nie sa potrzebne ale nie bede ich psul
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
        List<Integer> indexesOfNodes = new ArrayList<>();
        boolean[] visited = new boolean[n];
        // find max elem
        int maxIndexI = 0;
        int maxIndexJ = 0;
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
        //wg mnie to jest zle:
        int lengthOfSequence = nucleotideLength + adjacencyMatrix.getMatrix()[maxIndexI][maxIndexJ];
        // zrobilnym tak:
        // int lengthOfSequence = nucleotideLength + (nucleotideLength - adjacencyMatrix.getMatrix()[maxIndexI][maxIndexJ]);
        int currentNodeIndex = maxIndexJ;
        while(lengthOfSequence < maxLengthOfSequence){
            // find max from current Node
            int maxIndexNext = 0;
            for(int j = 0; j < n; j++){
                // must be >= because 0 index might be already visited
                if(adjacencyMatrix.getMatrix()[currentNodeIndex][j] >= adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext]
                        && !visited[j]){
                    maxIndexNext = j;
                }
            }

            if(lengthOfSequence + nucleotideLength - adjacencyMatrix.getMatrix()[currentNodeIndex][maxIndexNext] <= maxLengthOfSequence) {
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
    }
}
