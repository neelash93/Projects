package Banking1;

public class SavingAccount extends Account{
	//field
	private double interestRate;
	
	//constructor
	public SavingAccount(double balance, double interstRate){
		super(balance);
		this.interestRate = interstRate; 
	}
	
	//get and set methods
	public double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}
	
	
}
