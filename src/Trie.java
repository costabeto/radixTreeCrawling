public class Trie {

    private final Node root = new Node(false);
    private final char CASE;

    public Trie() {
        CASE = 'a';
    }

    public Trie(char CASE) {
        this.CASE = CASE;
    }

    public void insert(String word) {
        Node trav = root;
        int i = 0;

        while (i < word.length() && trav.edgeLabel[word.charAt(i) - CASE] != null) {
            int index = word.charAt(i) - CASE, j = 0;
            StringBuilder label = trav.edgeLabel[index];

            while (j < label.length() && i < word.length() && label.charAt(j) == word.charAt(i)) {
                ++i;
                ++j;
            }

            if (j == label.length()) {
                trav = trav.children[index];
            } else {
                if (i == word.length()) {
                    Node existingChild = trav.children[index];
                    Node newChild = new Node(true);
                    StringBuilder remainingLabel = strCopy(label, j);

                    label.setLength(j);
                    trav.children[index] = newChild;
                    newChild.children[remainingLabel.charAt(0) - CASE] = existingChild;
                    newChild.edgeLabel[remainingLabel.charAt(0) - CASE] = remainingLabel;
                } else {
                    StringBuilder remainingLabel = strCopy(label, j);
                    Node newChild = new Node(false);
                    StringBuilder remainingWord = strCopy(word, i);
                    Node temp = trav.children[index];

                    label.setLength(j);
                    trav.children[index] = newChild;
                    newChild.edgeLabel[remainingLabel.charAt(0) - CASE] = remainingLabel;
                    newChild.children[remainingLabel.charAt(0) - CASE] = temp;
                    newChild.edgeLabel[remainingWord.charAt(0) - CASE] = remainingWord;
                    newChild.children[remainingWord.charAt(0) - CASE] = new Node(true);
                }

                return;
            }
        }

        if (i < word.length()) {
            trav.edgeLabel[word.charAt(i) - CASE] = strCopy(word, i);
            trav.children[word.charAt(i) - CASE] = new Node(true);
        } else {
            trav.isEnd = true;
        }
    }

    private StringBuilder strCopy(CharSequence str, int index) {
        StringBuilder result = new StringBuilder(100);

        while (index != str.length()) {
            result.append(str.charAt(index++));
        }

        return result;
    }

    public boolean search(String word) {
        int i = 0;
        Node trav = root;

        while (i < word.length() && trav.edgeLabel[word.charAt(i) - CASE] != null) {
            int index = word.charAt(i) - CASE;
            StringBuilder label = trav.edgeLabel[index];
            int j = 0;

            while (i < word.length() && j < label.length()) {
                if (word.charAt(i) != label.charAt(j)) {
                    return false;
                }

                ++i;
                ++j;
            }

            if (j == label.length() && i <= word.length()) {
                trav = trav.children[index];
            } else {

                return false;
            }
        }

        return i == word.length() && trav.isEnd;
    }

    public boolean startsWith(String prefix) {
        int i = 0;
        Node trav = root;

        while (i < prefix.length() && trav.edgeLabel[prefix.charAt(i) - CASE] != null) {
            int index = prefix.charAt(i) - CASE;
            StringBuilder label = trav.edgeLabel[index];
            int j = 0;

            while (i < prefix.length() && j < label.length()) {
                if (prefix.charAt(i) != label.charAt(j)) {
                    return false;
                }

                ++i;
                ++j;
            }

            if (j == label.length() && i <= prefix.length()) {
                trav = trav.children[index];
            } else {
                return true;
            }
        }

        return i == prefix.length();
    }

    public void print() {
        printUtil(root, new StringBuilder());
    }

    private void printUtil(Node node, StringBuilder str) {
        if (node.isEnd) {
            System.out.println(str);
        }

        for (int i = 0; i < node.edgeLabel.length; ++i) {
            if (node.edgeLabel[i] != null) {
                int length = str.length();

                str = str.append(node.edgeLabel[i]);
                printUtil(node.children[i], str);
                str = str.delete(length, str.length());
            }
        }
    }
}

