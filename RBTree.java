/**
 * @author Guy Rosenmann
 * 
 * 
 * RBTree
 * 
 * An implementation of a Red Black Tree with non-negative, distinct
 * integer keys and values
 * 
 */
public class RBTree {

	private RBNode nil;		// NIL leaf
	private RBNode root;	// Root of tree
	private int size;		// Number of nodes in the tree
	private RBNode minNode;	// RBNode with the smallest key in the tree
	private RBNode maxNode; // RBNode with the largest key in the tree

	/**
	 * public RBTree()
	 * 
	 * Constructs a new empty Red-Black Tree
	 */
	public RBTree() {
		this.nil = new RBNode("BLACK", -1, null);
		this.root = this.nil;
		this.minNode = this.nil;
		this.maxNode = this.nil;
		this.size = 0;
	}

	/**
	 * public boolean empty()
	 * 
	 * returns true if and only if the tree is empty
	 * 
	 */
	public boolean empty() {
		return (this.size == 0);
	}

	/**
	 * public String search(int k)
	 * 
	 * returns the value of an item with key k if it exists in the tree
	 * otherwise, returns null
	 */
	public String search(int k) {
		return this.findRBNode(k).value; 
	}

	/**
	 * private RBNode findNode(int k)
	 * 
	 * Returns the node of an item with key k if it exists in the tree
	 * otherwise, returns NIL
	 */
	private RBNode findRBNode(int k) {
		RBNode node = this.root;
		while (node != this.nil && node.key != k) { // While the node isn't a leaf and does not contain the key k
			if (k < node.key)
				node = node.left;	// Turn left if the current node's key is smaller than k
			else
				node = node.right;	// Turn right otherwise
		}
		return node;
	}

	/**
	 * public int insert(int k, String v)
	 * 
	 * inserts an item with key k and value v to the red black tree. the tree
	 * must remain valid (keep its invariants). returns the number of color
	 * switches, or 0 if no color switches were necessary. returns -1 if an item
	 * with key k already exists in the tree.
	 */
	public int insert(int k, String v) {
		RBNode newNode = new RBNode("RED", k, v, this.nil, this.nil, this.nil); // Creates the node to be inserted
		RBNode newNodeParent = this.nil;
		RBNode currentNode = this.root;
		if (newNode.key > this.maxNode.key) {		// If newNode is the maximum, its parent will be the previous maximum
			newNodeParent = this.maxNode;
			this.maxNode = newNode;
		}
		else if (newNode.key < this.minNode.key) {	// If newNode is the minimum, its parent will be the previous minimum
			newNodeParent = this.minNode;
			this.minNode = newNode;
		}
		else {                                      // If newNode is neither the maximum nor the minimum, search for insertion position
			while (currentNode != this.nil) {		
				newNodeParent = currentNode;
				if (newNode.key == currentNode.key)
					return -1;						// The key is already in the tree
				if (newNode.key < currentNode.key)
					currentNode = currentNode.left; // Turn left if the current node's key is smaller than newNode's
				else
					currentNode = currentNode.right;  // Turn right otherwise
			}
		}
		size++;										// Increase tree size
		newNode.parent = newNodeParent;
		if (newNodeParent == this.nil) {			// Tree is empty; newNode becomes the root
			newNode.color = "BLACK";
			this.root = newNode;
			this.maxNode = newNode;
			this.minNode = newNode;
			return 0;
		}
		if (newNode.key < newNodeParent.key)		// Check whether newNode is a right child or a left child
			newNodeParent.left = newNode;
		else
			newNodeParent.right = newNode;
		return (this.insertFixup(newNode));			// Return the number of color switches (as returned from insertFixup)
	}

