package y.semina.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import y.semina.exeption.BookNotFoundException;
import y.semina.model.Book;
import y.semina.repository.BookRepository;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Page<Book> getAllBooks(int page, int size, String sortBy, String direction) {
        Sort sort = createSort(sortBy, direction);
        Pageable pageable = PageRequest.of(page, size, sort);
        return bookRepository.findAllWithAuthor(pageable);
    }

    public Book getBookById(Long id) {
        return bookRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new BookNotFoundException("Книга с ID " + id + " не найдена"));
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book bookDetails) {
        Book book = getBookById(id);

        book.setTitle(bookDetails.getTitle());
        book.setPublicationYear(bookDetails.getPublicationYear());
        book.setPageCount(bookDetails.getPageCount());
        book.setAuthor(bookDetails.getAuthor());

        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        getBookById(id);
        bookRepository.deleteById(id);
    }

    private Sort createSort(String sortBy, String direction) {
        return direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
    }

}
