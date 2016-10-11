package edu.ufl.alexgre.ads.FabonnaciHeap.ADSProject.datastructure;

/*
 * This class is the storage unit to store each hashtag's 
 * This class is a public class but all fields are protected
 * The constructor is protected, the object of this class can be created via getFreqInstance method in
 * FrequencyStorage class to facilitate encapsulation
 */
public class HashtagFreq {
	//fields
	protected int freq;
	protected int degree;
	protected boolean marked;
	protected HashtagFreq child;
	protected HashtagFreq parent;
	protected HashtagFreq left;
	protected HashtagFreq right;
	
	//constructor
	protected HashtagFreq(int freq){
		this.freq = freq;
		degree = 0;
		marked = false;
		child = parent = left = right = null;
	}
	
	//in this project, constructor without args wont be used
//	protected HashtagFreq(){
//		this(-1);
//	}
	
	public int getFreq(){
		return this.freq;
	}
//	
//	//args freq is the increment not new value
//	public void setFreq(int freq){
//		this.freq = this.freq + freq;
//	}
}
