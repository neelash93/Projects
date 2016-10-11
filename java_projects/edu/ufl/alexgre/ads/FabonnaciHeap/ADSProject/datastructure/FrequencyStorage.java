package edu.ufl.alexgre.ads.FabonnaciHeap.ADSProject.datastructure;

/*
 * the frequencyStorage is basically a brief version of max fibonacci heap
 * The hashtap's frequencies are stored in the frequencyStorage.
 * Since the frequency of hashtags is a int number, the fibonacci heap will directly use int type for node value.
 * The node <HashtagFreq> can not be initialized outside. Using getFreqInstance() method to obtain a obj of HashtagFreq 
 * only some of the fibonacci heap functions are implemented based on project requirements
 * function list:
 * 	insert()
 * 	changeFreq(freq)
 * 		increaseFreq() if freq > 0
 * 		decreaseFreq() if freq < 0
 * 	getMax()
 * 	deleteMax()
 */

public class FrequencyStorage {
	//private static final int INFIITE_MAX = 999999999;
	private int freqNum; // total node number
	private HashtagFreq max; // max ptr
	// private Map<Integer, HashtagFreq> degreeMap; //track each heap's degree
	// (max size is Log(n))

	/**
	 * constructor without args
	 */
	public FrequencyStorage() {
		freqNum = 0;
		max = null;
	}

	/**
	 * insert a new node into the top double linked list in the position left
	 * next to max
	 * 
	 * @param node
	 */
	public void insert(HashtagFreq node) {
		node.degree = 0;
		
		if (freqNum == 0) {
			max = node;
			max.left = max;
			max.right = max;
		} else {
			addNode(max, node);
			if (max.freq < node.freq) {
				max = node;
			}
		}

		freqNum += 1;
	}

	/**
	 * even through newFreq should be larger than oldFreq just in case, both
	 * increase and decrease methods are implemented newFreq is not the
	 * increment but the final value
	 * 
	 * @param node
	 * @param newFreq
	 */
	public void changeFreq(HashtagFreq node, int newFreq) {
		if (max == null || node == null) {
			return;
		}

		int oldFreq = node.freq;
		if (oldFreq < newFreq) {
			System.out.println("Increase frequency to" + newFreq + ".");
			increaseFreq(node, newFreq);
		} else if (oldFreq == newFreq) {
			return;
		} else {
			System.out.println("Decrease frequency to" + newFreq + ".");
			decreaseFreq(node, newFreq);
		}
	}

	/*
	 * getMax() and deleteMax() combined as extractMax()[return max and delete
	 * it from the fibonacci heap] a alternative extractMax() can be easily
	 * obtained by changing deleteMax()
	 */
	/**
	 * 
	 * @return max node in fibonacci heap
	 */
	public HashtagFreq getMax() {
		return max;
	}

	/**
	 * delete the max node from the fibonacci heap add all children of max into
	 * top level double linked list
	 */
	// public HashtagFreq extractMax(){
	public void deleteMax() {
		if (this.max == null) {
			return;
		}

		HashtagFreq node = max;
		// remove all the max children and add them into the top level double
		// linked list
		while (node.child != null) {
			HashtagFreq child = node.child;

			removeNode(child);

			if (child.right == child) {
				node.child = null;
			} else {
				node.child = child.right;
			}

			addNode(max, child);
			child.parent = null;
		}

		removeNode(node);

		if (node.right == node) { // max node is the only node in fibonacci heap
			max = null;
		} else {
			max = node.right; // arbitrarily choose a max, this max will be
								// update in recombine() method
			recombine();
		}

		freqNum--;

		node = null;
		// return node;
	}

	/**
	 * add a newNode in the position left next to root
	 * 
	 * @param root
	 * @param newNode
	 */
	private void addNode(HashtagFreq root, HashtagFreq newNode) {
		root.left.right = newNode;
		newNode.left = root.left;
		newNode.right = root;
		root.left = newNode;
	}

	/**
	 * remove a node from the current double linked list
	 * 
	 * @param node
	 */
	private void removeNode(HashtagFreq node) {
		node.left.right = node.right;
		node.right.left = node.left;
	}

	/**
	 * decreaseKey method in fibonacci heap cascade cut will be used
	 * 
	 * @param node
	 * @param newFreq
	 */
	private void increaseFreq(HashtagFreq node, int newFreq) {
		HashtagFreq parent = node.parent;
		node.freq = newFreq;

		if (parent != null && (node.freq > parent.freq)) {
			cut(node, parent);
			cascadeCut(parent);
		}

		// update max pointer
		if (node.freq > max.freq) {
			max = node;
		}
	}

