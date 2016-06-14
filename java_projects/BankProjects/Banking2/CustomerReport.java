package Banking2;

import java.util.Iterator;

public class CustomerReport {
	public void report() {
		Bank bank = Bank.getBank();
		
		System.out.println("\t\t\tCUSTOMERS REPORT");
	    System.out.println("\t\t\t================");
	    
	    /*
	    for ( int cust_idx = 0; cust_idx < bank.getNumofCustomers(); cust_idx++ ) {
	      customer = bank.getCustomer(cust_idx);

	      System.out.println();
	      System.out.println("Customer: "
				 + customer.getLastName() + ", "
				 + customer.getFirstName());
	      
	      if (customer.getCheckingAccount() != null){
	    	  System.out.println("CheckingAccount balance: " + customer.getCheckingAccount().getBalance());
	      }
	      
	      if (customer.getSavingAccount() != null){
	    	  System.out.println("Saving account balance: " + customer.getSavingAccount().getBalance());
	      }
	      System.out.println();
	    }
	    */
		
	    Iterator<Customer> itr = bank.iteratorCustomers();
	    while(itr.hasNext()){
	    	//create a cust to represent itr.next() obj
	    	Customer cust = itr.next();
	    	
	    	System.out.println();
	    	System.out.println("Customer: " + cust.getLastName() + "," + cust.getFirstName());
	    	
	    	if(cust.getCheckingAccount() != null){
	    		 System.out.println("CheckingAccount balance: " + cust.getCheckingAccount().getBalance());
	    	}
	    	
	    	if (cust.getSavingAccount() != null){
		    	  System.out.println("Saving account balance: " + cust.getSavingAccount().getBalance());
		    }
		      System.out.println();
	    }
		
	}
}