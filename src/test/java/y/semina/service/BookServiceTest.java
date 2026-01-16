package y.semina.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import y.semina.model.Book;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookServiceTest {

    @Autowired
    BookService bookService;

    @Test
    @Order(1)
    void should_Return_All_Books() {
        List<Book> books = bookService.findAllBooks();
        assertNotNull(books);
        assertEquals(4, books.size());
        assertEquals("Война и мир", books.get(0).getTitle());
        assertEquals(1866, books.get(1).getPublicationYear());
        assertEquals("Джордж Оруэлл", books.get(2).getAuthor());
        assertEquals("Мастер и Маргарита", books.get(3).getTitle());

    }

    @Test
    @Order(2)
    void should_ReturnBook_WhenBookExists() {
        Book book = bookService.findBookById(1L);
        assertEquals("Война и мир", book.getTitle());
        assertEquals(1869, book.getPublicationYear());
        assertEquals("Лев Толстой", book.getAuthor());
    }

    @Test
    @Order(3)
    void should_ThrowException_WhenBookNotFound() {
        assertThrows(RuntimeException.class, () -> bookService.findBookById(15L));
    }

    @Test
    @Order(4)
    void should_Save_Book(){
        Book book = Book.builder()
                .title("New book")
                .publicationYear(1900)
                .author("New Author")
                .build();
        Book savedBook = bookService.saveBook(book);
        assertEquals(5L, savedBook.getId());
        assertEquals("New book", savedBook.getTitle());
        assertEquals(1900, savedBook.getPublicationYear());
        assertEquals("New Author", savedBook.getAuthor());
    }

    @Test
    @Order(5)
    void should_UpdateBook_WhenBookExists(){
        Book book = Book.builder()
                .id(1L)
                .title("Война и мир")
                .publicationYear(1900)
                .author("New Author")
                .build();
        assertEquals(1,bookService.updateBook(book));
        Book updatedBook = bookService.findBookById(1L);
        assertEquals(1L, updatedBook.getId());
        assertEquals("Война и мир", updatedBook.getTitle());
        assertEquals(1900, updatedBook.getPublicationYear());
        assertEquals("New Author", updatedBook.getAuthor());
    }

    @Test
    @Order(6)
    void should_DeleteBook_WhenBookExists(){
        bookService.deleteBook(1L);
        assertEquals(4,bookService.findAllBooks().size());
    }

}
