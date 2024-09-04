import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

public class Bank {
    private Map<String, Account> accounts;

    public Bank() {
        accounts = new HashMap<>();
    }

    // Create an account with an initial balance
    public void createAccount(String accountNumber, String ownerName, BigDecimal initialBalance) {
        if (!accounts.containsKey(accountNumber)) {
            Account newAccount = new Account(accountNumber, ownerName, initialBalance);
            accounts.put(accountNumber, newAccount);
            System.out.println("Account created successfully.");
        } else {
            System.out.println("Account with this number already exists.");
        }
    }

    // Retrieve an account by its number
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    // List all accounts
    public void listAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts available.");
        } else {
            for (Account account : accounts.values()) {
                System.out.println(account);
            }
        }
    }

    // Getters and setters for accounts map
    public Map<String, Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Map<String, Account> accounts) {
        this.accounts = accounts;
    }
}
