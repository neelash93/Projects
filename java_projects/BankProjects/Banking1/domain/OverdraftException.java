package Banking1.domain;

public class OverdraftException extends Exception{
	//serial number
	static final long serialVersionUID = -338749374027845L;
	
	//field deficit
	private double deficit;  //represent the difference between balance and withdraw amount
	
	//get and set methods for deficit
	public double getDeficit() {
		return deficit;
	}
	
	//constructor
	public OverdraftException(String msg, double deficit){
		super(msg);
		this.deficit = deficit;
	}
}
