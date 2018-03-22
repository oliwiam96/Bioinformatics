import java.util.List;

public class Node {

    private String nucleortide;
    public Node(String nucleortide) {
        this.nucleortide = nucleortide;
    }

    public String getNucleortide() {
        return nucleortide;
    }

    public void setNucleortide(String nucleortide) {
        this.nucleortide = nucleortide;
    }

    public static int getWeight(Node from, Node to){
        int i = 1;
        int l = from.getNucleortide().length();
        int maxCommonSubstringLength = 0;
        while(i < l){
            String subFromEnd = from.getNucleortide().substring(l-i, l);
            String subToBegin = to.getNucleortide().substring(0, i);
            if(subFromEnd.equals(subToBegin)){
                maxCommonSubstringLength = i;
            }
            i++;
        }
        return maxCommonSubstringLength;
    }

}
