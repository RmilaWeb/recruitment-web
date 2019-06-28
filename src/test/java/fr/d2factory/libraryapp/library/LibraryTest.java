package fr.d2factory.libraryapp.library;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.book.ISBN;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import sun.misc.IOUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class LibraryTest {
    private Library library ;
    private BookRepository bookRepository;
    private Student student;
    private Student student_first;
    private Resident resident;

    String json = "[\n" +
            "    {\n" +
            "      \"title\": \"Harry Potter\",\n" +
            "      \"author\": \"J.K. Rowling\",\n" +
            "      \"isbn\": {\n" +
            "        \"isbnCode\": 46578964513\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"title\": \"Around the world in 80 days\",\n" +
            "      \"author\": \"Jules Verne\",\n" +
            "      \"isbn\": {\n" +
            "        \"isbnCode\": 3326456467846\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"title\": \"Catch 22\",\n" +
            "      \"author\": \"Joseph Heller\",\n" +
            "      \"isbn\": {\n" +
            "        \"isbnCode\": 968787565445\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"title\": \"La peau de chagrin\",\n" +
            "      \"author\": \"Balzac\",\n" +
            "      \"isbn\": {\n" +
            "        \"isbnCode\": 465789453149\n" +
            "      }\n" +
            "    }\n" +
            "]";


    @Before
    public void setup(){

        //Instantiation of the library and the repository
        bookRepository = new BookRepository();
        library = new LibraryImpl();
        resident = new Resident(2);
        student = new Student(1,2);
        student_first = new Student(1,1);
        ((LibraryImpl)library).setBookRepository(bookRepository);

//        Gson gson = new Gson();
//        String content = "";
//        try
//        {
//            content = new String ( Files.readAllBytes( Paths.get("C:\\Users\\th3rm\\Desktop\\recruitment-web\\src\\test\\resources\\books.json") ) );
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//
//        Book[] car = gson.fromJson(content, Book[].class);
//
//        System.out.println("carrr" + car[0].getTitle());

        //Add some test books
        List<Book> books = new ArrayList<>();
        books.add(new Book("Harry Potter","J.K. Rowling",new ISBN(46578964)));
        books.add(new Book("Around the world in 80 days","Jules Verne",new ISBN(332645646)));
        books.add(new Book("La peau de chagrin","Balzac",new ISBN(465789453)));
        bookRepository.addBooks(books);
        //TODO to help you a file called books.json is available in src/test/resources
    }

    @Test
    public void member_can_borrow_a_book_if_book_is_available(){
        //A student borrows a book so the book must be in the borrowBooks map
        Book book = library.borrowBook(46578964L,student, LocalDate.now());
        assertNotNull(bookRepository.getBorrowedBooks().get(book));
    }

    @Test
    public void borrowed_book_is_no_longer_available() {
        //A resident tries to borrow a book but since it is not available it's not in the borrowBooks map
        Book book = library.borrowBook(46578964L,student, LocalDate.now());
        Book book1 = library.borrowBook(book.getIsbn().getIsbnCode(),resident,LocalDate.now());
        assertNull(book1);
    }

    @Test
    public void residents_are_taxed_10cents_for_each_day_they_keep_a_book(){
        //Here a resident borrows a book then return it 5 days later
        Book book = bookRepository.getAvailableBooks().get(46578964L);
        resident.setWallet(1f);
        library.borrowBook(book.getIsbn().getIsbnCode(),resident,LocalDate.now().minusDays(5));
        //The resident pays when he returns the book
        library.returnBook(book,resident);
        //We could also just test the payBook method
        //resident.payBook(5);
        assertEquals(0.5f,resident.getWallet(),0);
    }

    @Test
    public void     students_pay_10_cents_the_first_30days(){
        Book book = bookRepository.getAvailableBooks().get(46578964L);
        student.setWallet(3f);
        library.borrowBook(book.getIsbn().getIsbnCode(),student,LocalDate.now().minusDays(30));
        library.returnBook(book,student);

        assertEquals(0f,student.getWallet(),0);
    }

    @Test
    public void students_in_1st_year_are_not_taxed_for_the_first_15days(){
        Book book = bookRepository.getAvailableBooks().get(46578964L);
        student_first.setWallet(2f);
        //In this case the student borrowed the book 15 days ago so he won't be charged
        library.borrowBook(book.getIsbn().getIsbnCode(),student_first,LocalDate.now().minusDays(15));
        library.returnBook(book,student_first);

        assertEquals(2f,student_first.getWallet(),0);

    }

    @Test
    public void students_pay_15cents_for_each_day_they_keep_a_book_after_the_initial_30days(){
        Book book = bookRepository.getAvailableBooks().get(46578964L);
        student.setWallet(10f);
        library.borrowBook(book.getIsbn().getIsbnCode(),student,LocalDate.now().minusDays(35));
        library.returnBook(book,student);

        assertEquals(6.25f ,student.getWallet(),0);

        student_first.setWallet(10f);
        library.borrowBook(book.getIsbn().getIsbnCode(),student_first,LocalDate.now().minusDays(35));
        library.returnBook(book,student_first);

        assertEquals(7.75f,student_first.getWallet(),0);
    }

    @Test
    public void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days(){
        Book book = bookRepository.getAvailableBooks().get(46578964L);
        resident.setWallet(10f);
        library.borrowBook(book.getIsbn().getIsbnCode(),resident,LocalDate.now().minusDays(65));
        library.returnBook(book,resident);
        //We could also just test the payBook method
        //resident.payBook(5);
        assertEquals(3f,resident.getWallet(),0);
    }

    @Test(expected = HasLateBooksException.class)
    public void members_cannot_borrow_book_if_they_have_late_books(){
        Book book = bookRepository.getAvailableBooks().get(46578964L);
        Book book1 = bookRepository.getAvailableBooks().get(332645646L);
        student.setWallet(10f);
        //Simulate the borrow of a book 35 days ago
        library.borrowBook(book.getIsbn().getIsbnCode(),student,LocalDate.now().minusDays(35));
        //Now the same student tries to borrow another book
        library.borrowBook(book1.getIsbn().getIsbnCode(),student,LocalDate.now());
    }
}
