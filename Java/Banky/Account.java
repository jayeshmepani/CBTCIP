import java.math.BigDecimal;

public class Account {
    private String accountNumber;
    private String ownerName;
    private BigDecimal balance;

    // Constructor
    public Account(String accountNumber, String ownerName, BigDecimal initialBalance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
    }

    // Deposit method
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            balance = balance.add(amount);
            System.out.println("Deposit successful. New balance: $" + balance.setScale(4, BigDecimal.ROUND_HALF_UP));
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }

    // Withdraw method
    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(balance) <= 0) {
            balance = balance.subtract(amount);
            System.out.println("Withdrawal successful. New balance: $" + balance.setScale(4, BigDecimal.ROUND_HALF_UP));
        } else {
            System.out.println("Insufficient balance or invalid amount.");
        }
    }

    // Transfer method
    public boolean transfer(Account targetAccount, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(balance) <= 0) {
            balance = balance.subtract(amount);
            targetAccount.deposit(amount);
            return true;  // Transfer successful
        }
        return false;  // Transfer failed
    }

    @Override
    public String toString() {
        return "Account Number: " + accountNumber + ", Owner: " + ownerName + ", Balance: $" + balance.setScale(4, BigDecimal.ROUND_HALF_UP);
    }

    // Getters and setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
