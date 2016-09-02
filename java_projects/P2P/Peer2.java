package edu.ufl.alexgre.P2P;

public class Peer2 {
	private static final int peer2Port = 10002; 
	
	public static void main(String[] args) {
		String configFilePath = "C:\\Users\\xiyang\\Desktop\\computer networks\\P2PTest\\Client\\c2\\client2.properties";
		Client c2 = new Client(configFilePath, peer2Port);
		c2.clientRun();
	}
}