	/**
	 * remove the child from its parent and add the child into the top level
	 * double linked list if parent is marked as removed child before, the
	 * parent will also be removed from its parent and added into top level
	 * double linked list the cascade cut will stop if parent is in the top
	 * level or it did not loss a child before if parent did not loss child
	 * before, it will be marked this time
	 * 
	 * @param child
	 * @param parent
	 */
	private void cut(HashtagFreq child, HashtagFreq parent) {
		// parent degree - 1
		//updateDegree(parent, child.degree);
		parent.degree--;
		removeNode(child);

		// update parent's child pointer
		if (child.right == child) {
			parent.child = null;
		} else {
			parent.child = child.right;
		}

		// update child's own pointers
		child.parent = null;
		child.left = child.right = child;

		// add the removed child into the top level double linked list
		addNode(max, child);
	}

	/**
	 * cascadeCut: refer to cut
	 * 
	 * @param parent
	 */
	private void cascadeCut(HashtagFreq parent) {
		HashtagFreq grandparent = parent.parent;
		if (grandparent != null) { // parent is not in the top level
			if (parent.marked == false) {
				parent.marked = true;
			} else {
				cut(parent, grandparent);
				cascadeCut(grandparent);
			}
		}
	}

	/**
	 * update degree changes
	 * 
	 * @param parent
	 * @param childDegree
	 */
//	private void updateDegree(HashtagFreq parent, int childDegree) {
//		parent.degree -= childDegree;
//		if (parent.parent != null) {
//			//System.out.println("1");
//			updateDegree(parent.parent, childDegree);
//		}
//	}

	/**
	 * 
	 * @param node
	 * @param newFreq
	 */
	private void decreaseFreq(HashtagFreq node, int newFreq) {

	}

	/**
	 * combine all sub-heaps with same degree
	 */
	private void recombine() {
		// determine the largest degree of a single tree (log(n) base 2)
		int maxDegree = (int) Math.floor(Math.log(freqNum) / Math.log(2)) + 1;

		/*
		 * create an log(n)+1 sized array to store roots of the sub-heaps. at
		 * array[index], the stored root has a degree equaled index if a root of
		 * sub-heap has degree equaled maxDegree, then there is only one tree in
		 * the fibonacci heap
		 */

		// array index range is 0 - maxDegree
		HashtagFreq[] degreeTrack = new HashtagFreq[maxDegree + 1];
		//not necessary
		for (int i = 0; i < maxDegree; i++) {
			degreeTrack[i] = null;
		}

		/*
		 * remove node from previous double linked list one by one add removed
		 * node into the array based on its degree if two nodes have same
		 * degree, then combine the two sub-heaps associated with these two node
		 * loop until no two subheaps have same degree then recombine all the
		 * heaps by reestablish the top level double linked list
		 */

		while (max != null) {
			HashtagFreq curr = popMax();
			int degree = curr.degree;

			while (degreeTrack[degree] != null) {
				HashtagFreq prev = degreeTrack[degree];
				if (prev.freq > curr.freq) {
					// guarantee curr.freq is bigger than prev.freq
					// which means curr is always the root
					HashtagFreq temp = curr;
					curr = prev;
					prev = temp;
				}

				linkTwoHeaps(prev, curr);
				degreeTrack[degree] = null;
				degree++;
			}

			degreeTrack[degree] = curr;
		}

		// Reconstruct the top level double linked list
		max = null;
		for (int i = 0; i <= maxDegree; i++) {
			if (degreeTrack[i] != null) {
				if (max == null) {
					max = degreeTrack[i];
				} else {
					addNode(max, degreeTrack[i]);
					if (degreeTrack[i].freq > max.freq) {
						max = degreeTrack[i];
					}
				}
			}
		}
	}

	/**
	 * combine two sub-heaps into one sub-heap
	 * 
	 * @param child
	 * @param root
	 */
	private void linkTwoHeaps(HashtagFreq child, HashtagFreq root) {
		removeNode(child);

		if (root.child == null) {
			root.child = child;
		} else {
			addNode(root.child, child);
		}

		child.parent = root;
		root.degree++;
		child.marked = false;
	}

	/**
	 * isolated the max from the double linked list
	 * 
	 * @return previous max node
	 */
	private HashtagFreq popMax() {
		HashtagFreq node = max;

		if (node.right == node) {
			max = null;
		} else {
			removeNode(node);
			max = node.right; // arbitrary assignment of max
		}

		node.left = node.right = node; // isolate from the double linked list

		return node;
	}

	/**
	 * 
	 * @param freq
	 * @return a new instance of HashtagFreq
	 */
	public HashtagFreq getHashtagFreqInstance(int freq) {
		return new HashtagFreq(freq);
	}
}
