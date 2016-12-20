package edu.ufl.alexgre.ads.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Test;

import edu.ufl.alexgre.ads.FabonnaciHeap.FiboNode;
import edu.ufl.alexgre.ads.FabonnaciHeap.FibonacciHeap;
import edu.ufl.alexgre.ads.FabonnaciHeap.ADSProject.datastructure.HashtagFreq;
import edu.ufl.alexgre.basicdatastructure.heap.MaxHeap;
import edu.ufl.alexgre.basicdatastructure.heap.MinHeap;

public class TestADS {
	
	
	@Test
	public void testResultMatch() throws IOException{
		File f1 = new File("sampleOutput.txt");
		File f2 = new File("output_file.txt");
		
		BufferedReader br1 = new BufferedReader(new FileReader(f1));
		BufferedReader br2 = new BufferedReader(new FileReader(f2));
		String line1;
		String line2;
		int lineNum = 0;
		while((line1 = br1.readLine()) != null){
			lineNum++;
			ArrayList<String> al = new ArrayList<String>();
			ArrayList<String> al1 = new ArrayList<String>();
			String[] s1 = line1.split(",");
			for(String s: s1){
				al.add(s);
			}
			
			line2 = br2.readLine();
			String[] s2 = line2.split(",");
			for(String s: s2){
				s = s.substring(1);
				//System.out.println(s);
				if(al.contains(s)){
					al.remove(s);
				}else{
					al1.add(s);
				}
			}
			
			System.out.println(lineNum + al.toString() + al1.toString());
		}
		
		br1.close();
		br2.close();
	}
	
	@Test 
	public void testMath(){
		ArrayList<Long> al = new ArrayList<>();
		long a2 = 100l;
		al.add(a2);
		
		double n = 13;
		int a = (int) Math.floor(Math.log(n) / Math.log(2));
		int b = (int) Math.ceil(Math.log(n) / Math.log(2));
		System.out.println(a);
		System.out.println(b);
		
		int[] aa = new int[5 + 1];
		for(int i = 0; i <= 5; i++){
			aa[i] = 1;
		}
		
		for(int e : aa){
			System.out.println(e);
		}
	}
	
	@Test
	public void testObjLocation(){
		Object obj = new Object();
		System.out.println(obj);
		
		Object a = obj;
		System.out.println(a);
		
		obj = null;
		System.out.println(obj + ":" + a);
	}
	
//	@Test
//	public void testHashMap(){
//		HashMap<String, HashtagFreq> hm = new HashMap<String, HashtagFreq>();
//		ArrayList<HashtagFreq> al = new ArrayList<HashtagFreq>();
//		for(int i = 0; i < 5; i++){
//			HashtagFreq htf = new HashtagFreq(i);
//			hm.put("tag" + i, htf);
//			al.add(htf);
//		}
//		
//		HashtagFreq node = al.get(3);
//		Iterator<String> itr = hm.keySet().iterator();
//		while(itr.hasNext()){
//			String key = itr.next();
//			if(hm.get(key) == node){
//				System.out.println(key);
//			}
//		}
//	}

	@Test
	public void testIO1() throws IOException{
		for(int i = 0; i < 10; i++){
			for(int k = 0; k < 3; k++){
				String path = "test2.txt";
				BufferedWriter bw = new BufferedWriter(new FileWriter(path, true));
				if(k != 2){
					bw.write("hahahahahahah" + k + " " + i + ", ");
				}else{
					bw.write("hahahahahaha" + k + " " + i + "\n");
				}
				
				bw.close();
			}
		}
	}
	
	@Test
	public void testIO() throws IOException{
		File f2 = new File("text1.txt");
		FileWriter fw = new FileWriter(f2);
		fw.write("alex");
		fw.close();
		
		String inputFilePath = "test.txt";
		File file = new File(inputFilePath);

		System.out.println(file.getAbsolutePath());
		
		System.out.println(file.getParent());
		
		System.out.println(file.getName());
		
		File f = new File("C:\\Users\\xiyang\\Desktop\\scrapy.pdf");
		System.out.println(f.getParent());
	}
//	public void testIO(){
//		String path = "test.txt";
//		ProjectIOUtils.read(path);
//	}
	
	@Test
	public void testFibonacciHeap(){
		Integer[] arr = {2,4,1,7,10,5,6,8,9,3,18,11,14,13,15,27,23,20};
		FibonacciHeap<Integer> fh = new FibonacciHeap<Integer>();
		for(int each: arr){
			fh.insert(each);
		}
		
		fh.update(20, 100);
		fh.update(11, 12);
		fh.update(27, 0);
		fh.update(3, 21);
		fh.update(100, 3);
		fh.insert(16);
		
		//fh.changeKey(node, newKey);
		
//		while(fh.getMin() != null){
//			System.out.println(fh.getMinKey());
//			fh.deleteMin();
//		}
		
//		FiboNode<Integer> fn = fh.getFiboNodeInstance();
//		fn = fh.search(7);
//		fh.changeKey(fn, 999);
//		
//		fh.deleteMin();
//		
//		fh.delete(4);
//		
//		fh.print();
	}
	
	@Test
	public void testMaxHeap(){
		Integer[] arr = {2,4,1,7,10,5,6,8,9,3};
		
		MaxHeap<Integer> mh = new MaxHeap<Integer>(arr);
		
		mh.printMaxHeap();
		
		System.out.println(mh.popMax());
		
		mh.printMaxHeap();
		
		System.out.println(mh.isEmpty());
		
		System.out.println(mh.getMax());
	}
	
	
	@Test
	public void testMinHeap(){
		Integer[] arr = {2,4,1,7,10,5,6,8,9,3};
		
		MinHeap<Integer> mh = new MinHeap<Integer>(arr);
		
		mh.printMinHeap();
		
		System.out.println(mh.popMin());
		
		mh.printMinHeap();
		
		System.out.println(mh.isEmpty());
		
		System.out.println(mh.getMin());
	}
}
