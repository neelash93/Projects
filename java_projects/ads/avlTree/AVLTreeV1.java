package edu.ufl.alexgre.ads.avlTree;

public class AVLTreeV1 <T extends Comparable<? super T>>{
	private AVLNode<T> root;
	
	public AVLTreeV1(){
		root = null;
	}
	
	public void insert(T value){
		root = insert(root, value);
	}
	
	private AVLNode<T> insert(AVLNode<T> root, T value){
		if(root == null){
			root = new AVLNode<T>(value);
		}else{
			int cmp = value.compareTo(root.value);
			if(cmp > 0){
				root.right = insert(root.right, value);
				
				if(getHeight(root.right) - getHeight(root.left) == 2){
					if(value.compareTo(root.right.value) > 0){
						//case RR
						root = RR_Rotation(root);
					}else{
						//case RL
						root = RL_Rotation(root);
					}
				}
			}else if(cmp < 0){
				root.left = insert(root.left, value);
				
				if(getHeight(root.left) - getHeight(root.right) == 2){
					if(value.compareTo(root.left.value) < 0){
						//case LL
						root = LL_Rotation(root);
					}else{
						root = LR_Rotation(root);
					}
				}
			}else{ //cmp == 0
				System.out.println("Insert a exist value is not allowed.");
			}
		}
		
		root.height = max(getHeight(root.left), getHeight(root.right)) + 1;
		return root;
	}
	
	public void remove(T value){
		AVLNode<T> target;
		if((target = search(root, value)) != null){
			root = remove(root, target);
		}
	}
	
	private AVLNode<T> remove(AVLNode<T> root, AVLNode<T> target){
		if(root == null || target == null){
			return null;
		}
		
		int cmp = target.value.compareTo(root.value);
		
		if(cmp < 0){
			root.left = remove(root.left, target);
			if((getHeight(root.right) - getHeight(root.left)) ==2){
				if(getHeight(root.right.right) > getHeight(root.right.left)){
					root = RR_Rotation(root);
				}else{
					root = RL_Rotation(root);
				}
			}
		}else if(cmp > 0){
			root.right = remove(root.right, target);
			if(getHeight(root.left) - getHeight(root.right) == 2){
				if(getHeight(root.left.left) > getHeight(root.left.right)){
					root = LL_Rotation(root);
				}else{
					root = LR_Rotation(root);
				}
			}
		}else{
			if((root.left != null) && (root.right != null)){
				if(getHeight(root.left) > getHeight(root.right)){
					AVLNode<T> max = findMax(root.left);
					root.value = max.value;
					root.left = remove(root.left, max);
				}else{
					AVLNode<T> min = findMin(root.right);
					root.value = min.value;
					root.right = remove(root.right, min);
				}
			}else{
				//AVLNode<T> temp = root;
				//root = (root.left != null ? root.left : root.right);
				//temp.left = null;
				if(root.left != null){
					root.value = root.left.value;
					root.left = null;
				}else if(root.right != null){
					root.value = root.right.value;
					root.right = null;
				}else{
					root = null;
				}
			}
		}
		
		return root;
	}
	
	public AVLNode<T> search(T value){
		return search(root, value);
	}
	
	private AVLNode<T> search(AVLNode<T> root, T value){
		if(root == null){
			return null;
		}
		
		int cmp = value.compareTo(root.value);
		
		if(cmp > 0){
			return search(root.right.left, value);
		}else if(cmp < 0){
			return search(root.left, value);
		}else{
			return root;
		}
	}
	
	//not recursively find a node with provided value
	public AVLNode<T> iterativeSearch(T value){
		return iterativeSearch(root, value);
	}
	
	private AVLNode<T> iterativeSearch(AVLNode<T> root, T value){
		while(root != null){
			int cmp = value.compareTo(root.value);
			
			if(cmp > 0){
				root = root.right;
			}else if(cmp < 0){
				root = root.left;
			}else{
				return root;
			}
		}
		
		return root;
	}
	
	public T getMin(){
		if(isEmpty()){
			return null;
		}
		
		return findMin(root).value;
	}
	
	private AVLNode<T> findMin(AVLNode<T> node){
		if(node == null){
			return node;
		}
		
		while(node.left != null){
			node = node.left;
		}
		
		return node;
	}
	
	public T getMax(){
		if(isEmpty()){
			return null;
		}
		
		return findMax(root).value;
	}
	
	private AVLNode<T> findMax(AVLNode<T> node){
		if(node == null){
			return node;
		}
		
		while(node.right != null){
			node = node.right;
		}
		
		return node;
	}
	
	public int treeHeight(){
		return getHeight(root);
	}
	
	private int getHeight(AVLNode<T> node){
		if(node != null){
			return node.height;
		}
		
		return 0;
	}
	
	public boolean isEmpty(){
		return root == null;
	}
	
	private int max(int a, int b){
		return a > b ? a : b;
	}
	
	//LL case rotation
	private AVLNode<T> LL_Rotation(AVLNode<T> node){
		AVLNode<T> cNode = node.left;
		node.left = cNode.right;
		cNode.right = node;
		
		node.height = max(getHeight(node.left), getHeight(node.right)) + 1;
		cNode.height = max(node.height, getHeight(cNode.left)) + 1;
		
		return cNode;
	}
	
	//RR case rotation
	private AVLNode<T> RR_Rotation(AVLNode<T> node){
		AVLNode<T> cNode = node.right;
		node.right = cNode.left;
		cNode.left = node;
		
		node.height = max(getHeight(node.left), getHeight(node.right)) + 1;
		cNode.height = max(node.height, getHeight(cNode.right)) + 1;
		
		return cNode;
	}
	
	//RL case roration
	private AVLNode<T> RL_Rotation(AVLNode<T> node){
		node.right = LL_Rotation(node.right);
		
		return RR_Rotation(node);
	}
	
	private AVLNode<T> LR_Rotation(AVLNode<T> node){
		node.left = RR_Rotation(node.left);
		
		return LL_Rotation(node);
	}
	
	
	//pre order iterate tree
	public void preOrder(){
		preOrder(root);
	}
	
	//recursion
	private void preOrder(AVLNode<T> root){
		if(root != null){
			System.out.println(root.value + " ");
			preOrder(root.left);
			preOrder(root.right);
		}
	}
	
	//in order iterate tree
	public void inOrder(){
		inOrder(root);
	}
	
	private void inOrder(AVLNode<T> root){
		if(root != null){
			inOrder(root.left);
			System.out.println(root.value + " ");
			inOrder(root.right);
		}
	}
	
	//post order
	public void postOrder(){
		postOrder(root);
	}
	
	private void postOrder(AVLNode<T> root){
		postOrder(root.left);
		postOrder(root.right);
		System.out.println(root.value + " ");
	}
	
	public void clear(){
		clear(root);
	}
	
	private void clear(AVLNode<T> root){
		if(root == null) return;
		
		if(root.left != null){
			clear(root.left);
		}
		if(root.right != null){
			clear(root.right);
		}
		
		root = null;
	}
	
	private static class AVLNode<T>{
		private T value;
		private AVLNode<T> left;
		private AVLNode<T> right;
		int height;
		
		AVLNode(T value, AVLNode left, AVLNode right){
			this.height = 0;
			this.value = value;
			this.left = left;
			this.right = right;
		}
		
		AVLNode(T value){
			this(value, null, null);
		}
	}
}






























