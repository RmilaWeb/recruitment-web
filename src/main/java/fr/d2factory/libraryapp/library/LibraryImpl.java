package fr.d2factory.libraryapp.library;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.Student;

import java.time.LocalDate;
import java.util.Map;

import static java.lang.Math.toIntExact;
import static java.time.temporal.ChronoUnit.DAYS;

public class LibraryImpl implements Library {

   private BookRepository bookRepository;

    public BookRepository getBookRepository() { return bookRepository; }
    public void setBookRepository(BookRepository bookRepository) { this.bookRepository = bookRepository; }


    public LibraryImpl(){
    }

    public LibraryImpl(BookRepository bookRepository){
        this.bookRepository=bookRepository;
    }


    @Override
    public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt) throws HasLateBooksException {
        if(doesMemberHasLateBooks(member)){
            throw new HasLateBooksException();
        }
        Book book = bookRepository.findBook(isbnCode);
        if(book != null){
            book.setMember(member);
            bookRepository.saveBookBorrow(book,borrowedAt);
        }
        return book;
    }

    @Override
    public void returnBook(Book book, Member member) {
        int numberOfDays = toIntExact(DAYS.between(bookRepository.findBorrowedBookDate(book),LocalDate.now()));
        bookRepository.getBorrowedBooks().remove(book);
        bookRepository.getAvailableBooks().put(book.getIsbn().getIsbnCode(),book);
        member.payBook(numberOfDays);
    }

    @Override
    public boolean doesMemberHasLateBooks(Member member) {
        int daysLimit;
        if(member.getClass().equals(Student.class)){
            daysLimit = 30;
        }else{
            daysLimit = 60;
        }
        for(Map.Entry<Book,LocalDate> entry : bookRepository.getBorrowedBooks().entrySet()){
            if(entry.getKey().getMember().equals(member) && LocalDate.now().isAfter(entry.getValue().plusDays(daysLimit))){
                return true;
            }
        }
        return false;
    }
}
