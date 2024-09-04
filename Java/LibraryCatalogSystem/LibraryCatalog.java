import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LibraryCatalog {
    private List<Book> books;

    public LibraryCatalog() {
        books = LibraryUtil.loadBooks(); // Load existing books on initialization
    }

    public void addBook(Book book) {
        books.add(book);
        System.out.println("Book added successfully.");
    }

    public void searchByTitle(String title) {
        boolean found = false;
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                System.out.println(book);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No book found with the title: " + title);
        }
    }

    public void searchByAuthor(String author) {
        boolean found = false;
        for (Book book : books) {
            if (book.getAuthor().equalsIgnoreCase(author)) {
                System.out.println(book);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No book found with the author: " + author);
        }
    }

    public void listBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available in the catalog.");
        } else {
            for (Book book : books) {
                System.out.println(book);
            }
        }
    }

    public void saveData() {
        LibraryUtil.saveBooks(books);
    }
}
