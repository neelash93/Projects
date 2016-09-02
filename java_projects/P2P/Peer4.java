package edu.ufl.alexgre.P2P;

public class Peer4 {
	private static final int peer4Port = 10004; 
	
	public static void main(String[] args) {
		String configFilePath = "C:\\Users\\xiyang\\Desktop\\computer networks\\P2PTest\\Client\\c4\\client4.properties";
		Client c4 = new Client(configFilePath, peer4Port);
		c4.clientRun();
	}

}
