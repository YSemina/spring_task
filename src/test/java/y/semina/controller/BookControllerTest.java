package y.semina.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import y.semina.model.Author;
import y.semina.model.Book;
import y.semina.service.BookService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        Author author = Author.builder()
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
    void getAllBooks_WithPageNumber_ShouldPassPageParameter() throws Exception {
        List<Book> books = Arrays.asList(book2);
        Page<Book> page = new PageImpl<>(books, PageRequest.of(1, 1), 2);

        when(bookService.getAllBooks(1, 1, "title", "asc")).thenReturn(page);

        mockMvc.perform(get("/api/books?page=1&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Капитанская дочка"))
                .andExpect(jsonPath("$.number").value(1));

        verify(bookService, times(1)).getAllBooks(1, 1, "title", "asc");
    }

    @Test
    void getAllBooks_ShouldReturnPaginationMetadata() throws Exception {
        List<Book> books = Arrays.asList(book1, book2);
        Page<Book> page = new PageImpl<>(books, PageRequest.of(0, 2), 58);

        when(bookService.getAllBooks(0, 2, "title", "asc")).thenReturn(page);

        mockMvc.perform(get("/api/books?size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(58))
                .andExpect(jsonPath("$.totalPages").value(29))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));
    }

}