	/**
	 * private int insertFixup(RBNode node)
	 * 
	 * Restores the red-black properties. Returns the number of color switches,
	 * or 0 if no color switches were necessary.
	 */
	private int insertFixup(RBNode node) {
		int switchesCounter = 0;                                // Local variable to count numer of color switches performed
		RBNode nodeUncle;                                       // Local variable to contain the current node's uncle
		while (node.parent.color.equals("RED")) {               // loop executes as long as there is a red node with a red parent
			if (node.parent == node.parent.parent.left) {
				nodeUncle = node.parent.parent.right;
				if (nodeUncle.color.equals("RED")) {				// Case 1 - the uncle is red
					node.parent.color = "BLACK";
					nodeUncle.color = "BLACK";
					node.parent.parent.color = "RED";
					switchesCounter += 3;							// Case 1 color switches
					node = node.parent.parent;
				}
				else {
					if (node == node.parent.right) {		// Case 2 - the uncle is black and the node is a right child
						node = node.parent;
						this.rotateLeft(node);
					}										// Case 3 - the uncle is black and the node is left child
					node.parent.color = "BLACK";
					node.parent.parent.color = "RED";
					switchesCounter += 2;					// Case 3 color switches
					this.rotateRight(node.parent.parent);
				}
			}
			else {	// Same as then clause, with "right" and "left" exchanged
				nodeUncle = node.parent.parent.left;
				if (nodeUncle.color.equals("RED")) {				// Case 1
					node.parent.color = "BLACK";
					nodeUncle.color = "BLACK";
					node.parent.parent.color = "RED";
					switchesCounter += 3;							// Case 1 color switches
					node = node.parent.parent;
				}
				else {
					if (node == node.parent.left) {			// Case 2
						node = node.parent;
						this.rotateRight(node);
					}										// Case 3
					node.parent.color = "BLACK";
					node.parent.parent.color = "RED";
					switchesCounter += 2;					// Case 3 color switches
					this.rotateLeft(node.parent.parent);
				}
			}
		}
		if (this.root.color.equals("RED")) {	// If root is red, it is changed to black
			this.root.color = "BLACK";
			switchesCounter++;                  // If the root's color was changes, it is counted as a color switch
		}
		return switchesCounter;					// Returns number of color switches
	}

	/**
	 * public int delete(int k)
	 * 
	 * deletes an item with key k from the binary tree, if it is there; the tree
	 * must remain valid (keep its invariants). returns the number of color
	 * switches, or 0 if no color switches were needed. returns -1 if an item
	 * with key k was not found in the tree.
	 */
	public int delete(int k) {
		RBNode node = this.findRBNode(k);		// Find node to delete
		if (node == this.nil)					// No item with key k
			return -1;
		int switchesCounter = 0;                // Local variable to count numer of color switches performed
		RBNode x, y = node;                     // Local variables; y will point to the node replacing the node with key k. x will point to the node replacing y
		String yOriginalColor = y.color;
		if (node.left == this.nil) {			// The node to be deleted has no left child
			x = node.right;
			if (node == this.minNode) {			// Fix minNode if needed
				if (node == this.root)
					this.maxNode = this.nil;	// If deleted node is root - fix maxNode too
				this.minNode = this.successor(this.minNode);  //Find the new minimum
			}
			this.transplant(node, node.right); // Replace the node to be deleted with its right subtree
		}
		else if (node.right == this.nil) {		// The node to be deleted has no right child
			x = node.left;
			this.transplant(node, node.left);   // Replace the node to be deleted with its left subtree
		}
		else {									// The node to be deleted has two children
			y = subtreeMinNode(node.right);     // y will point to the minimum in the node's right subtree
			yOriginalColor = y.color;
			x = y.right;                        // x will point to the above minimum's right child
			if (y.parent == node)
				x.parent = y;
			else {
				this.transplant(y, y.right);    // Replace y with its right subtree
				y.right = node.right;
				y.right.parent = y;
			}
			this.transplant(node, y);           // Replace the node with y
			y.left = node.left;
			y.left.parent = y;
			if (!y.color.equals(node.color)) {  //Make y's color the same as the deleted node's color
				y.color = node.color;
				switchesCounter++;
			}
		}
		if (yOriginalColor.equals("BLACK"))          // If y was originally black,  a fixup pof the tree is needed
			switchesCounter += this.deleteFixup(x);	
		size--;									// Decrease tree size
		if (node == this.maxNode)				// Fix maxNode if needed (if the maximum was deleted)
			this.maxNode = this.treeMaximum();
		return switchesCounter;					// Returns number of color switches
	}

