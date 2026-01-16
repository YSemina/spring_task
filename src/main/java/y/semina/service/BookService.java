package y.semina.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import y.semina.model.Book;
import y.semina.repository.BookRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<Book> findAllBooks() {
        return bookRepository.findAllBook();
    }

    public Book findBookById(Long id) {
        return bookRepository.findBookById(id)
                .orElseThrow(() -> new RuntimeException("Нет книги с id " + id));
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public int updateBook(Book book) {
        return bookRepository.update(book);
    }

    public boolean deleteBook(Long id) {
        return bookRepository.deleteById(id);
    }

}
