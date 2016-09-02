package edu.ufl.alexgre.P2P;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.SequenceInputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;

class Client {
	private String clientConfigFilePath;
	private int peerServerPort;
	
	public Client(String configFilePath, int peerServerPort){
		clientConfigFilePath = configFilePath;
		this.peerServerPort = peerServerPort;
	}
	
	public void clientRun(){
		//download files from server
		ClientConfig cc = new ClientConfig(clientConfigFilePath);
		String serverIP = cc.getServerIP();
		int serverPort = cc.getServerPort();
		//default path for where the downloaded .part files will be stored, before every new download process, the config file with the partfilefolder directory have to to be modified 
		String partFileFolder = cc.getPartFileFolderDir(); 
		System.out.println(partFileFolder);
		
		System.out.println("Trying to connect to " + serverIP + " at port: " + serverPort);
		DownloadFileFromServer dffs = new DownloadFileFromServer(serverIP, serverPort);
		dffs.downloadPartFiles(partFileFolder);
		
		
		//peer to peer file exchange
		FileCombination fc = null;
		try {
			 fc = new FileCombination(partFileFolder);
		} catch (FileNotFoundException e) {
			System.out.println("The original config file path is not found, check the right path and run again");
			System.exit(0);
		}
		
		String downloadClientIP = cc.getPeerClientIP();
		int downloadClientPort = cc.getPeerClientPort();
		
		DownloadFromPeer dfp = new DownloadFromPeer(fc, downloadClientIP, downloadClientPort, partFileFolder);
		UploadToPeer utp = new UploadToPeer(peerServerPort, partFileFolder);
		
		Thread download = new Thread(dfp);
		download.start();
		Thread upload = new Thread(utp);
		upload.start();
	}
}

//this class handle the task of uploading the possessed .part files to its neighbor client
class UploadToPeer implements Runnable{	
	//private ArrayList<File> partFileList = null;
	private int uploadServerPort;
	private ServerSocket sskt = null;
	private Socket skt = null;
	private String partFileFolder;
	
	public UploadToPeer(int port, String partFileFolder){
		uploadServerPort = port;
		this.partFileFolder = partFileFolder;
	}
	
