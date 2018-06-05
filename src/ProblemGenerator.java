import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ProblemGenerator {

    public enum Nucleotides{
        A, C, G, T;
    }
    public String problemStr = "";
    public List<String> strList = new ArrayList<>();

    private Nucleotides randNuc() {
        int pick = new Random().nextInt(Nucleotides.values().length);
        return Nucleotides.values()[pick];
    }

    private String getRandomNuc(int nucleotideLength){
        String str = "";
        for (int i = 0; i < nucleotideLength; i++) {
            str += randNuc();
        }
        return str;
    }

    public void generateProblem(String filename, int nucleotideLength, int n, int posNum, int negNum){
        for (int i = 0; i < nucleotideLength + n - 1; i++) {
            problemStr += randNuc();
        }
        for (int i = 0; i < n; i++) {
            strList.add(problemStr.substring(i, i+nucleotideLength));
        }
        for(int i =0; i < negNum; i++){
            strList.remove(new Random().nextInt(strList.size()));
        }
        for (int i = 0; i < posNum; i++) {
            strList.add(getRandomNuc(nucleotideLength));
        }
        Collections.shuffle(strList);
        for(int i = 0; i < strList.size(); i++){
            System.out.println(strList.get(i));
        }
        Collections.shuffle(strList);

        try {

            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            writer.println((n+ nucleotideLength - 1) + " " + nucleotideLength + " " + n + " " + (n - negNum + posNum));
            for(String str: strList){
                writer.println(str);
            }
            writer.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        //System.out.println(problemStr);


    }
}
