package edu.ufl.alexgre.ads.RBTree;

public class RBTree <K extends Comparable<? super K>>{
	private static final boolean RED = false;
	private static final boolean BLACK = true;
	
	//root of the BST
	private Node<K> root;
	
	public RBTree(){
		
	}
	
	public void insert(K key){
		Node<K> node = new Node<K>(key, RED);
		Node<K> cRoot = root;
		Node<K> pos = null;
		
		if(root == null){
			root = node;
			node.color = BLACK;
			return;
		}
		
		int cmp;
		
		while(cRoot != null){
			pos = cRoot;
			cmp = key.compareTo(cRoot.key);
			if(cmp > 0)
				cRoot = cRoot.right;
			else 
				cRoot = cRoot.left;
//			else{
//				System.out.println("The tree has a node with same key value");
//				return;
//			}
		}
			
			node.parent = pos;
			
			cmp = key.compareTo(pos.key);
			if(cmp > 0)
				pos.right = node;
			else
				pos.left = node;
		
		insertFixNonRecursion(node);
		//insertFixRecursion(node);
	}
	
	/*
	 * fix the violation after insertation to maintain all the five properties that RB tree has
	 * total five possible cases needed to be considered
	 * case 1 is when p is not exist: which mean the new node is the root of the tree has been handled in the insert function
	 * case 2 is when p is black node. In this case, no operations other than insert is required since insert a red node to a
	 * 	black parent will not violate the red-black tree properties
	 * therefore there are only three cases that need to be handled by rotation or color flip
	 */
	private void insertFixNonRecursion(Node<K> n){
		//p = parent, g = grandparent, u = uncle
		Node<K> p, g, u;
		while(((p = n.parent) != null) && isRed(p)){
			g = p.parent;
			
			if(g.left == p){
				u = g.right;
				
				if((u != null) && (isRed(u))){
					u.color = BLACK;
					p.color = BLACK;
					g.color = RED;
					n = g;
					continue;
				}
				
				if(n == p.right){
					p = leftRotation(p);
				}
					
				g.color = RED;
				p.color = BLACK;
				g = rightRotation(g);
			}else{   //g.left == u && g.right == p
				u = g.left;
				
				if((u != null) && (isRed(u))){
					u.color = BLACK;
					p.color = BLACK;
					g.color = RED;
					n = g;
					continue;
				}
				
				if(n == p.left){
					p = rightRotation(p);
				}
				
				p.color = BLACK;
				g.color = RED;
				g = leftRotation(g);
			}
		}
		
		root.color = BLACK;
	}
	
	public void delete(K key){
		Node<K> n = search(root, key);
		
		if(n != null)
			delete(n);
	}
	
	private void delete(Node<K> n){
		Node<K> p, p1, c;
		boolean color;
		//case1: the delete node has both left and right child
		if((n.left != null) && (n.right != null)){
			Node<K> r = n;
			r = r.right;
			r = getMin(r);
			//same as r = r.left; r = getMax(r);
			
			p1 = n.parent;
			if(p1 != null){
				if(p1.left == n)
					p1.left = r;
				else
					p1.right = r;
			}else{
				root = r;
			}
			
			c = r.right;
			p = r.parent;
			color = r.color;
			
			if(p == n){
				p = r;
			}else{
				if(c != null)
					c.parent = p;
				p.left = c;
				
				r.right = n.right;
				n.right.parent = r;
			}
			
			r.parent = p1;
			r.color = n.color;
			r.left = n.left;
			n.left.parent = r;
			
			if(color == BLACK)
				deleteFix(c, p);
			
			n = null;
			return;
		}
		//case2: the delete node has only one child (either left or right) or none
		if(n.left != null)
			c = n.left;
		else
			c = n.right;
		
		p = n.parent;
		
		color = n.color;
		
		if(c != null){
			c.parent = p;
		}
		
		if(p != null){
			if(p.left == n)
				p.left = c;
			else
				p.right = c;
		}else{
			root = c;
		}
		
		if(color == BLACK)
			deleteFix(c, p);
		
		n = null;
	}
	
