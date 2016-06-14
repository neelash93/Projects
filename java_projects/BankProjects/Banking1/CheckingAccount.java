package Banking1;

import Banking1.domain.OverdraftException;

public class CheckingAccount extends Account{
	//field
	private Double overdraftProtection;
	private SavingAccount protectedby;
	
	//constructor
	public CheckingAccount(double balance){
		super(balance);
	}
	
	public CheckingAccount(double balance, double protect){
		super(balance);
		this.overdraftProtection = protect;
	}
	
	public CheckingAccount(double balance, double protect, SavingAccount protectby){
		super(balance);
		this.overdraftProtection = protect;
		this.protectedby = protectby;
	}
	
	//withdraw method for Checking account
	public void withdraw(double amt) throws OverdraftException{
		if (this.balance >= amt){
			this.balance -= amt;
		}else if (overdraftProtection == null && protectedby == null){
			throw new OverdraftException("no overdraft protection", amt - balance);
		}else if (overdraftProtection != null && amt <= this.balance + this.overdraftProtection){
			this.overdraftProtection -= (amt - this.balance);
			this.balance = 0;
		}else if (protectedby != null && overdraftProtection != null &&
				amt <= this.balance + this.overdraftProtection + protectedby.getBalance()){
			protectedby.withdraw(amt - this.balance - this.overdraftProtection);
			this.balance = 0;
		}else if (overdraftProtection == null && protectedby != null 
				&& protectedby.getBalance() + this.balance >= amt){
			protectedby.withdraw(amt-this.balance);
			this.balance = 0;
		}else{
			throw new OverdraftException("Insufficient funds for overdraft.", 
					amt - this.balance - this.overdraftProtection - this.protectedby.getBalance());
		}
	}
	
	//checking connect with Saving
	
	
	// get and set methods
	public double getOverdraftProtection() {
		return overdraftProtection;
	}

	public void setOverdraftProtection(double overdraftProtection) {
		this.overdraftProtection = overdraftProtection;
	}

	public SavingAccount getProtectedby() {
		return protectedby;
	}

	public void setProtectedby(SavingAccount protectedby) {
		this.protectedby = protectedby;
	}
}
