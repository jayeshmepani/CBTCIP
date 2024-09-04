package com.library.admin;

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

public class AdminDashboard extends JFrame {

    private JButton addBookButton;
    private JButton removeBookButton;
    private JButton modifyBookButton;
    private JButton addMemberButton;
    private JButton removeMemberButton;
    private JButton modifyMemberButton;
    private JTextField bookTitleField;
    private JTextField bookAuthorField;
    private JTextField bookCategoryField;
    private JTextField bookQuantityField;
    private JTextField memberIdField;
    private JTextField memberNameField;
    private JTextField memberEmailField;
    private JTextField memberPhoneField;
    private JTextField memberMembershipDateField;
    private JPanel mainPanel;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 2)); // Adjusted to fit dynamic content

        // Book fields
        inputPanel.add(new JLabel("Book Title:"));
        bookTitleField = new JTextField();
        inputPanel.add(bookTitleField);

        inputPanel.add(new JLabel("Book Author:"));
        bookAuthorField = new JTextField();
        inputPanel.add(bookAuthorField);

        inputPanel.add(new JLabel("Book Category:"));
        bookCategoryField = new JTextField();
        inputPanel.add(bookCategoryField);

        inputPanel.add(new JLabel("Book Quantity:"));
        bookQuantityField = new JTextField();
        inputPanel.add(bookQuantityField);

        // Member fields
        inputPanel.add(new JLabel("Member ID:"));
        memberIdField = new JTextField();
        inputPanel.add(memberIdField);

        inputPanel.add(new JLabel("Member Name:"));
        memberNameField = new JTextField();
        inputPanel.add(memberNameField);

        inputPanel.add(new JLabel("Member Email:"));
        memberEmailField = new JTextField();
        inputPanel.add(memberEmailField);

        inputPanel.add(new JLabel("Member Phone:"));
        memberPhoneField = new JTextField();
        inputPanel.add(memberPhoneField);

        inputPanel.add(new JLabel("Membership Date (YYYY-MM-DD):"));
        memberMembershipDateField = new JTextField();
        inputPanel.add(memberMembershipDateField);

        // Buttons
        addBookButton = new JButton("Add Book");
        removeBookButton = new JButton("Remove Book");
        modifyBookButton = new JButton("Modify Book");
        addMemberButton = new JButton("Add Member");
        removeMemberButton = new JButton("Remove Member");
        modifyMemberButton = new JButton("Modify Member");

        // Adding buttons
        inputPanel.add(addBookButton);
        inputPanel.add(removeBookButton);
        inputPanel.add(modifyBookButton);
        inputPanel.add(addMemberButton);
        inputPanel.add(removeMemberButton);
        inputPanel.add(modifyMemberButton);

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        addBookButton.addActionListener(e -> addBook());
        removeBookButton.addActionListener(e -> removeBook());
        modifyBookButton.addActionListener(e -> modifyBook());
        addMemberButton.addActionListener(e -> addMember());
        removeMemberButton.addActionListener(e -> removeMember());
        modifyMemberButton.addActionListener(e -> modifyMember());
    }

    private void addBook() {
        String title = bookTitleField.getText();
        String author = bookAuthorField.getText();
        String category = bookCategoryField.getText();
        String quantityText = bookQuantityField.getText();
        int quantity;

        // Input validation
        if (title.isEmpty() || author.isEmpty() || category.isEmpty() || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all book fields.");
            return;
        }

        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a number.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO Books (title, author, category, availability) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, category);
            stmt.setBoolean(4, true); // New books are available
            stmt.executeUpdate();

            // Adding initial quantity
            String sqlQuantity = "INSERT INTO BookCopies (book_id) VALUES ((SELECT book_id FROM Books WHERE title = ? AND author = ? AND category = ? ORDER BY book_id DESC LIMIT 1))";
            PreparedStatement stmtQuantity = conn.prepareStatement(sqlQuantity);
            stmtQuantity.setString(1, title);
            stmtQuantity.setString(2, author);
            stmtQuantity.setString(3, category);
            for (int i = 0; i < quantity; i++) {
                stmtQuantity.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Book added successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding book.");
        }
    }

    private void removeBook() {
        String bookId = JOptionPane.showInputDialog(this, "Enter Book ID to Remove:");

        if (bookId == null || bookId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Book ID cannot be empty.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Remove book copies
            String removeCopiesSql = "DELETE FROM BookCopies WHERE book_id = ?";
            PreparedStatement removeCopiesStmt = conn.prepareStatement(removeCopiesSql);
            removeCopiesStmt.setString(1, bookId);
            removeCopiesStmt.executeUpdate();

            // Remove the book
            String removeBookSql = "DELETE FROM Books WHERE book_id = ?";
            PreparedStatement removeBookStmt = conn.prepareStatement(removeBookSql);
            removeBookStmt.setString(1, bookId);
            removeBookStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book removed successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error removing book.");
        }
    }

    private void modifyBook() {
        String bookId = JOptionPane.showInputDialog(this, "Enter Book ID to Modify:");
        if (bookId == null || bookId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Book ID cannot be empty.");
            return;
        }

        String newTitle = JOptionPane.showInputDialog(this, "Enter New Title:");
        String newAuthor = JOptionPane.showInputDialog(this, "Enter New Author:");
        String newCategory = JOptionPane.showInputDialog(this, "Enter New Category:");
        String newQuantityText = JOptionPane.showInputDialog(this, "Enter New Quantity:");
        int newQuantity;

        if (newTitle == null || newAuthor == null || newCategory == null || newQuantityText == null ||
            newTitle.trim().isEmpty() || newAuthor.trim().isEmpty() || newCategory.trim().isEmpty() || newQuantityText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide all book details.");
            return;
        }

        try {
            newQuantity = Integer.parseInt(newQuantityText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a number.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Update book details
            String updateBookSql = "UPDATE Books SET title = ?, author = ?, category = ? WHERE book_id = ?";
            PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql);
            updateBookStmt.setString(1, newTitle);
            updateBookStmt.setString(2, newAuthor);
            updateBookStmt.setString(3, newCategory);
            updateBookStmt.setString(4, bookId);
            updateBookStmt.executeUpdate();

            // Remove old copies and add new ones
            String removeOldCopiesSql = "DELETE FROM BookCopies WHERE book_id = ?";
            PreparedStatement removeOldCopiesStmt = conn.prepareStatement(removeOldCopiesSql);
            removeOldCopiesStmt.setString(1, bookId);
            removeOldCopiesStmt.executeUpdate();

            String addCopiesSql = "INSERT INTO BookCopies (book_id) VALUES (?)";
            PreparedStatement addCopiesStmt = conn.prepareStatement(addCopiesSql);
            for (int i = 0; i < newQuantity; i++) {
                addCopiesStmt.setString(1, bookId);
                addCopiesStmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Book modified successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error modifying book.");
        }
    }

    private void addMember() {
        String name = memberNameField.getText();
        String email = memberEmailField.getText();
        String phone = memberPhoneField.getText();
        String membershipDateText = memberMembershipDateField.getText();
        Date membershipDate;

        // Input validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || membershipDateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all member fields.");
            return;
        }

        try {
            membershipDate = Date.valueOf(membershipDateText);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO Members (name, email, phone, membership_date) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setDate(4, membershipDate);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Member added successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding member.");
        }
    }

    private void removeMember() {
        String memberId = JOptionPane.showInputDialog(this, "Enter Member ID to Remove:");

        if (memberId == null || memberId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Member ID cannot be empty.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Check if the member exists
            String checkSql = "SELECT * FROM Members WHERE member_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, memberId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Remove member
                String removeMemberSql = "DELETE FROM Members WHERE member_id = ?";
                PreparedStatement removeMemberStmt = conn.prepareStatement(removeMemberSql);
                removeMemberStmt.setString(1, memberId);
                removeMemberStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Member removed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Member not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error removing member.");
        }
    }

    private void modifyMember() {
        String memberId = JOptionPane.showInputDialog(this, "Enter Member ID to Modify:");
        if (memberId == null || memberId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Member ID cannot be empty.");
            return;
        }

        String newName = JOptionPane.showInputDialog(this, "Enter New Name:");
        String newEmail = JOptionPane.showInputDialog(this, "Enter New Email:");
        String newPhone = JOptionPane.showInputDialog(this, "Enter New Phone:");
        String newMembershipDateText = JOptionPane.showInputDialog(this, "Enter New Membership Date (YYYY-MM-DD):");
        Date newMembershipDate;

        if (newName == null || newEmail == null || newPhone == null || newMembershipDateText == null ||
            newName.trim().isEmpty() || newEmail.trim().isEmpty() || newPhone.trim().isEmpty() || newMembershipDateText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide all member details.");
            return;
        }

        try {
            newMembershipDate = Date.valueOf(newMembershipDateText);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Update member details
            String updateMemberSql = "UPDATE Members SET name = ?, email = ?, phone = ?, membership_date = ? WHERE member_id = ?";
            PreparedStatement updateMemberStmt = conn.prepareStatement(updateMemberSql);
            updateMemberStmt.setString(1, newName);
            updateMemberStmt.setString(2, newEmail);
            updateMemberStmt.setString(3, newPhone);
            updateMemberStmt.setDate(4, newMembershipDate);
            updateMemberStmt.setString(5, memberId);
            updateMemberStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Member modified successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error modifying member.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminDashboard adminDashboard = new AdminDashboard();
            adminDashboard.setVisible(true);
        });
    }
}
