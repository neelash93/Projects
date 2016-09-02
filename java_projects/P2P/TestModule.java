package edu.ufl.alexgre.P2P;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;

import org.junit.Test;

public class TestModule {
	@Test
	public void sort(){
		ArrayList<String> a = new ArrayList<String>();
		a.add("aa\2.part");
		a.add("aa\3.part");
		a.add("aa\1.part");
		
		System.out.println(a + "");
	}
	
	
	@Test
	public void testMTS(){
		ServerSocket sskt = null;
		Socket skt = null;
		DataOutputStream dos = null;
		DataInputStream dis = null;
		try {
			sskt = new ServerSocket(10323);
			skt = sskt.accept();
			dos = new DataOutputStream(skt.getOutputStream());
			dis = new DataInputStream(skt.getInputStream());
			String path = "C:\\Users\\xiyang\\Desktop\\computer networks\\P2PTest\\Server\\part and prop files of test1.zip";
			File f = new File(path);
			File[] files = f.listFiles();
			byte[] buf;
			dos.writeInt(files.length);
			buf = new  byte[8000];
			int len = 0;
			for(File f1: files){
				dos.writeUTF(f1.getName());
				System.out.println(f1.getName());
				dos.writeLong(f1.length());
				BufferedInputStream bis = null;
				while(true){
				try {
					bis = new BufferedInputStream(new FileInputStream(f1));
					while((len = bis.read(buf)) != -1){
						dos.write(buf, 0, len);
						dos.flush();
					}
				
					long size = dis.readLong();
					if(size == f1.length()){
						dos.writeBoolean(false);
						break;
					}else{
						dos.writeBoolean(true);
					}
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
				try {
					bis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
		
		try {
			dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			dis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			skt.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			sskt.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	@Test
	public void testMTC1(){
		Socket skt = null;
		DataOutputStream dos = null;
		DataInputStream dis = null;
		byte[] buf;
		File f = new File("C:\\Users\\xiyang\\Desktop\\test");
		if(!f.exists()){
			f.mkdirs();
		}
		
		try {
			skt = new Socket("127.0.0.1", 10323);
			dos = new DataOutputStream(skt.getOutputStream());
			dis = new DataInputStream(skt.getInputStream());
			
			int a = dis.readInt();
			buf = new byte[1024]; 
			int len = 0;
			for(int i = 0; i < a; i++){
				String name = dis.readUTF();
				long size = dis.readLong();
				long rsize = 0;
				buf = new byte[8000];
				File f1 = new File(f, name);
				FileOutputStream fos = null;
				boolean flag = true;
				while(flag){
				try {
					fos = new FileOutputStream(f1);
					while(rsize < size){
						len = dis.read(buf);
						if(len != -1){
						fos.write(buf, 0, len);
						fos.flush();
						rsize += (long)len;
						}else{
							break;
						}
					}
					
					dos.writeLong(rsize);
					flag = dis.readBoolean();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				
			}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
		
		try {
			dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			dis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			skt.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
	}

	@Test
	public void test(){
		ArrayList<String> aa = new ArrayList<String>();
		String a1 = "absnvguiacdsa";
		for(int i = 0; i < a1.length(); i++){
			String m = a1.charAt(i) + "";
			aa.add(m);
		}
		
		System.out.println(aa.toString());
		int n = a1.length()/5;
		System.out.println(n);
		for(int i = 1; i <= 5; i++){
			if(i != 5){
				for(int j = (i-1)*n; j < i*n; j++){
					System.out.print(aa.get(j).toString());
					
				}
			}else{
				for(int j = (i-1)*n; j<a1.length(); j++){
					System.out.print(aa.get(j).toString());
					
				}
			}
			System.out.println();
		}
		
	}
	
	@Test
	public void testObjectTCPserver(){
		String path = "C:\\Users\\xiyang\\Desktop\\computer networks\\P2PTest\\Server\\part and prop files of test1.zip";
		File f = new File(path);
		File[] files = f.listFiles();
		ArrayList<File> toSend = new ArrayList<File>();
		ArrayList<String> fileList = new ArrayList<String>();
		
		for(File f1 : files){
			fileList.add(f1.getName());
		}
		
		try {
			ServerSocket ssk = new ServerSocket(10001);
			Socket skt = ssk.accept();
			
			ObjectInputStream ois = new ObjectInputStream(skt.getInputStream());
			Object list = ois.readObject();
			ArrayList<String> clientList = (ArrayList<String>)list;
			
			File f3 = null;
			for(String f2: fileList){
				if(!clientList.contains(f2)){
					System.out.println(f2);
					f3 = new File(path, f2);
					toSend.add(f3);
				}
			}
			
			DataOutputStream dos = new DataOutputStream(skt.getOutputStream());
			FileInputStream fis = null;
			dos.writeInt(toSend.size());
			dos.flush();
			
			byte[] buf = new byte[10240];
			int len;
			for(File f4: toSend){
				dos.writeUTF(f4.getName());
				dos.flush();
				System.out.println(f4.getAbsolutePath());
				
				dos.writeLong(f4.length());
				dos.flush();
				
				fis = new FileInputStream(f4);
				len = 0;
				while((len = fis.read(buf)) != -1){
					dos.write(buf, 0 ,len);
					dos.flush();
				}
				
				fis.close();
			}
			
			dos.close();
			ois.close();
			skt.close();
			ssk.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testObjectTCPclient(){
		String path = "C:\\Users\\xiyang\\Desktop\\computer networks\\P2PTest\\Server\\1";
		File f = new File(path);
		File[] files = f.listFiles();
		ArrayList<String> fileList = new ArrayList<String>();
		for(File f1 : files){
			fileList.add(f1.getName());
		}
		
		
		try {
			Socket skt = new Socket("127.0.0.1", 10001);
			ObjectOutputStream oos = new ObjectOutputStream(skt.getOutputStream());
			oos.writeObject(fileList);
			oos.flush();
			
			DataInputStream dis = new DataInputStream(skt.getInputStream());
			FileOutputStream fos;
			int num = dis.readInt();
			for(int i = 0; i < num; i++){
				String name = dis.readUTF();
				File f2 = new File(path, name);
				System.out.println(f2.getAbsolutePath());
				fos = new FileOutputStream(f2);
				
				long size = dis.readLong();
				long rsize = 0;
				//int times = (int)(size / 10240);
				int end = (int)(size % 10240);
				
				byte[] buf = new byte[10240];
				byte[] buf1 = new byte[end];
				int len = 0;
				//int j = 1;
				while(rsize != size){
					len = dis.read(buf);
					fos.write(buf, 0, len);
					fos.flush();
					rsize += (long)len;
				}
				fos.close();
			}
			
			dis.close();
			oos.close();
			skt.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//when using DataInputStream, the buffersize should be around 10kb
	@Test
	public synchronized void multiFiletransferServer(){
		String path = "C:\\Users\\xiyang\\Desktop\\computer networks\\P2PTest\\" + 
						"Server\\part and prop files of test1.zip";
		try {
			File folder = new File(path);
			File[] files = folder.listFiles();
			FileInputStream fis = null;
			
			ServerSocket sskt = new ServerSocket(10001);
			Socket skt = sskt.accept();
			
			DataInputStream in = new DataInputStream(skt.getInputStream());
			byte[] buf = null;
			int len;
			String ACK = in.readUTF();
			System.out.println(ACK);
			
			int n = in.readInt();
			int[] seq = new int[n];
			for(int i = 0; i < n; i++){
				seq[i] = in.readInt();
			}
			
			
					
			DataOutputStream out = new DataOutputStream(skt.getOutputStream());
			int fileNum = n + 1; //one file is properties file
			out.writeInt(fileNum);

			System.out.println(fileNum);
			
			for(File f: files){
				 if(f.getName().endsWith(".part")){
					 String fileName = f.getName();
					 buf = new byte[1024*10];
					 len = 0;
					 out.writeUTF(fileName);
					 out.flush();
					 
					 String a = in.readUTF();
					 
					 System.out.println(fileName + "  " + a);
					 
					 long fileSize = f.length();
					 long count = 0;
					 
					 out.writeLong(fileSize);
					 out.flush();
					
					 System.out.println(fileSize);
					 
					 fis = new FileInputStream(f);
					 while((len = fis.read(buf)) != -1){
						 count += len;
						 out.write(buf, 0, len);
						 out.flush();
						 System.out.println("send " + (count*100/fileSize) + "%" + count);
					 }
					 fis.close();
				 }
				 
				 if(f.getName().endsWith(".properties")){
					 String fileName = f.getName();
					 buf = new byte[10240];
					 len = 0;
					 out.writeUTF(fileName);
					 out.flush();
					 
					 String a = in.readUTF();
					 
					 System.out.println(fileName + "  " + a);
					 
					 long fileSize = f.length();
					 
					 out.writeLong(fileSize);
					 out.flush();
					
					 System.out.println(fileSize);
					 
					 fis = new FileInputStream(f);
					 while((len = fis.read(buf)) != -1){
						 out.write(buf, 0, len);
						 out.flush();
					 }
					 fis.close();
				 }
			 }
			
			skt.close();
			sskt.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public synchronized void multiFiletransferClient(){
		
		try {
			Socket skt = new Socket("127.0.0.1", 10001);
			String dir1 = "C:\\Users\\xiyang\\Desktop\\computer networks\\P2PTest\\" + 
					"Client\\part and prop files of test1.zip";
			
			File dir2 = new File(dir1);
			if(!dir2.exists()){
				dir2.mkdirs();
			}
			
			String ACK = "1.1OK";
			
			DataOutputStream out = new DataOutputStream(skt.getOutputStream());
			out.writeUTF(ACK);
			int[] seq = new int[]{1,5,8};
			out.writeInt(seq.length);
			
			for(int i = 0; i < seq.length; i++){
				out.writeInt(seq[i]);
				out.flush();
			}
			
			DataInputStream in = new DataInputStream(new BufferedInputStream((skt.getInputStream())));
			
			FileOutputStream fos = null;
			
			int fileNum = in.readInt();
			System.out.println(fileNum);

			for(int i = 0; i < fileNum; i++){
				byte[] buf = new byte[1024*10];
				int len = 0;
				
				String fileName = in.readUTF();
				System.out.println(fileName);
				out.writeUTF("get filename");
				
				File f = new File(dir2, fileName);
				System.out.println(f.getAbsolutePath());
				
				long size = in.readLong();
				int times = (int)(size / (1024*10));
				int end = (int)(size % (1024*10));
				long rsize = 0;
				byte[] buf1 = new byte[end];
				System.out.println(size + "  " + times + "  " + end);
				
				
				fos = new FileOutputStream(f);
				
				while(rsize != size){
					len = in.read(buf);
					fos.write(buf, 0, len);
					fos.flush();
					rsize += (long)len;
				}
				fos.close();
			}
			
			skt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void servertimeout(){
		try {
			ServerSocket sskt = new ServerSocket(9999);
			Socket skt = sskt.accept();
			
			DataInputStream dis = new DataInputStream(skt.getInputStream());
			DataOutputStream dos = new DataOutputStream(skt.getOutputStream());
			
			int a = dis.readInt();
			System.out.println(a);
			dos.writeUTF("OK");
			int b = dis.readInt();
			System.out.println(b);
			dos.writeUTF("OK");
			String s = dis.readUTF();
			dos.writeUTF(s);
			
			dis.close();
			dos.close();
			skt.close();
			sskt.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void clienttimeout(){
		Socket skt = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;
		
		boolean connectionFlag = false;
		while(! connectionFlag){
				try{
					skt = new Socket();
					String a = "127.0.0.1";
					InetSocketAddress iskta = new InetSocketAddress(a, 9999);
					skt.connect(iskta);
					
					connectionFlag = true;
					
					dis = new DataInputStream(skt.getInputStream());
					dos = new DataOutputStream(skt.getOutputStream());
					
				}catch(ConnectException e){
					System.out.println("Connection error. " + e.getMessage());
					tryReconnect();
				}catch(SocketTimeoutException e){
					System.out.println("Connection " + e.getMessage() + ".");
					tryReconnect();
				}catch(IOException e){
					e.printStackTrace();
				}
		}
		
		try {
			dos.writeInt(1);
			String info1 = dis.readUTF();
			System.out.println(info1);
			dos.writeInt(2);
			String infor2 = dis.readUTF();
			System.out.println(infor2);
			dos.writeUTF("over");
			String info3 = dis.readUTF();
			System.out.println(info3);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
		if(dis != null){
			try {
				dis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(dos != null){
			try {
				dos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(skt != null){
			try {
				skt.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
	}
	
	public void tryReconnect(){
		System.out.println("Try to recoonect to server in 2 seconds...");
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){
			
		}
	}
	
	
	@Test
	public void test6(){
		ReadConfigFile rcf = new ReadConfigFile("C:\\Users\\xiyang\\Desktop\\computer networks\\P2PTest\\Server\\ServerConfig.properties");
		System.out.println(rcf.getFilePath());
		System.out.println(rcf.getPartFileSize());
	}
	
	@Test
	public void test5(){
		FileInputStream fis = null;
		try {
			File f = new File("C:\\Users\\xiyang\\Desktop\\computer networks\\P2PTest\\Server\\ServerConfig.properties"); 
			Properties prop = new Properties();
			fis = new FileInputStream(f);
			prop.load(fis);
			String path = prop.getProperty("path");
			int size = Integer.parseInt(prop.getProperty("size"));
			System.out.println("size" + size +"  " + "path" + path);
			
			File f1 = new File(path);
			System.out.println(f1.exists() + "   " +  f1.length());
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Test
	public void test4(){
		String a = "C:\\Users\\xiyang\\Desktop\\computer networks\\P2PTest\\Server\\part and prop files of test1.zip\\1.part";
		File f = new  File(a);
		System.out.println(f.getName());
	}
	
	@Test
	public void test3(){
		Scanner s = new Scanner(System.in);
		String str = s.nextLine();
		if(str.equalsIgnoreCase("aa")){
			s.close();
			return; //terminated
		}
		s.close();
	}
	
	@Test
	public void test1(){
		try {
			File f = new File("C:\\Users\\xiyang\\Desktop\\1.txt");
			File dir = new File(f.getParentFile(), "partandpropfiles");
			if(!dir.exists()){
				dir.mkdirs();
			}
			String dir1 = dir.getAbsolutePath(); 
			
			File f1 = new File(dir1, "2.txt");
			System.out.println(f1.getAbsolutePath());
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(f1));
			bw.write("alex");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	@Test
	public void test2(){
		try {
			Class clazz = A.class;
			Method m1 = clazz.getDeclaredMethod("getA");
			m1.setAccessible(true);
			A a = new A();
			int i = (int)m1.invoke(a);
			System.out.println(i);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}


class A{
	int i = 1;
	
	private int getA(){
		return i;
	}
}