package fr.d2factory.libraryapp.book;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The book repository emulates a database via 2 HashMaps
 */
public class BookRepository {
    private Map<Long, Book> availableBooks = new HashMap<>();
    private Map<Book, LocalDate> borrowedBooks = new HashMap<>();

    public void addBooks(List<Book> books){
        for(Book book : books){
            availableBooks.put(book.getIsbn().isbnCode,book);
        }
    }

    public Book findBook(long isbnCode) {
        return availableBooks.get(isbnCode);
    }

    public void saveBookBorrow(Book book, LocalDate borrowedAt){
        availableBooks.remove(book.getIsbn().getIsbnCode());
        borrowedBooks.put(book,borrowedAt);
    }

    public LocalDate findBorrowedBookDate(Book book) {
        return borrowedBooks.get(book);
    }

    public Map<Long, Book> getAvailableBooks() {
        return availableBooks;
    }

    public void setAvailableBooks(Map<Long, Book> availableBooks) {
        this.availableBooks = availableBooks;
    }

    public Map<Book, LocalDate> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(Map<Book, LocalDate> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }
}
