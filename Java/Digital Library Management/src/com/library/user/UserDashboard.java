package com.library.user;

import com.library.database.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.*;


public class UserDashboard extends JFrame {

    private JButton searchBookButton;
    private JButton issueBookButton;
    private JButton returnBookButton;
    private JTextField searchField;
    private JTextField memberIdField;
    private JTextArea resultsTextArea;
    private JPanel mainPanel;
    private JPanel inputPanel;

    public UserDashboard() {
        setTitle("User Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel searchLabel = new JLabel("Search Book:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(searchLabel, gbc);

        searchField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(searchField, gbc);

        searchBookButton = new JButton("Search Book");
        gbc.gridx = 2;
        gbc.gridy = 0;
        inputPanel.add(searchBookButton, gbc);

        JLabel memberIdLabel = new JLabel("Member ID:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(memberIdLabel, gbc);

        memberIdField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(memberIdField, gbc);

        issueBookButton = new JButton("Issue Book");
        gbc.gridx = 2;
        gbc.gridy = 1;
        inputPanel.add(issueBookButton, gbc);

        returnBookButton = new JButton("Return Book");
        gbc.gridx = 2;
        gbc.gridy = 2;
        inputPanel.add(returnBookButton, gbc);

        resultsTextArea = new JTextArea();
        resultsTextArea.setEditable(false);
        resultsTextArea.setLineWrap(true);
        resultsTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultsTextArea);
        
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        searchBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBook();
            }
        });

        issueBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                issueBook();
            }
        });

        returnBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnBook();
            }
        });
    }

    private void searchBook() {
        String searchText = searchField.getText();
        StringBuilder sb = new StringBuilder();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Books WHERE title LIKE ? OR author LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + searchText + "%");
            stmt.setString(2, "%" + searchText + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String category = rs.getString("category");
                boolean available = rs.getBoolean("availability");
                sb.append(String.format("ID: %d, Title: %s, Author: %s, Category: %s, Available: %b\n", id, title, author, category, available));
            }
            resultsTextArea.setText(sb.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching for books.");
        }
    }

    private void issueBook() {
        String bookId = JOptionPane.showInputDialog(this, "Enter Book ID:");
        String memberId = memberIdField.getText();
        Date issueDate = new Date(System.currentTimeMillis());
    
        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT availability FROM Books WHERE book_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, Integer.parseInt(bookId));
            ResultSet rs = checkStmt.executeQuery();
    
            if (rs.next() && rs.getBoolean("availability")) {
                // Assuming BookCopies table is used to track individual copies
                String copySql = "SELECT copy_id FROM BookCopies WHERE book_id = ? AND is_issued = FALSE LIMIT 1";
                PreparedStatement copyStmt = conn.prepareStatement(copySql);
                copyStmt.setInt(1, Integer.parseInt(bookId));
                ResultSet copyRs = copyStmt.executeQuery();
    
                if (copyRs.next()) {
                    int copyId = copyRs.getInt("copy_id");
                    String issueSql = "INSERT INTO Transactions (copy_id, member_id, issue_date) VALUES (?, ?, ?)";
                    PreparedStatement issueStmt = conn.prepareStatement(issueSql);
                    issueStmt.setInt(1, copyId);
                    issueStmt.setInt(2, Integer.parseInt(memberId));
                    issueStmt.setDate(3, issueDate);
                    issueStmt.executeUpdate();
    
                    String updateCopySql = "UPDATE BookCopies SET is_issued = TRUE WHERE copy_id = ?";
                    PreparedStatement updateCopyStmt = conn.prepareStatement(updateCopySql);
                    updateCopyStmt.setInt(1, copyId);
                    updateCopyStmt.executeUpdate();
    
                    // Update book availability
                    String updateBookSql = "UPDATE Books SET availability = FALSE WHERE book_id = ?";
                    PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql);
                    updateBookStmt.setInt(1, Integer.parseInt(bookId));
                    updateBookStmt.executeUpdate();
    
                    // Call stored procedure to update availability
                    updateBookAvailability(Integer.parseInt(bookId));
    
                    JOptionPane.showMessageDialog(this, "Book issued successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "No available copies of this book.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Book is not available.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error issuing book.");
        }
    }
    
    private void updateBookAvailability(int bookId) throws SQLException {
        String procedureCall = "{call UpdateBookAvailability(?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(procedureCall)) {
            stmt.setInt(1, bookId);
            stmt.execute();
        }
    }
    
    private void returnBook() {
        String bookId = JOptionPane.showInputDialog(this, "Enter Book ID:");
        String memberId = memberIdField.getText();
        Date returnDate = new Date(System.currentTimeMillis());
    
        try (Connection conn = DBConnection.getConnection()) {
            // Get the copy_id from Transactions table
            String getCopyIdSql = "SELECT copy_id FROM Transactions WHERE member_id = ? AND return_date IS NULL AND copy_id IN (SELECT copy_id FROM BookCopies WHERE book_id = ? AND is_issued = TRUE)";
            PreparedStatement getCopyIdStmt = conn.prepareStatement(getCopyIdSql);
            getCopyIdStmt.setInt(1, Integer.parseInt(memberId));
            getCopyIdStmt.setInt(2, Integer.parseInt(bookId));
            ResultSet rs = getCopyIdStmt.executeQuery();
    
            if (rs.next()) {
                int copyId = rs.getInt("copy_id");
    
                // Update the Transactions table with return date
                String returnSql = "UPDATE Transactions SET return_date = ? WHERE copy_id = ? AND member_id = ? AND return_date IS NULL";
                PreparedStatement returnStmt = conn.prepareStatement(returnSql);
                returnStmt.setDate(1, returnDate);
                returnStmt.setInt(2, copyId);
                returnStmt.setInt(3, Integer.parseInt(memberId));
                returnStmt.executeUpdate();
    
                // Update BookCopies table to mark the copy as returned
                String updateCopySql = "UPDATE BookCopies SET is_issued = FALSE WHERE copy_id = ?";
                PreparedStatement updateCopyStmt = conn.prepareStatement(updateCopySql);
                updateCopyStmt.setInt(1, copyId);
                updateCopyStmt.executeUpdate();
    
                // Update Books table to reflect availability
                String updateBookSql = "UPDATE Books SET availability = TRUE WHERE book_id = ?";
                PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql);
                updateBookStmt.setInt(1, Integer.parseInt(bookId));
                updateBookStmt.executeUpdate();
    
                // Call stored procedure to update availability
                updateBookAvailability(Integer.parseInt(bookId));
    
                JOptionPane.showMessageDialog(this, "Book returned successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "No record of this book being issued.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error returning book.");
        }
    }    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserDashboard userDashboard = new UserDashboard();
            userDashboard.setVisible(true);
        });
    }
}
