public class Node {

    Node[] children = new Node[26];
    StringBuilder[] edgeLabel = new StringBuilder[26];
    boolean isEnd;

    public Node(boolean isEnd) {
        this.isEnd = isEnd;
    }
}

