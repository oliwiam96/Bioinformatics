public class Main {

    public static void main(String[] args) {

        for(int i = 0; i < 24; i++){
            String filename = "neg" + i + ".txt";
            Problem negProblem = new Problem(filename);
        }
        for(int i = 0; i < 5; i++){
            String filename = "negRepeat" + i + ".txt";
            Problem negProblem = new Problem(filename);
        }

        //negProblem.solveProblem();
    }
}
