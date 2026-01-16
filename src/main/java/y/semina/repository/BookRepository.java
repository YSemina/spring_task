package y.semina.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import y.semina.model.Book;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookRepository {

    private final JdbcTemplate template;

    public List<Book> findAllBook() {
        String sql = "SELECT * FROM books";
        return template.query(sql, new BeanPropertyRowMapper<>(Book.class));
    }

    public Optional<Book> findBookById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try {
            Book book = template.queryForObject(sql, new BeanPropertyRowMapper<>(Book.class), id);
            return Optional.of(book);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Book save(Book book) {
        String sql = "INSERT INTO books (title, publication_year, author) VALUES (?, ?, ?)";

        String title = book.getTitle();
        Integer publicationYear = book.getPublicationYear();
        String author = book.getAuthor();
        template.update(sql, title, publicationYear, author);

        sql = "SELECT MAX(id) FROM books";
        Long generatedId = template.queryForObject(sql, Long.class);
        book.setId(generatedId);
        return book;
    }

    public int update(Book book) {
        String sql = "UPDATE books SET title = ?, publication_year = ?, author = ? WHERE id = ?";
        String title = book.getTitle();
        Integer publicationYear = book.getPublicationYear();
        String author = book.getAuthor();
        Long id = book.getId();
        return template.update(sql, title, publicationYear, author, id);
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        int rowsDeleted = template.update(sql, id);
        return rowsDeleted > 0;
    }

}
