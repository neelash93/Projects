package edu.ufl.alexgre.P2P;

public class Peer5 {
	private static final int peer5Port = 10005; 
	
	public static void main(String[] args) {
		String configFilePath = "C:\\Users\\xiyang\\Desktop\\computer networks\\P2PTest\\Client\\c5\\client5.properties";
		Client c5 = new Client(configFilePath, peer5Port);
		c5.clientRun();
	}

}
