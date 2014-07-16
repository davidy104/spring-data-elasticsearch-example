package nz.co.springdata.elasticsearch;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.annotation.Resource;

import nz.co.springdata.elasticsearch.config.ApplicationContextConfig;
import nz.co.springdata.elasticsearch.data.entities.Book;
import nz.co.springdata.elasticsearch.data.repositories.BookRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationContextConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class BasicBookRepositoryIntegrationTest {

	@Resource
	private BookRepository bookRepository;

	@Resource
	private ElasticsearchTemplate elasticsearchTemplate;

	private List<Book> books = null;

	private int existedBooksSize = 0;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BasicBookRepositoryIntegrationTest.class);

	@Before
	public void emptyData() {
		elasticsearchTemplate.createIndex(Book.class);
		elasticsearchTemplate.putMapping(Book.class);
		elasticsearchTemplate.refresh(Book.class, true);
		books = TestUtils.getBuckBooks();
		existedBooksSize = books.size();
		bookRepository.save(books);
	}

	@After
	public void cleanUp() {
		bookRepository.deleteAll();
		elasticsearchTemplate.deleteIndex(Book.class);
	}

	@Test
	public void crudRepositoryTest() {
		long totalCount = bookRepository.count();
		assertThat(totalCount, is(equalTo(new Long(existedBooksSize))));

		Iterable<Book> bookList = bookRepository.findAll();
		assertThat(bookList, is(notNullValue()));

		Page<Book> bookPage = bookRepository.findAll(new PageRequest(0, 2));
		assertThat(bookPage, is(notNullValue()));
		assertThat(bookPage.getNumberOfElements(), is(equalTo(2)));
		assertThat(bookPage.getTotalElements(), is(equalTo(new Long(
				existedBooksSize))));
		assertThat(bookPage.getTotalPages(), is(equalTo(3)));

		// create new book
		Book book = Book.getBuilder(String.valueOf(totalCount + 1),
				"Rafał Kuć", "Elasticsearch Server", null, 10, true, 2014)
				.build();
		book.addTags("education");
		LOGGER.info("book:{} ", book);

		bookRepository.save(book);
		// lets try to search same record in elasticsearch
		Book indexedBook = bookRepository.findOne(book.getId());
		assertThat(indexedBook, is(notNullValue()));
		assertThat(indexedBook.getId(), is(book.getId()));

		totalCount = bookRepository.count();
		assertThat(totalCount, is(equalTo(new Long(existedBooksSize + 1))));

		boolean exists = bookRepository.exists(book.getId());
		assertThat(exists, is(equalTo(true)));

		Iterable<Book> bookIterable = bookRepository.findAll(new Sort(
				new Sort.Order(Sort.Direction.ASC, "title")));
		assertThat(bookIterable, is(notNullValue()));

		// same as: bookRepository.delete(book.getId());
		bookRepository.delete(book);

		totalCount = bookRepository.count();
		assertThat(totalCount, is(equalTo(new Long(existedBooksSize))));

		// delete multiple document using collection
		bookRepository.delete(books);
		totalCount = bookRepository.count();
		assertThat(totalCount, is(equalTo(0L)));
	}

	@Test
	public void bookRepositoryGeneralFindTest() {
		String testSearchTitle = "Crime and Punishment";
		String testSearchAuthor = "Fyodor Dostoevsky";

		List<Book> foundBooks = bookRepository.findByTitle(testSearchTitle);
		assertThat(foundBooks, is(notNullValue()));
		assertThat(foundBooks.size(), is(equalTo(1)));
		Book foundBook = foundBooks.get(0);
		assertThat(foundBook.getTitle(), is(testSearchTitle));
		LOGGER.info("foundBook:{} ", foundBook);

		foundBooks = bookRepository.findByAuthor(testSearchAuthor);
		assertThat(foundBooks, is(notNullValue()));
		assertThat(foundBooks.size(), is(equalTo(1)));
		foundBook = foundBooks.get(0);
		assertThat(foundBook.getAuthor(), is(testSearchAuthor));
		LOGGER.info("foundBook:{} ", foundBook);

		foundBooks = bookRepository.findByAvailable(true);
		assertThat(foundBooks, is(notNullValue()));
		assertThat(foundBooks.size(), is(equalTo(2)));

		foundBooks = bookRepository.findByTitle(testSearchTitle,
				new PageRequest(0, 1));
		assertThat(foundBooks, is(notNullValue()));
		assertThat(foundBooks.size(), is(equalTo(1)));
	}

}
