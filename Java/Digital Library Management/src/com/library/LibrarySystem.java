package com.library;

import com.library.admin.AdminDashboard;
import com.library.user.UserDashboard;

import javax.swing.*;

public class LibrarySystem {

    public static void main(String[] args) {
        String[] options = {"Admin", "User"};
        int choice = JOptionPane.showOptionDialog(null,
                "Select Login Role",
                "Library Management System",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            SwingUtilities.invokeLater(() -> {
                AdminDashboard adminDashboard = new AdminDashboard();
                adminDashboard.setVisible(true);
            });
        } else if (choice == 1) {
            SwingUtilities.invokeLater(() -> {
                UserDashboard userDashboard = new UserDashboard();
                userDashboard.setVisible(true);
            });
        }
    }
}
