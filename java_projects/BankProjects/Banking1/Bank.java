package Banking1;

public class Bank {
	//field
	private Customer[] customers;  //store customers
	private int numofCustomers;  //account how many customers the bank has
	
	//method
	//private constructor
	private Bank(){
		customers = new Customer[10];
	}
	
	//create static bank obj
	private static Bank bank = new Bank();
	
	//create a method for get bank
	public static Bank getBank(){
		return bank;
	}
	
	//add customer to bank
	public void addCustomer(String f, String l){
		Customer cust = new Customer(f,l);
		customers[numofCustomers] = cust;
		numofCustomers ++;
		System.out.println("An account for " + f + " " + l + " has been created.");
	}
	
	//return number of customers
	public int getNumofCustomers(){
		return numofCustomers;
	}
	
	//return index customer (specific)
	public Customer getCustomer(int index){
		return customers[index];
	}
	
	//search customer's index using name
	public void searchCustomer(String f, String l){
		int flag = 0;
		int index1 = -1;
		if (numofCustomers == 0){
			flag = 2; 
		}else{
			for (int j = 0; j < numofCustomers; j++){
				if (customers[j].getFirstName() == f &&
						customers[j].getLastName() == l){
					flag = 1;
					index1 = j;
					break;
				}
			}
		}
		if(flag == 0){
			System.out.println("Customer is not found.");
		}else if(flag == 2){
			System.out.println("No customer in the bank.");
		}else if(flag == 1){
			System.out.println("Customer is found, the index of the customer is " + index1);
		}
	}
}
