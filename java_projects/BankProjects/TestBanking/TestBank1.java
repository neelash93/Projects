package TestBanking;

import Banking1.domain.OverdraftException;
import Banking2.*;

public class TestBank1 {
	public static void main(String[] args) {
		Bank bank = Bank.getBank();
	    Customer customer;
	    CustomerReport report = new CustomerReport();

	    // Create several customers and their accounts
	    bank.addCustomer("Jane", "Simms");
	    customer = bank.getCustomer(0);
	    customer.setSavingAccount(new SavingAccount(500.00, 0.05));
	    customer.setCheckingAccount(new CheckingAccount(200.00, 400.00, customer.getSavingAccount()));
	    try{
	    	customer.getCheckingAccount().withdraw(1200);
	    	
	    }catch(OverdraftException e){
	    	System.out.println(e.getMessage()+ " " + e.getDeficit());
	    }

	    bank.addCustomer("Owen", "Bryant");
	    customer = bank.getCustomer(1);
	    customer.setCheckingAccount(new CheckingAccount(200.00));
	    try{
	    	customer.getCheckingAccount().withdraw(250);
	    }catch(OverdraftException e){
	    	System.out.println(e.getMessage() + "  " + e.getDeficit());
	    }

	    bank.addCustomer("Tim", "Soley");
	    customer = bank.getCustomer(2);
	    customer.setSavingAccount(new SavingAccount(1500.00, 0.05));
	    customer.setCheckingAccount(new CheckingAccount(200.00));

	    bank.addCustomer("Maria", "Soley");
	    customer = bank.getCustomer(3);
	    // Maria and Tim have a shared checking account
	    customer.setCheckingAccount(bank.getCustomer(2).getCheckingAccount());
	    customer.setSavingAccount(new SavingAccount(150.00, 0.05));
	    
	    System.out.println(bank.getNumofCustomers());
	    // Generate a report
	    report.report();
	}
}
