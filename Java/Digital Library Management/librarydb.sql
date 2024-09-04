CREATE DATABASE IF NOT EXISTS librarydb;

USE librarydb;

-- Drop existing tables to avoid conflicts
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS Transactions;
DROP TABLE IF EXISTS BookCopies;
DROP TABLE IF EXISTS Members;
DROP TABLE IF EXISTS Books;
SET FOREIGN_KEY_CHECKS = 1;

-- Create Books table
CREATE TABLE Books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    category VARCHAR(100),
    availability BOOLEAN DEFAULT TRUE
);

-- Create Members table
CREATE TABLE Members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20),
    membership_date DATE
);

-- Create BookCopies table
CREATE TABLE BookCopies (
    copy_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT,
    is_issued BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (book_id) REFERENCES Books(book_id)
);

-- Create Transactions table
CREATE TABLE Transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    copy_id INT,
    member_id INT,
    issue_date DATE,
    return_date DATE,
    fine DECIMAL(5, 2) DEFAULT 0.00,
    FOREIGN KEY (copy_id) REFERENCES BookCopies(copy_id),
    FOREIGN KEY (member_id) REFERENCES Members(member_id)
);

-- Stored Procedure to Update Book Availability
DELIMITER //

CREATE PROCEDURE UpdateBookAvailability(IN p_book_id INT)
BEGIN
    UPDATE Books b
    JOIN (
        SELECT
            book_id,
            COUNT(*) AS total_copies,
            COUNT(CASE WHEN is_issued = TRUE THEN 1 END) AS issued_copies,
            COUNT(CASE WHEN is_issued = FALSE THEN 1 END) AS available_copies
        FROM
            BookCopies
        GROUP BY
            book_id
    ) bc ON b.book_id = bc.book_id
    SET b.availability = bc.available_copies
    WHERE b.book_id = p_book_id;
END //

DELIMITER ;
