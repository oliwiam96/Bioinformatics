import java.util.List;

public class Node {

    private String nucleotide;
    public Node(String nucleotide) {
        this.nucleotide = nucleotide;
    }

    public String getNucleotide() {
        return nucleotide;
    }

    public void setNucleotide(String nucleotide) {
        this.nucleotide = nucleotide;
    }

    public static int getWeight(Node from, Node to){
        int i = 1;
        int l = from.getNucleotide().length();
        int maxCommonSubstringLength = 0;
        while(i < l){
            String subFromEnd = from.getNucleotide().substring(l-i, l);
            String subToBegin = to.getNucleotide().substring(0, i);
            if(subFromEnd.equals(subToBegin)){
                maxCommonSubstringLength = i;
            }
            i++;
        }
        return maxCommonSubstringLength;
    }

}