	@Override
	public synchronized void run() {
		System.out.println("upload start...");		
		DataInputStream dis = null;
		DataOutputStream dos = null;
		FileInputStream fis = null;
		//partFileList = new ArrayList<File>();
		 		
		boolean flag = true;
		
		try {
			sskt = new ServerSocket(uploadServerPort);
			
			while(flag){
				try {
					skt = sskt.accept();
					dos = new DataOutputStream(skt.getOutputStream());
					dis = new DataInputStream(skt.getInputStream());
					//TODO error
					ArrayList<String> clientList = new ArrayList<String>();
					int n = dis.readInt();
					
					for(int i = 0; i < n; i++){
						String s = dis.readUTF();
						clientList.add(s);
					}
					
					ArrayList<File> fileToSend = new ArrayList<File>();
					ArrayList<String> toSendNames = new ArrayList<String>();
					File folder = new File(partFileFolder);
					File[] files = folder.listFiles();
					ArrayList<String> partFileNames = new ArrayList<String>();
					for(File f: files){
			        	//partFileList.add(f);
			        	partFileNames.add(f.getName());
			        }
					for(String name: partFileNames){
						if(!clientList.contains(name)){
							File f = new File(partFileFolder ,name);
							if(f.exists()){
								fileToSend.add(f);
								toSendNames.add(f.getName());
							}
						}
					}
					dos.writeUTF("Receiving file list: " + toSendNames + "");
					
					dos.writeInt(fileToSend.size());
	
					byte[] buf = new byte[8000];
					int len = 0;
					//use the same protocol of server-client file transfer
					for(File f: fileToSend){
						dos.writeUTF(f.getName());
						dos.flush();
						
						dos.writeLong(f.length());
						dos.flush();
						while(true){
							try {
								fis = new FileInputStream(f);
								while((len = fis.read(buf)) != -1){
									dos.write(buf, 0 ,len);
									dos.flush();
								}
								
								long size = dis.readLong();
								if(size == f.length()){
									dos.writeBoolean(false);
									break;
								}else{
									dos.writeBoolean(true);
								}
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} finally{
								if(fis != null){
									try {
										fis.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				
					int state = dis.readInt();
					if(state == 1){
						//System.out.println("All the .part files are sent.");
						flag = false;
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					if(dos != null){
						try {
							dos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					if(dis != null){
						try {
							dis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					if(skt != null){
						try {
							skt.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally{
			if(sskt != null){
				try {
					sskt.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

//this class handle the task of downloading the unpossessed .part files from its neighbor client
class DownloadFromPeer implements Runnable{
	private FileCombination fc;
	private String downloadIP;
	private int downloadPort;
	private String partFileFolder;
	//byte[] buf;
	private boolean fileTransferflag = true;
	private File folder;
	
	public DownloadFromPeer(FileCombination fc, String downloadPeerIP, int downloadPeerPort, String partFileFolder) {
		this.fc = fc;
		this.downloadIP = downloadPeerIP;
		this.downloadPort = downloadPeerPort;
		this.partFileFolder = partFileFolder;
	}
	
	/**
	 * downloading not possessed .part files from peer Client 
	 */
	@Override
	public synchronized void run() {
		System.out.println("download start...");
		Socket skt = null;	
		DataInputStream dis = null;
		DataOutputStream dos = null;
		
		int total = fc.getPartFileNum();
		//System.out.println("Total number of .part files should be " + total);
		
		while(fileTransferflag){
			boolean connectionflag = false;
			while(! connectionflag){
				try{
					skt = new Socket();
					InetSocketAddress isa = new InetSocketAddress(downloadIP, downloadPort);
					System.out.println("Trying to connect " + isa.getHostName() + " at port: " + isa.getPort());
					skt.connect(isa);
					
					connectionflag = true;

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
			
			try{
				//TODO partFileList is unnecessary (delete), only use partFileNames can achieve the same result
				ArrayList<File> partFileList = new ArrayList<File>();
				ArrayList<String> partFileNames = new ArrayList<String>();
				folder = new File(partFileFolder);
				File[] fileList = folder.listFiles();
				for(File f: fileList){
					partFileList.add(f);     //including a config file
				}
				
				for(File f: partFileList){
					partFileNames.add(f.getName());
				}
				
				dis = new DataInputStream(skt.getInputStream());
				dos = new DataOutputStream(skt.getOutputStream());
				
				dos.writeInt(partFileNames.size());
				for(String s: partFileNames){
					dos.writeUTF(s);
				}
				dos.flush();
				
				System.out.println(dis.readUTF());
				
				int num = dis.readInt();
				byte[] buf = new byte[8000];
				int len = 0;
				for(int i = 0; i < num; i++){
					String name = dis.readUTF();
					File f = new File(folder, name);
					
					long fileSize = dis.readLong();
					long currentSize = 0;
					boolean flag = true;
					while(flag){
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(f);
							
							while(currentSize < fileSize){
								len = dis.read(buf);
								if(len != -1){
									currentSize += (long)len;
									fos.write(buf, 0, len);
									fos.flush();
								}else{
									break;
								}
							}
							
							dos.writeLong(currentSize);
							flag = dis.readBoolean();
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							if(fos != null){
								try {
									fos.close();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
					
					partFileList.add(f);
				}
				
				Thread.sleep(500);
				int current = partFileList.size();
				//System.out.println("Current have " + current + " files.");
				
				//there should be total+1 files (one is config file)
				if(current >= (total+1)){
					fileTransferflag = false;
					System.out.println("All the .part files are downloaded.");
					dos.writeInt(1);
					dos.flush();
				}else{
					dos.writeInt(0);
					dos.flush();
				}
				
			}catch (Exception e){
				e.printStackTrace();
			}finally{
				if(dos != null){
					try {
						dos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(dis != null){
					try {
						dis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(skt != null){
					try {
						skt.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		fc.fileCombination();
		System.out.println("Downloading " + fc.getOriginalFileName() + " is finished.");
	}
	
	/**
	 * create a time interval between two attempts to connect to server
	 */
	private void tryReconnect(){
		System.out.println("Try to recoonect to PeerServer in 2 seconds...");
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){
			
		}
	}
}

class DownloadFileFromServer{
	private String serverIP;
	private int port;
	
	public DownloadFileFromServer(String IPaddress, int portNumber){
		serverIP = IPaddress;
		port = portNumber;
	}
	
	/**
	 *  download some .part files from the server
	 */
	public synchronized void downloadPartFiles(String partFileFolderPath){
		Socket skt = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;
		byte[] buf;
		String path = partFileFolderPath;
		
		boolean connectionFlag = false;
		while(! connectionFlag){
				try{
					skt = new Socket();
					
					InetSocketAddress iskta = new InetSocketAddress(serverIP, port);
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
					System.out.println("Connection error. " + e.getMessage());
					Scanner s = new Scanner(System.in);
					System.out.println("input the correct IP address for Server: ");
					serverIP = s.nextLine();
					s.close();
				}
		}
		
		try {
			String ack = "OK";
			dos.writeUTF(ack);
			
			int fileNum = dis.readInt();
			System.out.println("Received " + fileNum + " files from server.");
			buf = new byte[8000];
			int len = 0;
			
			/**
			 * Server-Client file transfer Protocol:
			 * 1.receive file name
			 * 2.receive file size
			 * 3.receive actual file content as binary
			 * 4.send file size client received
			 * 5.receive ACK as boolean flag: 
			 * 		if size received is right, receive false and start to receive the next file
			 * 		else receive true, re-receive current file  
			 */
			for(int i = 0; i < fileNum; i++){
				
				String fileName = dis.readUTF();

				//adjust buffer size based on file size
				long size = dis.readLong();
				long rsize = 0;
				System.out.println("Received " + fileName + " size: " + size);
				
				FileOutputStream fos = null;
				boolean flag = true;
				while(flag){
					try {
						File f = new File(path, fileName);
						fos = new FileOutputStream(f);
						while(rsize < size){
							len = dis.read(buf);
							if(len != -1){
								fos.write(buf, 0, len);
								fos.flush();
								rsize += (long)len;
								//size -= len;
							}else{
								break;
							}
						}
						
						dos.writeLong(rsize);
						flag = dis.readBoolean();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if(fos != null){
							try {
								fos.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(dos != null){
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(dis != null){
				try {
					dis.close();
				} catch (IOException e) {
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
	
	/**
	 * create a time interval between two attempts to connect to server
	 */
	private void tryReconnect(){
		System.out.println("Try to recoonect to server in 2 seconds...");
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){
			
		}
	}
}

//extract config information for each client peers
class ClientConfig{
	private String configFilePath;
	private Properties prop;
	private String serverIP;
	private int serverPort;
	private String peerClientIP;
	private int peerClientPort;
	private String partFileFolder;
	private int ownPort;
	FileInputStream fis = null;
	
	public ClientConfig(String path){
		configFilePath = path;
		readPropertiesFile();
	}
	
	/**
	 * obtain the config file information
	 */
	private void readPropertiesFile(){		
		try {
			File f = new File(configFilePath);
			fis = new FileInputStream(f);
			prop = new Properties();
			prop.load(fis);
			
			serverIP = prop.getProperty("serverIP");
			serverPort = Integer.parseInt(prop.getProperty("serverport"));
			peerClientIP = prop.getProperty("downloadIP");
			peerClientPort = Integer.parseInt(prop.getProperty("downloadport"));
			partFileFolder = prop.getProperty("partfilefolder");
			ownPort = Integer.parseInt(prop.getProperty("ownport"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * @return server IP address
	 */
	public String getServerIP(){
		return serverIP;
	}
	
	/**
	 * 
	 * @return server port number
	 */
	public int getServerPort(){
		return serverPort;
	}
	
	/**
	 * 
	 * @return peer client IP address which this client will download rest of the .part files from
	 */
	public String getPeerClientIP(){
		return peerClientIP;
	}
	
	/**
	 * 
	 * @return peer client port number
	 */
	public int getPeerClientPort(){
		return peerClientPort;
	}
	
	/**
	 * 
	 * @return where to store the received .part files and originalCOnfig file
	 */
	public String getPartFileFolderDir(){
		File f = new File(partFileFolder);
		if(!f.exists()){
			f.mkdirs();
		}
		
		return f.getAbsolutePath();
	}
	
	/**
	 * 
	 * @return
	 */
	public int getOwnPort(){
		return ownPort;
	}
}

// the class is designed for recombine all the .part files back to the original file
class FileCombination{
	private String partFileFolderPath; //path where the .part files stored
	private File fileDir;
	private int partFileNum = 0; 
	private String originalFileName;
	private File[] files;
	private ArrayList<File> partFiles;
	
	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public FileCombination(String path) throws FileNotFoundException{
		this.partFileFolderPath = path;
		getOriginalFileInfo();
	}
	
	/**
	 * @return number of total number of .part files
	 */
	public int getPartFileNum(){
		return partFileNum;
	}
	
	/**
	 * @return The file name of the original file, use this name as the name for the recombined file
	 */
	public String getOriginalFileName(){
		return originalFileName;
	}
	
	/**
	 * read the original file information (fileName, # of .part files)  
	 * @throws FileNotFoundException 
	 */
	private void getOriginalFileInfo() throws FileNotFoundException{
		fileDir = new File(partFileFolderPath);
		files = fileDir.listFiles();
		
		if(files.length == 0){
			System.out.println("config file cannot be found.");
			throw new FileNotFoundException("config file is missing.");
		}else{
			Properties prop = new Properties();
			File propFile = null;
		
			for(File f: files){
				if(f.getName().endsWith("originalfileconfig.properties")){
					propFile = f;
				}
			}
			
			FileReader fr = null;
			
			try {
				fr = new FileReader(propFile);
				prop.load(fr);
				originalFileName = prop.getProperty("fileName");
				partFileNum = Integer.parseInt(prop.getProperty("numOfParts"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(fr != null){
					try {
						fr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @return an ArrayList containing all the .part files 
	 */
	private void allPartFiles(){
		partFiles = new ArrayList<File>();
		fileDir = new File(partFileFolderPath);
		files = fileDir.listFiles();
		for(File f: files){
			if(f.getName().endsWith(".part")){
				partFiles.add(f);
			}
		}
		
		
		//System.out.println(partFiles + "");
		//Collections.sort(partFiles);
		//System.out.println(partFiles + "");
	}
	
	/**
	 * 
	 * @return current number of .part files
	 */
	public int partFileCount(){
		int count = partFiles.size();
		return count;
	}
	
	/**
	 * 
	 * @return The whole path for place where the recombined file stored
	 */
	private String completeFileDir(){
		File dir = new File(fileDir.getParentFile(), "complete" + originalFileName);
		
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		return dir.getAbsolutePath();
	}
	
	/**
	 * The function is used to combine all the .part files back to the original file
	 */
	public void fileCombination(){
		allPartFiles();
		
		String completeFileDir = completeFileDir(); 
		
		SequenceInputStream sis = null;
		FileOutputStream fos = null;
		RandomAccessFile raf = null;
		
		try {
			File originalFile = new File(completeFileDir, originalFileName);
			
			ArrayList<FileInputStream> fileList = new ArrayList<FileInputStream>();
			for(File f: partFiles){
				fileList.add(new FileInputStream(f));
			}
			
			Enumeration<FileInputStream> en = Collections.enumeration(fileList);
			
			sis = new SequenceInputStream(en);
			fos = new FileOutputStream(originalFile);
			
			byte[] buf = new byte[8000];
			int len = 0;
			while((len = sis.read(buf)) != -1){
				fos.write(buf, 0, len);
				fos.flush();
			}
			
			raf = new RandomAccessFile(new File(completeFileDir, "log.txt"), "rwd");
			long position = raf.length();
			raf.seek(position);
			Date d = new Date();
			raf.write(("Part files has been merged" + " as" + originalFileName + "  @ " + d.toString() + "\n").getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(raf != null){
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(sis != null){
				try {
					sis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} 
	}
}
