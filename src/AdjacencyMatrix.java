public class AdjacencyMatrix {
    private int n;
    private int[][] matrix;
    private int l; // String length

    public void test(){
        Node node1 = new Node("DFABCABC");
        Node node2 = new Node("ABCABCZZ");
        l = node1.getNucleortide().length();
        System.out.println(getWieght(node1, node2));
    }

    private int getWieght(Node from, Node to){
        int i = 1;
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
