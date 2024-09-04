import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Bank bank = new Bank();

        // Load existing data or start new
        System.out.println("Welcome to Banky!");
        System.out.println("1. Load existing data");
        System.out.println("2. Start with new data");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (choice == 1) {
            Map<String, Account> loadedAccounts = AccountDatabase.loadAccounts();
            bank.setAccounts(loadedAccounts);
            System.out.println("Accounts loaded successfully.");
        } else {
            System.out.println("Starting with a new bank system.");
        }

        // Main menu loop
        while (true) {
            System.out.println("\nBanky - Banking System");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. List Accounts");
            System.out.println("6. Save & Exit");
            System.out.print("Choose an option: ");

            int menuChoice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (menuChoice) {
                case 1:
                    System.out.print("Enter account number: ");
                    String accountNumber = scanner.nextLine();
                    System.out.print("Enter owner name: ");
                    String ownerName = scanner.nextLine();
                    // Assuming a default initial balance of 0
                    bank.createAccount(accountNumber, ownerName, BigDecimal.ZERO);
                    System.out.println("Account created successfully.");
                    break;

                case 2:
                    System.out.print("Enter account number: ");
                    accountNumber = scanner.nextLine();
                    Account account = bank.getAccount(accountNumber);
                    if (account != null) {
                        System.out.print("Enter amount to deposit: ");
                        BigDecimal depositAmount = new BigDecimal(scanner.nextDouble()).setScale(4, BigDecimal.ROUND_HALF_UP);
                        scanner.nextLine();  // Consume newline
                        account.deposit(depositAmount);
                        System.out.println("Deposit successful.");
                    } else {
                        System.out.println("Account not found.");
                    }
                    break;

                case 3:
                    System.out.print("Enter account number: ");
                    accountNumber = scanner.nextLine();
                    account = bank.getAccount(accountNumber);
                    if (account != null) {
                        System.out.print("Enter amount to withdraw: ");
                        BigDecimal withdrawAmount = new BigDecimal(scanner.nextDouble()).setScale(4, BigDecimal.ROUND_HALF_UP);
                        scanner.nextLine();  // Consume newline
                        account.withdraw(withdrawAmount);
                        System.out.println("Withdrawal successful.");
                    } else {
                        System.out.println("Account not found.");
                    }
                    break;

                case 4:
                    System.out.print("Enter your account number: ");
                    accountNumber = scanner.nextLine();
                    account = bank.getAccount(accountNumber);
                    if (account != null) {
                        System.out.print("Enter target account number: ");
                        String targetAccountNumber = scanner.nextLine();
                        Account targetAccount = bank.getAccount(targetAccountNumber);
                        if (targetAccount != null) {
                            System.out.print("Enter amount to transfer: ");
                            BigDecimal transferAmount = new BigDecimal(scanner.nextDouble()).setScale(4, BigDecimal.ROUND_HALF_UP);
                            scanner.nextLine();  // Consume newline
                            boolean success = account.transfer(targetAccount, transferAmount);
                            if (success) {
                                System.out.println("Transfer successful.");
                            } else {
                                System.out.println("Transfer failed.");
                            }
                        } else {
                            System.out.println("Target account not found.");
                        }
                    } else {
                        System.out.println("Your account not found.");
                    }
                    break;

                case 5:
                    bank.listAccounts();
                    break;

                case 6:
                    AccountDatabase.saveAccounts(bank.getAccounts());
                    System.out.println("Data saved successfully. Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
