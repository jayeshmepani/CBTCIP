import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        LibraryCatalog catalog = new LibraryCatalog();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Library Catalog System!");
        System.out.println("1. Load existing data");
        System.out.println("2. Start with new data");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (choice == 2) {
            catalog = new LibraryCatalog(); // Reset catalog to new instance
            System.out.println("Starting with a new catalog.");
        } else if (choice != 1) {
            System.out.println("Invalid choice. Starting with existing data.");
        }

        // Main menu loop
        while (true) {
            System.out.println("\nLibrary Catalog System");
            System.out.println("1. Add Book");
            System.out.println("2. Search by Title");
            System.out.println("3. Search by Author");
            System.out.println("4. List All Books");
            System.out.println("5. Save & Exit");
            System.out.print("Choose an option: ");

            int menuChoice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (menuChoice) {
                case 1:
                    System.out.print("Enter title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter author: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter ISBN: ");
                    String isbn = scanner.nextLine();
                    catalog.addBook(new Book(title, author, isbn));
                    break;

                case 2:
                    System.out.print("Enter title to search: ");
                    String searchTitle = scanner.nextLine();
                    catalog.searchByTitle(searchTitle);
                    break;

                case 3:
                    System.out.print("Enter author to search: ");
                    String searchAuthor = scanner.nextLine();
                    catalog.searchByAuthor(searchAuthor);
                    break;

                case 4:
                    catalog.listBooks();
                    break;

                case 5:
                    catalog.saveData();
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
