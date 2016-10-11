package edu.ufl.alexgre.ads.FabonnaciHeap.ADSProject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import edu.ufl.alexgre.ads.FabonnaciHeap.ADSProject.datastructure.FrequencyStorage;
import edu.ufl.alexgre.ads.FabonnaciHeap.ADSProject.datastructure.HashtagFreq;

/*
 * input file contains one of three information:
 * 	1 #hashtag frequency
 * 	2 number of outputs
 * 	3 stop
 * when info is 1, add the pair into a hashtable and track the frequencies in a fibonacci heap
 * when info is 2 (assume the number is k), output hashtags associated with 1st - kth largest frequencies
 * when info is 3, stop the process
 * 
 * two HashMaps are used to store information:
 * 	1. store hashtag-fibo_pointer
 * 	2. store fibo_pointer-hashtag
 * The is way can guarantee use either hashtag or fibo_pointer as key, the value can be found in O(1)
 * 
 * Multi-thread are not considered in this case. (Runnable are not implemented)
 */
public class Main {
	public static String outputFilePath = "output_file.txt";
	
	public static void main(String[] args) {
		//take args as input file path
//		if(args.length != 1){
//			System.out.println("Please input a file path.");
//			return;
//		}
//		//create input file and output file
//		String inputFilePath = args[0];
		String inputFilePath = "testProj.txt";
		
		File input_file = new File(inputFilePath);
		String outPath;
		if((outPath = input_file.getParent()) != null)
			outputFilePath = outPath + "output_file.txt";
		File output_file = new File(outputFilePath);

		//use hashmap to store hashtag-hashtagfreq_pointer pairs
		HashMap<String, HashtagFreq> HashtagFreqTable = new HashMap<String, HashtagFreq>();
		//this map is used to store hashtagfreq_pointer-hashtag pairs
		HashMap<HashtagFreq, String> FreqHashtagTable = new HashMap<HashtagFreq, String>();
		//use fibonacci heap to store hashtag associated frequencies
		FrequencyStorage freqSt = new FrequencyStorage();
		
		//IO operation
		BufferedReader br = null;
		BufferedWriter bw = null;
		int lineNum = 0;
		String line = null;
		
		//for store and output hashtags, check function testHashtag() @ TestADS.java for detail
		try {
			br = new BufferedReader(new FileReader(input_file));
			bw = new BufferedWriter(new FileWriter(output_file, true));
			
			while(!(line = br.readLine()).equalsIgnoreCase("stop")){
				lineNum++;
				if(!line.startsWith("#")){
					//get order for how many frequencies needed to be written into the output file
					int order = Integer.parseInt(line);
					//temporarily saving Max node popped from the fibonacci heap
					HashtagFreq[] tempMax = new HashtagFreq[order];
					
					//used to check if file content are appropriately processed 
//					System.out.println(order);
//					Iterator<Entry<String, HashtagFreq>> itr = HashtagFreqTable.entrySet().iterator();
//					while(itr.hasNext()){
//						Entry<String, HashtagFreq> en = itr.next();
//						System.out.println(en.getKey() + " : " + en.getValue().getFreq());
//					}

					//loop to control number of operations
					for(int i = 0; i < order; i ++){
						tempMax[i] = freqSt.getMax();
						
						//write associated hashtag into output file
						String hashtag = FreqHashtagTable.get(tempMax[i]);
						
						if(i != (order - 1)){
							bw.write(hashtag + ",");
						}else{
							bw.write(hashtag + System.lineSeparator()); 
						}
						
						freqSt.deleteMax();
					}
					
					for(int i = 0; i < order; i++){
						freqSt.insert(tempMax[i]);
					}
				}else{
					String[] content = line.split(" +");
					String key = content[0];
					int freq = Integer.parseInt(content[1]);
					//if two hashtags are the same, the value combined
					if(HashtagFreqTable.containsKey(key)){
						HashtagFreq old = HashtagFreqTable.get(key);
						int newFreq = old.getFreq() + freq;
						System.out.print("At tag: " + key + " ");
						freqSt.changeFreq(old, newFreq);
					}else{
						HashtagFreq hashtagFreq = freqSt.getHashtagFreqInstance(freq);
						HashtagFreqTable.put(key, hashtagFreq);
						FreqHashtagTable.put(hashtagFreq, key);
						freqSt.insert(hashtagFreq);		
					}
				}
			}
		} catch (NumberFormatException e) {
			//e.printStackTrace();
			System.out.println("data cannot be processed");
			System.out.println("problem of data is in line: " + lineNum + " of the file");
			System.out.println("the data in this line is: " + line);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {	
			e.printStackTrace();
		} finally {
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(bw != null){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
