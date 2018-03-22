import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class NegProblem extends Problem {

    public NegProblem(String fileName){
        super(fileName);
    }
    @Override
    void readInputFromFile(String fileName) {
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

        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