	/**
	 * private int deleteFixup(RBNode node)
	 * 
	 * Restores the red-black properties. Returns the number of color switches,
	 * or 0 if no color switches were necessary.
	 */
	private int deleteFixup(RBNode node) {
		int switchesCounter = 0;
		RBNode nodeSibling;
		while (node != this.root && node.color.equals("BLACK")) {   // The loop will execute as long as the current node is black and is not the root
			if (node == node.parent.left) {
				nodeSibling = node.parent.right;
				if (nodeSibling.color.equals("RED")) {					// Case 1 - the node's brother is red
					nodeSibling.color = "BLACK";
					node.parent.color = "RED";
					switchesCounter += 2;
					this.rotateLeft(node.parent);
					nodeSibling = node.parent.right;
				}
				if (nodeSibling.left.color.equals("BLACK")
						&& nodeSibling.right.color.equals("BLACK")) {	// Case 2 - the node's brother is black and has two black children
					nodeSibling.color = "RED";
					switchesCounter++;
					node = node.parent;
				}
				else {
					if (nodeSibling.right.color.equals("BLACK")) {		// Case 3 - the node's brother is black and its right child is black
						nodeSibling.left.color = "BLACK";
						nodeSibling.color = "RED";
						switchesCounter += 2;
						this.rotateRight(nodeSibling);
						nodeSibling = node.parent.right;
					}													// Case 4  - the node's brother is black, its left child is black and its right child is red
					if (node.parent.color.equals("RED")) {
						nodeSibling.color = node.parent.color;
						node.parent.color = "BLACK";
						switchesCounter += 2;
					}
					nodeSibling.right.color = "BLACK";
					switchesCounter++;
					this.rotateLeft(node.parent);
					node = this.root;
				}
			}
			else {	// Same as then clause, with "right" and "left" exchanged
				nodeSibling = node.parent.left;
				if (nodeSibling.color.equals("RED")) {					// Case 1
					nodeSibling.color = "BLACK";
					node.parent.color = "RED";
					switchesCounter += 2;
					this.rotateRight(node.parent);
					nodeSibling = node.parent.left;
				}
				if (nodeSibling.left.color.equals("BLACK")
						&& nodeSibling.right.color.equals("BLACK")) {	// Case 2
					nodeSibling.color = "RED";
					switchesCounter++;
					node = node.parent;
				}
				else {
					if (nodeSibling.left.color.equals("BLACK")) {		// Case 3
						nodeSibling.right.color = "BLACK";
						nodeSibling.color = "RED";
						switchesCounter += 2;
						this.rotateLeft(nodeSibling);
						nodeSibling = node.parent.left;
					}													// Case 4
					if (node.parent.color.equals("RED")) {
						nodeSibling.color = node.parent.color;
						node.parent.color = "BLACK";
						switchesCounter += 2;
					}
					nodeSibling.left.color = "BLACK";
					switchesCounter++;
					this.rotateRight(node.parent);
					node = this.root;
				}
			}
		}
		if (node.color.equals("RED")) {
			node.color = "BLACK";
			switchesCounter++;
		}
		return switchesCounter;		// Returns color switches
	}

	/**
	 * private void rotateLeft(RBNode x)
	 * 
	 * Rotate left the subtree of node x
	 */
	private void rotateLeft(RBNode x) {
		RBNode y = x.right;
		x.right = y.left;
		if (y.left != this.nil)
			y.left.parent = x;
		y.parent = x.parent;
		if (x.parent == this.nil)
			this.root = y;
		else if (x == x.parent.left)
			x.parent.left = y;
		else
			x.parent.right = y;
		y.left = x;
		x.parent = y;
	}

	/**
	 * private void rotateRight(RBNode x)
	 * 
	 * Rotate right the subtree of node x
	 */
	private void rotateRight(RBNode x) {
		RBNode y = x.left;
		x.left = y.right;
		if (y.right != this.nil)
			y.right.parent = x;
		y.parent = x.parent;
		if (x.parent == this.nil)
			this.root = y;
		else if (x == x.parent.right)
			x.parent.right = y;
		else
			x.parent.left = y;
		y.right = x;
		x.parent = y;
	}

	/**
	 * private RBNode successor(RBNode node)
	 * 
	 * Returns the successor of node
	 */
	private RBNode successor(RBNode node) {
		if (node.right != this.nil)					// node has right child
			return subtreeMinNode(node.right);		// returns right sub-tree maximum
		RBNode x = node.parent;						// else
		while (x != this.nil && node == x.right) {
			node = x;
			x = x.parent;
		}
		return x;
	}

	/**
	 * private void transplant(RBNode x, RBNode y)
	 * 
	 * Transplant node y instead of x
	 */
	private void transplant(RBNode x, RBNode y) {
		if (x.parent == this.nil)
			this.root = y;
		else if (x == x.parent.left)
			x.parent.left = y;
		else
			x.parent.right = y;
		y.parent = x.parent;
	}

	/**
	 * public String min()
	 * 
	 * Returns the value of the item with the smallest key in the tree, or null
	 * if the tree is empty
	 */
	public String min() {
		return this.minNode.value;
	}

	/**
	 * private RBNode subtreeMinNode(RBNode node)
	 * 
	 * Returns the RBNode with the lowest key in subtree node If subtree node is
	 * empty - returns NIL
	 */
	private RBNode subtreeMinNode(RBNode node) {
		if (node == this.nil || node.left == this.nil)	// if node is NIL or a has no left child
			return node;
		return subtreeMinNode(node.left);
	}

