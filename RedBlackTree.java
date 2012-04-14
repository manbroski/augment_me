/* Copyright (c) 2012 the authors listed at the following URL, and/or
the authors of referenced articles or incorporated external code:
http://en.literateprograms.org/Red-black_tree_(Java)?action=history&offset=20100112141306

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY intIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Retrieved from: http://en.literateprograms.org/Red-black_tree_(Java)?oldid=16622
*/

enum Color { RED, BLACK }

class Node
{
    public int key;
    public Node left;
    public Node right;
    public Node parent;
    public Color color;
    public Node successor, predecessor;

    public Node(int key, Color nodeColor, Node left, Node right) {
        this.key = key;
        this.color = nodeColor;
        this.left = left;
        this.right = right;
        if (left  != null)  left.parent = this;
        if (right != null) right.parent = this;
        this.parent = null;
        this.successor = null;
        this.predecessor = null;
    }
    public Node grandparent() {
        assert parent != null; // Not the root node
        assert parent.parent != null; // Not child of root
        return parent.parent;
    }
    public Node sibling() {
        assert parent != null; // Root node has no sibling
        if (this == parent.left)
            return parent.right;
        else
            return parent.left;
    }
    public Node uncle() {
        assert parent != null; // Root node has no uncle
        assert parent.parent != null; // Children of root have no uncle
        return parent.sibling();
    }
}

public class RedBlackTree
{
    public static final boolean VERIFY_RBTREE = true;
    private static final int INDENT_STEP = 4;

    public Node root;

    public RedBlackTree() {
        root = null;
        //verifyProperties();
    }

       private static Color nodeColor(Node n) {
        return n == null ? Color.BLACK : n.color;
    }

    private Node successor (Node x) {
        if (x.right != null) {
            return minimumNode(x.right);
        }
        else {
            Node p = x.parent;
            while (p != null && x == p.right) {
                x = p;
                p = p.parent;
            }
            return p;
        }
    }

    private Node predecessor (Node x) {
        if (x.left != null) {
            return maximumNode(x.left);
        }
        else {
            Node p = x.parent;
            while (p != null && x == p.left) {
                x = p;
                p = p.parent;
            }
            return p;
        }
    }



    private Node lookupNode(int key) {
        Node n = root;
        while (n != null) {
            if (key == n.key) {
                return n;
            } else if (key < n.key) {
                n = n.left;
            } else {
                n = n.right;
            }
        }
        return n;
    }
    public Node lookup(int key) {
        Node n = lookupNode(key);
        return n;
    }
    private void rotateLeft(Node n) {
        Node r = n.right;
        replaceNode(n, r);
        n.right = r.left;
        if (r.left != null) {
            r.left.parent = n;
        }
        r.left = n;
        n.parent = r;
    }
    private void rotateRight(Node n) {
        Node l = n.left;
        replaceNode(n, l);
        n.left = l.right;
        if (l.right != null) {
            l.right.parent = n;
        }
        l.right = n;
        n.parent = l;
    }
    private void replaceNode(Node oldn, Node newn) {
        if (oldn.parent == null) {
            root = newn;
        } else {
            if (oldn == oldn.parent.left)
                oldn.parent.left = newn;
            else
                oldn.parent.right = newn;
        }
        if (newn != null) {
            newn.parent = oldn.parent;
        }
    }
    public void insert(int key) {
        Node insertedNode = new Node(key, Color.RED, null, null);
        if (root == null) {
            root = insertedNode;
        } else {
            Node n = root;
            while (true) {
                if (key == n.key) {
                    return;
                } else if (key < n.key) {
                    if (n.left == null) {
                        n.left = insertedNode;
                        break;
                    } else {
                        n = n.left;
                    }
                } else {
                    if (n.right == null) {
                        n.right = insertedNode;
                        break;
                    } else {
                        n = n.right;
                    }
                }
            }
            insertedNode.parent = n;
        }
        insertCase1(insertedNode);

        Node successor = successor(insertedNode);
        Node predecessor = predecessor(insertedNode);
        insertedNode.successor = successor;
        if (successor != null)
            successor.predecessor = insertedNode;
        insertedNode.predecessor = predecessor;
        if (predecessor != null)
        predecessor.successor = insertedNode;
    }
    private void insertCase1(Node n) {
        if (n.parent == null)
            n.color = Color.BLACK;
        else
            insertCase2(n);
    }
    private void insertCase2(Node n) {
        if (nodeColor(n.parent) == Color.BLACK)
            return; // Tree is still valid
        else
            insertCase3(n);
    }
    void insertCase3(Node n) {
        if (nodeColor(n.uncle()) == Color.RED) {
            n.parent.color = Color.BLACK;
            n.uncle().color = Color.BLACK;
            n.grandparent().color = Color.RED;
            insertCase1(n.grandparent());
        } else {
            insertCase4(n);
        }
    }
    void insertCase4(Node n) {
        if (n == n.parent.right && n.parent == n.grandparent().left) {
            rotateLeft(n.parent);
            n = n.left;
        } else if (n == n.parent.left && n.parent == n.grandparent().right) {
            rotateRight(n.parent);
            n = n.right;
        }
        insertCase5(n);
    }
    void insertCase5(Node n) {
        n.parent.color = Color.BLACK;
        n.grandparent().color = Color.RED;
        if (n == n.parent.left && n.parent == n.grandparent().left) {
            rotateRight(n.grandparent());
        } else {
            assert n == n.parent.right && n.parent == n.grandparent().right;
            rotateLeft(n.grandparent());
        }
    }
    public void delete(int key) {
        Node n = lookupNode(key);
        if (n == null)
            return;  // intey not found, do nothing
        if (n.left != null && n.right != null) {
            // Copy key/value from predecessor and then delete it instead
            Node pred = maximumNode(n.left);
            n.key   = pred.key;
            n = pred;
        }

        assert n.left == null || n.right == null;
        Node child = (n.right == null) ? n.left : n.right;
        if (nodeColor(n) == Color.BLACK) {
            n.color = nodeColor(child);
            deleteCase1(n);
        }
        replaceNode(n, child);
        
        if (nodeColor(root) == Color.RED) {
            root.color = Color.BLACK;
        }

    }
    private static Node maximumNode(Node n) {
        if (n != null) {
            while (n.right != null) {
                n = n.right;
            }
            return n;
        }
        else {
            return null;
        }
    }
    private static Node minimumNode (Node n) {
        if (n != null) {
            while (n.left != null) {
                n = n.left;
            }
            return n;
        }
        else {
            return null;
        }
    }

