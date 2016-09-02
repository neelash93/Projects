package edu.ufl.alexgre.P2P;

public class Peer3 {
	private static final int peer3Port = 10003; 
	
	public static void main(String[] args) {
		String configFilePath = "C:\\Users\\xiyang\\Desktop\\computer networks\\P2PTest\\Client\\c3\\client3.properties";
		Client c3 = new Client(configFilePath, peer3Port);
		c3.clientRun();
	}
}
