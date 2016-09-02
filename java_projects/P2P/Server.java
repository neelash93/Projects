package edu.ufl.alexgre.P2P;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

public class Server {
	private static final int ServerPortNum = 10504; //port number are absolutely assigned
	
	public static void main(String[] args) {
		//partitioned the files for sending
		FilePartition fp = new FilePartition();
		
		//change route of the file when do demo
		/*
		System.out.println("input the file (full path) that you want to share and .part file size: ");
		Scanner s = new Scanner(System.in);
		String filePath = s.nextLine();
		int partFileSize = s.nextInt();
		s.close();
		*/
		
		//using Serverconfig file to config the program
		String configFilePath = "C:\\Users\\xiyang\\Desktop\\computer networks\\P2PTest\\Server\\ServerConfig.properties";
		ReadConfigFile rcf = new ReadConfigFile(configFilePath);
		String originalFilePath = rcf.getFilePath();
		int partFileSize = rcf.getPartFileSize();
		
		
		boolean a = fp.filePartition(originalFilePath, partFileSize);
		boolean b = fp.configFile();
		if(!a || !b){
			System.out.println("File path is wrong. Please check the config file and run again.");
			System.exit(0);
		}
		
		String partFileFolder = fp.fileDestination();
		
		ServerSocket sskt = null;
		
		try {
			//no close server socket commend, if we want to close the server socket, we need to manually abort.
			sskt = new ServerSocket(ServerPortNum);
			System.out.println("The Server is ready for connection...");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		int ID = 0;
		int clientID;
		while (true){
			try{
				Socket skt = sskt.accept();
				
				clientID = (ID % 5) + 1;
				FileTransfer ft = new FileTransfer(skt, partFileFolder, clientID);
				Thread connection = new Thread(ft);
				connection.start();
				ID += 1;
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
	}
}

//thread of transferring information with each different client connected with the Server
class FileTransfer implements Runnable{
	private Socket skt;
	private String partFileDir;
	private int clientID;
	
	/**
	 * only provide a constructor with args 
	 * @param socket
	 * @param partFileDir
	 * @param clientID
	 */
	public FileTransfer(Socket socket, String partFileDir, int clientID){
		this.skt = socket;
		this.partFileDir = partFileDir;
		this.clientID = clientID;
	}
	
	@Override
	/**
	 * transfer certain .part files and .properties file to each client connected
	 */
	public synchronized void run() {
		sendPartFiles();
		System.out.println("Download process for client" + clientID + " is finished.");
	}
	
	//no need to synchronize 
	private void sendPartFiles(){
		BufferedInputStream bis = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		byte[] buf;
		
		try {
			String clientIP = skt.getInetAddress().getHostAddress();
			
			System.out.println(clientIP + " connected...");
			
			in = new DataInputStream(skt.getInputStream());
			out = new DataOutputStream(skt.getOutputStream());
			
			//acknowledgement for successful connection
			String ack = in.readUTF();
			System.out.println("Client " + clientID + " connection state: " + ack);
			
			ArrayList<File> fileToTransfer = fileForConnectedClient();
			int filesNum = fileToTransfer.size();
			out.writeInt(filesNum);
			System.out.println(filesNum + " files are sent to Clinet " + clientID);
			buf = new byte[8000];
			int len = 0;
			
			/**
			 * Server-Client file transfer Protocol:
			 * 1.send file name
			 * 2.send file size
			 * 3.send actual file content as binary
			 * 4.receive file size client received
			 * 5.send ACK as boolean flag: 
			 * 		if size received is right, send false and start to send the next file
			 * 		else send true, re-send current file  
			 */
			for(File f: fileToTransfer){
				String fileName = f.getName();
				out.writeUTF(fileName);

				long fileSize = f.length();
				out.writeLong(fileSize);
				
				while(true){
					//long count = 0;
					try {
						bis = new BufferedInputStream(new FileInputStream(f));
						while((len = bis.read(buf)) != -1){
							//count += len;
							out.write(buf, 0, len);
							out.flush();
							//System.out.println("send " + (100*count/fileSize) + "%");
						}
						
						long size = in.readLong();
						if(size == f.length()){
							out.writeBoolean(false);
							break;
						}else{
							out.writeBoolean(true);
						}
					} catch (IOException e){
						e.printStackTrace();
					} finally{
						if(bis != null){
							try {
								bis.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(out != null){
					try {
						out.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			}
			
			if(in != null){
					try {
						in.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
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
	
	/**
	 * define a file ArrayList containing all the .part files needed to send for the current connected client
	 * rule of grouping files is based on clinetID: the total files have been divided into 5 groups, the each client get one group based on its ID sequence 
	 * @return ArrayList<File>
	 */
	private ArrayList<File> fileForConnectedClient(){
		File partFilefolder = new File(partFileDir);
		File[] files = partFilefolder.listFiles();
		ArrayList<File> partFile = new ArrayList<File>();
		ArrayList<File> fileToTransfer = new ArrayList<File>();
		int total = files.length - 1; //The last one file is .properties file
		int each = total / 5; //Since we will use 5 peers the number here is arbitrarily decided as 5
		
		for(File f: files){
			if(f.getName().endsWith(".properties")){
				fileToTransfer.add(f);
			}
			
			if(f.getName().endsWith(".part")){
				partFile.add(f);
			}
		}

		if(clientID != 5){
			for(int i = each*(clientID - 1); i < each*clientID; i++){
				fileToTransfer.add(partFile.get(i));
			}
		} else if(clientID == 5){
			for(int i = each*(clientID - 1); i < total; i++){
				fileToTransfer.add(partFile.get(i));
			}
		}
		
		return fileToTransfer;
	}
}



//this class used for partition the files into many .part files for sending to the clients
//this class created a .properties file contains the original file size and name for later .part files combination
class FilePartition{
	private String fileName = null;
	private long fileSize = 0;
	private int partNum = 0;
	private String filePath = null;
	private File f = null;
	
	public FilePartition(){
		
	}
	
	/**
	 * 
	 * @param filePath
	 */
	private void getFileAttribute(String filePath){
		f = new File(filePath);
		this.filePath = filePath; 
		fileName = f.getName();
		fileSize = f.length();
	}
	
	/**
	 * 
	 * @param filePath
	 * @return directory of where the part files and properties file will be stored
	 */
	private String getDir(){
		File dir = new File(f.getParentFile(), "part and prop files of " + fileName);
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		return dir.getAbsolutePath();
	}
	
	/**
	 * 
	 * @param partSize
	 * @return how many part files generated
	 */
	private int getPartNum(long partSize){
		if(fileSize % partSize == 0){
			return (int)(fileSize/partSize);
		}else{
			return (int)(fileSize/partSize + 1);
		}
	}
	
	/**
	 * 
	 * @param filePath
	 * @param currentPart
	 * @return after partition, the name of each .part file. (start with part order)
	 */
	private String getpartFileName(int currentPart){
		return  currentPart + ".part"; 
	}
	
	/**
	 * 
	 * @param filePath
	 * @param partFileName
	 * @param partSize
	 * @param beginPos
	 * @return indicate whether the read and write process is successful or not
	 */
	private boolean processFile(String partFileName, long partSize, long beginPos){
		RandomAccessFile raf = null;
		FileOutputStream fos= null;
		
		try {
			raf = new RandomAccessFile(filePath, "r");
			raf.seek(beginPos);
			fos = new FileOutputStream(partFileName);
			
			byte[] buf = new byte[1024];
			int len = 0;
			long writeByte = 0;
			
			while((len = raf.read(buf)) != -1){
				if(writeByte < partSize){
					writeByte += len;
					if(writeByte <= partSize){
						fos.write(buf, 0, len);
						fos.flush();
					}else{
						len = len - (int)(writeByte - partSize);
						fos.write(buf, 0, len);
					}
				}
			}
			
			fos.close();
			raf.close();
		
		} catch (IOException e) {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if(raf != null){	
				try {
					raf.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			System.out.println(e.getMessage());
			
			return false;
	}
		
		return true;
	}
	
	/**
	 * 
	 * @param filePath
	 * @param partSize
	 * @return indicate whether the partition is successful or not
	 */
	public boolean filePartition(String filePath, long partSize){
		getFileAttribute(filePath);
		System.out.println("filename: " + fileName + "\tfilesize: " + (fileSize/1024) + "KB");
		
		partNum = getPartNum(partSize);
		System.out.println("total num part files: " + partNum);
		//System.exit(0);
		
		if(partNum == 1){
			partSize = fileSize;
		}
		
		//use as read file location pointer
		long writeSize = 0;
		long WriteTotal = 0;
		
		String partFileName = null;
		
		for(int i = 1; i <= partNum; i++){
			if(i < partNum){
				writeSize = partSize;
			}else{
				writeSize = fileSize - WriteTotal;
			}
			
			String name = getpartFileName(i);
			
			System.out.println(name + "\tSize: " + (writeSize /1024) + "KB");

			File f = new File(getDir(), name); 
			partFileName = f.getAbsolutePath();
			
			if(! processFile(partFileName, partSize, WriteTotal)){
				return false;
			}
			
			WriteTotal += writeSize;
		}
		
		return true;
	}
	
	/**
	 * generate a .properties file with # of .part file and the original file name and extension  
	 * @return
	 */
	public boolean configFile(){
		Properties prop = new Properties();
		File fprop = new File(getDir(), "originalfileconfig.properties");
		
		String numPart = Integer.toString(partNum);
		prop.setProperty("fileName", fileName);
		prop.setProperty("numOfParts", numPart);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fprop); 
			prop.store(out, "config file from server contained original fileName and number of .part files");
			out.close();
		} catch (IOException e) {
			if(out != null){
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * @return the path where all the .part file and the properties file stored
	 */
	public String fileDestination(){
		String path = getDir();
		return path;
	}
}

class ReadConfigFile{
	private String configFilePath;
	private Properties prop = null;
	private FileInputStream fis = null;
	
	
	public ReadConfigFile(String path){
		configFilePath = path;
		
		prop = new Properties();
		try {
			fis = new FileInputStream(configFilePath);
			prop.load(fis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getFilePath(){
		String path = prop.getProperty("path");
		return path;
	}
	
	public int getPartFileSize(){
		int buffersize = Integer.parseInt(prop.getProperty("filesize"));
		return buffersize;
	}
}