    private void deleteCase1(Node n) {
        if (n.parent == null)
            return;
        else
            deleteCase2(n);
    }
    private void deleteCase2(Node n) {
        if (nodeColor(n.sibling()) == Color.RED) {
            n.parent.color = Color.RED;
            n.sibling().color = Color.BLACK;
            if (n == n.parent.left)
                rotateLeft(n.parent);
            else
                rotateRight(n.parent);
        }
        deleteCase3(n);
    }
    private void deleteCase3(Node n) {
        if (nodeColor(n.parent) == Color.BLACK &&
            nodeColor(n.sibling()) == Color.BLACK &&
            nodeColor(n.sibling().left) == Color.BLACK &&
            nodeColor(n.sibling().right) == Color.BLACK)
        {
            n.sibling().color = Color.RED;
            deleteCase1(n.parent);
        }
        else
            deleteCase4(n);
    }
    private void deleteCase4(Node n) {
        if (nodeColor(n.parent) == Color.RED &&
            nodeColor(n.sibling()) == Color.BLACK &&
            nodeColor(n.sibling().left) == Color.BLACK &&
            nodeColor(n.sibling().right) == Color.BLACK)
        {
            n.sibling().color = Color.RED;
            n.parent.color = Color.BLACK;
        }
        else
            deleteCase5(n);
    }
    private void deleteCase5(Node n) {
        if (n == n.parent.left &&
            nodeColor(n.sibling()) == Color.BLACK &&
            nodeColor(n.sibling().left) == Color.RED &&
            nodeColor(n.sibling().right) == Color.BLACK)
        {
            n.sibling().color = Color.RED;
            n.sibling().left.color = Color.BLACK;
            rotateRight(n.sibling());
        }
        else if (n == n.parent.right &&
                 nodeColor(n.sibling()) == Color.BLACK &&
                 nodeColor(n.sibling().right) == Color.RED &&
                 nodeColor(n.sibling().left) == Color.BLACK)
        {
            n.sibling().color = Color.RED;
            n.sibling().right.color = Color.BLACK;
            rotateLeft(n.sibling());
        }
        deleteCase6(n);
    }
    private void deleteCase6(Node n) {
        n.sibling().color = nodeColor(n.parent);
        n.parent.color = Color.BLACK;
        if (n == n.parent.left) {
            assert nodeColor(n.sibling().right) == Color.RED;
            n.sibling().right.color = Color.BLACK;
            rotateLeft(n.parent);
        }
        else
        {
            assert nodeColor(n.sibling().left) == Color.RED;
            n.sibling().left.color = Color.BLACK;
            rotateRight(n.parent);
        }
    }

    public static void main(String[] args) {
        RedBlackTree t = new RedBlackTree();

        for (int i = 10; i > 0; i--) {
            int x = i;

            System.out.println("Inserting " + x);

            t.insert(x);
        }
        for (int i = 10; i > 0; i--) {
            if (t.lookup(i).predecessor != null)
            System.out.println(t.lookup(i).predecessor.key);
        }
        for (int i = 0; i < 10; i++) {
            int x = i;

            System.out.println("Deleting key " + x);

            t.delete(x);
        }
    }
}

