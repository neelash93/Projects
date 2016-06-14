package Banking2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Bank {
	//field
	private List<Customer> customers;  //store customers
	
	//method
	//singleton design
	//private constructor
	private Bank(){
		customers = new ArrayList<Customer>();
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
		customers.add(cust);
		System.out.println("An account for " + f + " " + l + " has been created.");
	}
	
	//return number of customers
	public int getNumofCustomers(){
		return customers.size();
	}
	
	//return index customer (specific)
	public Customer getCustomer(int index){
		return customers.get(index);
	}
	
	//search customer's index using name
	public void searchCustomer(String f, String l){
		int flag = 0;
		int index1 = -1;
		if (customers.size() == 0){
			flag = 2; 
		}else{
			for (int j = 0; j < customers.size(); j++){
				if (customers.get(j).getFirstName() == f &&
						customers.get(j).getLastName() == l){
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
	
	//Iterator
	public Iterator<Customer> iteratorCustomers(){
		return customers.iterator();
	}
}
