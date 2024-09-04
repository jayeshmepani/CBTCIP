package com.library.database;

import com.library.models.Book;
import com.library.models.Member;
import com.library.models.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBQueries {

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM Books";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setCategory(rs.getString("category"));
                book.setAvailability(rs.getBoolean("availability"));
                books.add(book);
            }
        }
        
        return books;
    }

    public void addBook(Book book) throws SQLException {
        String query = "INSERT INTO Books (title, author, category, availability) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getCategory());
            pstmt.setBoolean(4, book.isAvailability());
            pstmt.executeUpdate();
        }
    }

    public void updateBookAvailability(int bookId) throws SQLException {
        String procedureCall = "{call UpdateBookAvailability(?)}";
        
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(procedureCall)) {
            
            stmt.setInt(1, bookId);
            stmt.execute();
        }
    }
    

    // Implement similar methods for Member and Transaction
}
