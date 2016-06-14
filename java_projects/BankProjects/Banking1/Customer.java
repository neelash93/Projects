package Banking1;

public class Customer {
	//fields
	private String firstName;
	private String lastName;
	private SavingAccount savingAccount;
	private CheckingAccount checkingAccount;
	
	//private Account account;
	
		
	//Constructor
	public Customer(String f, String l){
		firstName = f;
		lastName = l;
	}
	
	//get and set
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public SavingAccount getSavingAccount() {
		return savingAccount;
	}

	public void setSavingAccount(SavingAccount savingAccount) {
		this.savingAccount = savingAccount;
	}

	public CheckingAccount getCheckingAccount() {
		return checkingAccount;
	}

	public void setCheckingAccount(CheckingAccount checkingAccount) {
		this.checkingAccount = checkingAccount;
	}
}