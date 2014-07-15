package nz.co.springdata.elasticsearch.data.repositories;

import nz.co.springdata.elasticsearch.data.entities.Book;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookRepository extends ElasticsearchRepository<Book, String> {

}
