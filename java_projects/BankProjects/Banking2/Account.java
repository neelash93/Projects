package Banking2;

import Banking1.domain.OverdraftException;

public class Account {
	//field balance
	protected double balance;
	
	//constructor
	public Account(double init_balance){
		balance = init_balance;
	}
	//overload constructor
	public Account(){
		balance = 0;
	}
	
	//visit balance
	public double getBalance(){
		return balance;
	}
	
	//deposite
	public boolean deposit(double depositAmount){
		balance += depositAmount;
		System.out.println("Depositing " + depositAmount);
		return true;
	}
	
	public void withdraw(double withdrawAmount) throws OverdraftException{
		if (balance >= withdrawAmount){
			balance -= withdrawAmount;
		}else{
			throw new OverdraftException("not enough balance.", withdrawAmount - balance);
			}
	}
}
