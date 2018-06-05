import java.nio.file.ProviderNotFoundException;

public class Main {

    public static void main(String[] args) {


        for(int i = 0; i < 24; i++){
            String filename = "neg" + i + ".txt";
            Problem negProblem = new Problem(filename);
            negProblem.solveProblem();
        }
        for(int i = 0; i < 5; i++){
            String filename = "negRepeat" + i + ".txt";
            Problem negProblem = new Problem(filename);
            negProblem.solveProblem();
        }

        for(int i = 0; i < 12; i++){
            String filename = "pos" + i + ".txt";
            Problem posProblem = new Problem(filename);
            posProblem.solveProblem();
        }

        for(int i = 0; i < 12; i++){
            String filename = "posend" + i + ".txt";
            Problem posProblem = new Problem(filename);
            posProblem.solveProblem();
        }


        /*ProblemGenerator pg = new ProblemGenerator();
        pg.generateProblem("asd", 10, 200, 30, 30);*/

        /*
        String filename = "negRepeat0.txt";
        Problem negProblem = new Problem(filename);
        System.out.println(negProblem);
        */
    }
}
