package edu.ufl.alexgre.P2P;

public class Peer1 {
	private static final int peer1Port = 10001; 
	
	public static void main(String[] args) {
		String configFilePath = "C:\\Users\\xiyang\\Desktop\\computer networks\\P2PTest\\Client\\c1\\client1.properties";
		Client c1 = new Client(configFilePath, peer1Port);
		c1.clientRun();
	}
}
