package y.semina.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import y.semina.exeption.BookNotFoundException;
import y.semina.model.Author;
import y.semina.model.Book;
import y.semina.repository.BookRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1;
    private Book book2;
    private Author author;

    @BeforeEach
    void setUp() {
        author = Author.builder()
                .id(1L)
                .firstName("Александр")
                .lastName("Пушкин")
                .build();

        book1 = Book.builder()
                .id(1L)
                .title("Евгений Онегин")
                .publicationYear(1833)
                .pageCount(320)
                .author(author)
                .build();

        book2 = Book.builder()
                .id(2L)
                .title("Капитанская дочка")
                .publicationYear(1836)
                .pageCount(256)
                .author(author)
                .build();
    }

    @Test
    void getAllBooks_WithDefaultPagination_ShouldReturnFirstPage() {
        List<Book> books = Arrays.asList(book1, book2);
        Page<Book> page = new PageImpl<>(books);

        when(bookRepository.findAllWithAuthor(any(Pageable.class))).thenReturn(page);

        Page<Book> result = bookService.getAllBooks(0, 10, "title", "asc");

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Евгений Онегин", result.getContent().get(0).getTitle());
        assertEquals("Капитанская дочка", result.getContent().get(1).getTitle());

        verify(bookRepository, times(1)).findAllWithAuthor(any(Pageable.class));
    }

    @Test
    void getAllBooks_WithCustomPageSize_ShouldReturnCorrectNumberOfBooks() {
        List<Book> books = Arrays.asList(book1);
        Page<Book> page = new PageImpl<>(books);

        when(bookRepository.findAllWithAuthor(argThat(pageable ->
                pageable.getPageSize() == 5 && pageable.getPageNumber() == 0)))
                .thenReturn(page);
        Page<Book> result = bookService.getAllBooks(0, 5, "title", "asc");

        assertEquals(1, result.getContent().size());
        verify(bookRepository).findAllWithAuthor(argThat(pageable ->
                pageable.getPageSize() == 5));
    }

    @Test
    void getAllBooks_WithSecondPage_ShouldReturnPage2() {
        List<Book> books = Arrays.asList(book2);
        Page<Book> page = new PageImpl<>(books, PageRequest.of(1, 1), 2);

        when(bookRepository.findAllWithAuthor(argThat(pageable ->
                pageable.getPageNumber() == 1)))
                .thenReturn(page);

        Page<Book> result = bookService.getAllBooks(1, 1, "title", "asc");

        assertEquals(1, result.getContent().size());
        assertEquals("Капитанская дочка", result.getContent().get(0).getTitle());
        assertEquals(1, result.getNumber());
        verify(bookRepository).findAllWithAuthor(argThat(pageable ->
                pageable.getPageNumber() == 1));
    }

    @Test
    void getAllBooks_WithSortingAscending_ShouldReturnSortedBooks() {
        List<Book> books = Arrays.asList(book1, book2);
        Page<Book> page = new PageImpl<>(books);

        when(bookRepository.findAllWithAuthor(argThat(pageable ->
                pageable.getSort().getOrderFor("title").getDirection() == Sort.Direction.ASC)))
                .thenReturn(page);

        Page<Book> result = bookService.getAllBooks(0, 10, "title", "asc");

        assertEquals(2, result.getContent().size());
        verify(bookRepository).findAllWithAuthor(argThat(pageable ->
                pageable.getSort().getOrderFor("title").getDirection() == Sort.Direction.ASC));
    }

    @Test
    void getAllBooks_WithSortingDescending_ShouldReturnReverseSortedBooks() {
        List<Book> books = Arrays.asList(book2, book1); // Обратный порядок
        Page<Book> page = new PageImpl<>(books);

        when(bookRepository.findAllWithAuthor(argThat(pageable ->
                pageable.getSort().getOrderFor("title").getDirection() == Sort.Direction.DESC)))
                .thenReturn(page);

        Page<Book> result = bookService.getAllBooks(0, 10, "title", "desc");

        assertEquals(2, result.getContent().size());
        assertEquals("Капитанская дочка", result.getContent().get(0).getTitle());
        verify(bookRepository).findAllWithAuthor(argThat(pageable ->
                pageable.getSort().getOrderFor("title").getDirection() == Sort.Direction.DESC));
    }

    @Test
    void getAllBooks_WithSortingByPublicationYear_ShouldSortByYear() {
        List<Book> books = Arrays.asList(book1, book2);
        Page<Book> page = new PageImpl<>(books);

        when(bookRepository.findAllWithAuthor(argThat(pageable ->
                pageable.getSort().getOrderFor("publicationYear") != null)))
                .thenReturn(page);

        Page<Book> result = bookService.getAllBooks(0, 10, "publicationYear", "asc");

        verify(bookRepository).findAllWithAuthor(argThat(pageable ->
                pageable.getSort().getOrderFor("publicationYear") != null));
    }

    @Test
    void getAllBooks_WithSortingByPageCount_ShouldSortByPages() {
        List<Book> books = Arrays.asList(book2, book1);
        Page<Book> page = new PageImpl<>(books);

        when(bookRepository.findAllWithAuthor(argThat(pageable ->
                pageable.getSort().getOrderFor("pageCount") != null)))
                .thenReturn(page);

        Page<Book> result = bookService.getAllBooks(0, 10, "pageCount", "asc");

        verify(bookRepository).findAllWithAuthor(argThat(pageable ->
                pageable.getSort().getOrderFor("pageCount") != null));
    }

    @Test
    void getAllBooks_WithEmptyResult_ShouldReturnEmptyPage() {
        Page<Book> emptyPage = new PageImpl<>(List.of());

        when(bookRepository.findAllWithAuthor(any(Pageable.class))).thenReturn(emptyPage);

        Page<Book> result = bookService.getAllBooks(0, 10, "title", "asc");

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getAllBooks_WithLargePageSize_ShouldReturnAllBooks() {
        List<Book> allBooks = createMockBooks(58);
        Page<Book> page = new PageImpl<>(allBooks, PageRequest.of(0, 100), 58);

        when(bookRepository.findAllWithAuthor(argThat(pageable ->
                pageable.getPageSize() == 100))).thenReturn(page);

        Page<Book> result = bookService.getAllBooks(0, 100, "id", "asc");

        assertEquals(58, result.getContent().size());
        assertEquals(58, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void getAllBooks_WithPageSize10And58Books_ShouldHave6Pages() {
        List<Book> firstPageBooks = createMockBooks(10);
        Page<Book> page = new PageImpl<>(firstPageBooks, PageRequest.of(0, 10), 58);

        when(bookRepository.findAllWithAuthor(argThat(pageable ->
                pageable.getPageSize() == 10 && pageable.getPageNumber() == 0)))
                .thenReturn(page);

        Page<Book> result = bookService.getAllBooks(0, 10, "id", "asc");

        assertEquals(10, result.getContent().size());
        assertEquals(58, result.getTotalElements());
        assertEquals(6, result.getTotalPages());
        assertTrue(result.isFirst());
        assertFalse(result.isLast());
    }

    private List<Book> createMockBooks(int count) {
        Author author = Author.builder()
                .id(1L)
                .firstName("Автор")
                .lastName("Тестовый")
                .build();

        List<Book> books = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            books.add(Book.builder()
                    .id((long) i)
                    .title("Книга " + i)
                    .publicationYear(1800 + i)
                    .pageCount(100 + i)
                    .author(author)
                    .build());
        }
        return books;
    }

    @Test
    void getBookById_WithExistingId_ShouldReturnBook() {
        when(bookRepository.findByIdWithAuthor(1L)).thenReturn(Optional.of(book1));

        Book result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals("Евгений Онегин", result.getTitle());
        assertEquals(1833, result.getPublicationYear());
        assertEquals(320, result.getPageCount());
        assertEquals("Александр Пушкин", result.getAuthor().getFullName());

        verify(bookRepository, times(1)).findByIdWithAuthor(1L);
    }

    @Test
    void getBookById_WithNonExistingId_ShouldThrowException() {
        when(bookRepository.findByIdWithAuthor(999L)).thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(
                BookNotFoundException.class,
                () -> bookService.getBookById(999L)
        );

        assertEquals("Книга с ID 999 не найдена", exception.getMessage());
        verify(bookRepository, times(1)).findByIdWithAuthor(999L);
    }

    @Test
    void createBook_ShouldSaveAndReturnBook() {
        Book newBook = Book.builder()
                .title("Новая книга")
                .publicationYear(2024)
                .pageCount(300)
                .author(author)
                .build();

        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            book.setId(100L);
            return book;
        });

        Book createdBook = bookService.createBook(newBook);

        assertNotNull(createdBook.getId());
        assertEquals("Новая книга", createdBook.getTitle());
        verify(bookRepository, times(1)).save(newBook);
    }

    @Test
    void updateBook_ShouldUpdateAndReturnBook() {
        when(bookRepository.findByIdWithAuthor(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.save(any(Book.class))).thenReturn(book1);

        Book updateData = Book.builder()
                .title("Обновленное название")
                .publicationYear(1900)
                .pageCount(400)
                .author(author)
                .build();

        Book updatedBook = bookService.updateBook(1L, updateData);

        assertEquals("Обновленное название", updatedBook.getTitle());
        assertEquals(1900, updatedBook.getPublicationYear());
        assertEquals(400, updatedBook.getPageCount());

        verify(bookRepository, times(1)).findByIdWithAuthor(1L);
        verify(bookRepository, times(1)).save(book1);
    }

    @Test
    void deleteBook_WithExistingId_ShouldDelete() {
        when(bookRepository.findByIdWithAuthor(1L)).thenReturn(Optional.of(book1));
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).findByIdWithAuthor(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }

}
