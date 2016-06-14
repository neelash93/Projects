package Banking1;

public class CustomerReport {
	public void report() {
		Bank bank = Bank.getBank();
		Customer customer;
		
		System.out.println("\t\t\tCUSTOMERS REPORT");
	    System.out.println("\t\t\t================");

	    for (int cust_idx = 0; cust_idx < bank.getNumofCustomers(); cust_idx++ ) {
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
	}
}