	private void deleteFix(Node<K> c, Node<K> p){
		Node<K> s;
		
		while(((c == null) || isBlack(c)) && ((c != root))){
			if(c == p.left){
				s = p.right;
				if(isRed(s)){
					p.color = RED;
					s.color = BLACK;
					leftRotation(p); 
					s = p.right;
				}
					
				if((isBlack(s.left) || (s.left == null)) && (isBlack(s.right) || (s.right == null))){
					s.color = RED;
					c = p;
					p = p.parent;
				}else{
					if(isBlack(s.right) || (s.right == null)){
						//s.right is black, s.left is red
						s.left.color = BLACK;
						s.color = RED;
						s = rightRotation(s);
					}
					
					s.color = p.color;
					p.color = BLACK;
					s.right.color = BLACK;
					leftRotation(p);
					c = root;
				}
				
			}else{
				s = p.left;
				
				if(isRed(s)){
					s.color = BLACK;
					p.color = RED;
					rightRotation(p);
					s = p.left;
				}
				
				if((isBlack(s.left) || (s.left == null)) && (isBlack(s.right) || (s.right == null))){
					s.color = RED;
					c = p;
					p = p.parent;
				}else{
					if(isBlack(s.left) || (s.left == null)){
						//s.right is black, s.left is red
						s.right.color = BLACK;
						s.color = RED;
						s = leftRotation(s);
					}
					
					s.color = p.color;
					p.color = BLACK;
					s.left.color = BLACK;
					rightRotation(p);
					c = root;
				}
			}
		}
		
		if(c != null)
			c.color = BLACK;
	}
	
	public Node<K> get(K key){
		return search(root, key);
	}
	
	private Node<K> search(Node<K> root, K key){
		int cmp = root.key.compareTo(key);
		
		if(cmp > 0)
			return search(root.right, key);
		else if(cmp < 0)
			return search(root.left, key);
		else
			return root;
	}
	
	public Node<K> getMin(){
		if(root != null)
			return getMin(root);
		else return null;
	}
	
	private Node<K> getMax(Node<K> root){
		Node<K> r = root;
		while(r.right != null){
			r = r.right;
		}
		
		return r;
	}
	
	public Node<K> getMax(){
		if(root != null)
			return getMin(root);
		else return null;
	}
	
	private Node<K> getMin(Node<K> root){
		Node<K> r = root;
		while(r.left != null){
			r = r.left;
		}
		
		return r;
	}
	
	/* Red-Black tree rotation
	 * 
	 *       g                      p
	 *      / \    right           / \
	 *     p   u  ------->        n   g
	 *    / \                        / \   
	 *   n   a                      a   u 
	 *   
	 *       g                      p
	 *      / \    left            / \
	 *     u   p  ------->        g   n
	 *        / \                / \      
	 *       a   n              u   a    
	 */
	private Node<K> rightRotation(Node<K> g){
		Node<K> p = g.left;
		g.left = p.right;
		if(p.right != null){
			p.right.parent = g;
		}
		
		p.parent = g.parent;
		if(g.parent == null){
			root = p;
		}else{
			if(g == g.parent.left)
				g.parent.left = p;
			else
				g.parent.right = p;
		}
		
		p.right = g;
		g.parent = p;
		
		return p;
	}
	
	private Node<K> leftRotation(Node<K> g){
		Node<K> p = g.right;
		g.right = p.left;
		if(p.left != null){
			p.left.parent = g;
		}
		
		p.parent = g.parent;
		
		if(g.parent == null){ //g is root
			root = p;
		}else{
			if(g == g.parent.left)
				g.parent.left = p;
			else
				g.parent.right = p;
		}
		
		p.left = g;
		g.parent = p;
		
		return p;
	}
	
	private boolean isRed(Node<K> node){
		return !node.color;
	}
	
	private boolean isBlack(Node<K> node){
		return node.color;
	}
	
	//Node class define
	private static class Node<K>{
		private K key;
		//used to link to left and right subtree
		private Node<K> left, right, parent;
		private boolean color;
		
		private Node(K key, boolean color, Node<K> right, Node<K> left, Node<K> parent){
			this.key = key;
			this.color = color;
			this.left = left;
			this.right = right;
			this.parent = parent;
		}
		
		private Node(K key, boolean color){
			this(key, color, null, null, null);
		}
	}
}