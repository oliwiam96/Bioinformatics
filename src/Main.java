public class Main {

    public static void main(String[] args) {
        String filename = "pos" + 1 + ".txt";
        Problem negProblem = new NegProblem(filename);
        negProblem.solveProblem();

        /*for(int i = 1; i < 24; i++){
            String filename = "neg" + i + ".txt";
            Problem negProblem = new NegProblem(filename);
            negProblem.solveProblem();
        }
        for(int i = 0; i < 5; i++){
            String filename = "negRepeat" + i + ".txt";
            Problem negProblem = new NegProblem(filename);
            negProblem.solveProblem();
        }
        for(int i = 0; i < 12; i++){
            String filename = "pos" + i + ".txt";
            Problem posProblem = new NegProblem(filename);
            posProblem.solveProblem();
        }
        for(int i = 0; i < 12; i++){
            String filename = "posend" + i + ".txt";
            Problem posProblem = new NegProblem(filename);
            posProblem.solveProblem();
        }*/
    }
}