	/**
	 * public String max()
	 * 
	 * Returns the value of the item with the largest key in the tree, or null
	 * if the tree is empty
	 */
	public String max() {
		return maxNode.value;
	}
	
    /**
     * private RBNode treeMaximum()
     * 
     * Returns the node with the lowest key in the tree, or NIL if the tree is
     * empty
     */
    private RBNode treeMaximum() {
        return subtreeMaxNode(this.root);
    }
 
    /**
     * private RBNode subtreeMaxNode(RBNode node)
     * 
     * Returns the RBNode with the highest key in subtree node If subtree node
     * is empty - returns NIL
     */
    private RBNode subtreeMaxNode(RBNode node) {
        if (node == this.nil || node.right == this.nil)	// if node is NIL or a has no right child
            return node;
        return subtreeMaxNode(node.right);
    }
    
	/**
	 * public int size()
	 * 
	 * Returns the number of nodes in the tree.
	 * 
	 * precondition: none postcondition: none
	 */
	public int size() {
		return (this.size);
	}

	/**
	 * public int[] keysToArray()
	 * 
	 * Returns a sorted array which contains all keys in the tree, or an empty
	 * array if the tree is empty.
	 */
	public int[] keysToArray() {
		int[] keysArr = new int[this.size];
		RBNode[] nodesArray = this.createInorderNodesArray();
		for (int i = 0; i < this.size; i++)	// Fills keysArr with keys in-order
			keysArr[i] = nodesArray[i].key;
		return keysArr;
	}

	/**
	 * public String[] valuesToArray()
	 * 
	 * Returns an array which contains all values in the tree, sorted by their
	 * respective keys, or an empty array if the tree is empty.
	 */
	public String[] valuesToArray() {
		String[] valuesArr = new String[this.size];
		RBNode[] nodesArray = this.createInorderNodesArray();
		for (int i = 0; i < this.size; i++)	// Fills valuesArr with values in-order
			valuesArr[i] = nodesArray[i].value;
		return valuesArr;
	}
	
	/**
	 * private RBNode[] createInorderNodesArray()
	 * 
	 * Returns an array which contains all the nodes in the tree, in-order
	 */
	private RBNode[] createInorderNodesArray() {
		RBNode[] nodesArray = new RBNode[this.size];
		this.inorderArrayHelper(nodesArray, this.root, 0);	// Fills nodesArray with RBNodes in-order
		return nodesArray;
	}
	
	/**
	 * private int inorderArrayHelper (RBNode[] nodesArray, RBNode currentNode, int index)
	 * 
	 * A recursive function that fills nodesArray with nodes from tree, in-order
	 */
	private int inorderArrayHelper (RBNode[] nodesArray, RBNode currentNode, int index) {
		if (currentNode != this.nil) {	// If not a leaf
			index = inorderArrayHelper(nodesArray, currentNode.left, index);		// Fills recursively with nodes from left sub-tree
			nodesArray[index] = currentNode;										// Inserts current node
			index = inorderArrayHelper(nodesArray, currentNode.right, index + 1);	// Fills recursively with nodes from right sub-tree
		}
		return index;	// Returns next empty index in nodesArray
	}
	
	
	
	// An RBNode class implementation - each node in the tree is a RBNode object
	
	/**
	 * 
	 * public class RBNode
	 * 
	 * An implementation of a Red Black Tree Node with parent, left and right
	 * nodes, color, non-negative integer keys and values
	 * 
	 */
	public class RBNode {

		private RBNode parent;		// Node's parent
		private RBNode left;		// Node's left child
		private RBNode right;		// Node's right child
		private String color;		// Node's color
		private final int key;		// Node's key
		private final String value;	// Node's value

		/**
		 * public RBNode(String color, int key, String value)
		 * 
		 * Creates a new RBNode object, with null parent, left and right children
		 */
		public RBNode(String color, int key, String value) {
			this(color, key, value, null, null, null);
		}

		/**
		 * public RBNode(String color, int key, String value, RBNode parent,
		 * RBNode leftChild, RBNode rightChild)
		 * 
		 * Creates a new RBNode object
		 * 
		 */
		public RBNode(String color, int key, String value, RBNode parent,
				RBNode leftChild, RBNode rightChild) {
			this.color = color;
			this.key = key;
			this.value = value;
			this.left = leftChild;
			this.right = rightChild;
			this.parent = parent;
		}
		
	}
	
}
