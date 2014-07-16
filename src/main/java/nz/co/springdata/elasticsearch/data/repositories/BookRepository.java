package nz.co.springdata.elasticsearch.data.repositories;

import java.util.List;

import nz.co.springdata.elasticsearch.data.entities.Book;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookRepository extends ElasticsearchRepository<Book, String> {
	List<Book> findByTitle(String title);

	List<Book> findByTitle(String title, Pageable pageable);

	List<Book> findByAuthor(String author);

	List<Book> findByAvailable(boolean available);
}
