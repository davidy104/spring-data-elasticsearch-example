package nz.co.springdata.elasticsearch;

import static org.elasticsearch.index.query.QueryBuilders.idsQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryString;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.fuzzyLikeThisQuery;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import nz.co.springdata.elasticsearch.config.ApplicationContextConfig;
import nz.co.springdata.elasticsearch.data.entities.Book;
import nz.co.springdata.elasticsearch.data.repositories.BookRepository;

import org.elasticsearch.index.query.TermsQueryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationContextConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class BookBasicQueryIntegrationTest {
	@Resource
	private BookRepository bookRepository;

	@Resource
	private ElasticsearchTemplate elasticsearchTemplate;

	private List<Book> books = null;

	private int existedBooksSize = 0;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BookBasicQueryIntegrationTest.class);

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
	public void termQueryTest() {
		Book foundBook = null;
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
				termQuery("title", "crime")).build();
		Page<Book> books = bookRepository.search(searchQuery);
		assertThat(books.getContent().size(), is(1));
		foundBook = books.getContent().get(0);
		LOGGER.info("foundBook:{} ", foundBook);
	}

	@Test
	public void termsQueryTest() {
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
				new TermsQueryBuilder("tags", "novel", "education")).build();
		Page<Book> books = bookRepository.search(searchQuery);
		assertThat(books.getContent().size(), is(3));
	}

	/**
	 * same as findAll
	 */
	@Test
	public void matchAllQueryTest() {
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
				matchAllQuery())
		// .withPageable(new PageRequest(0,3))
				.build();
		Page<Book> books = bookRepository.search(searchQuery);
		assertThat(books.getContent().size(), is(existedBooksSize));
	}

	@Test
	public void matchQueryTest() {
		String expectedTitle = "Crime and Punishment";
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
				matchQuery("title", "crime and punishment")).build();
		Page<Book> books = bookRepository.search(searchQuery);
		assertThat(books.getContent().size(), is(1));

		Book foundBook = books.getContent().get(0);
		LOGGER.info("foundBook:{} ", foundBook);
		assertThat(foundBook.getTitle(), is(equalTo(expectedTitle)));
	}

	@Test
	public void multiMatchQueryTest() {
		String expectedTitle = "Crime and Punishment";
		String queryText = "crime punishment";
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
				multiMatchQuery(queryText, "title", "otitle")).build();
		Page<Book> books = bookRepository.search(searchQuery);
		assertThat(books.getContent().size(), is(1));

		Book foundBook = books.getContent().get(0);
		LOGGER.info("foundBook:{} ", foundBook);
		assertThat(foundBook.getTitle(), is(equalTo(expectedTitle)));
	}

	/**
	 * the query_string query supports the full Apache Lucene query syntax
	 */
	@Test
	public void queryStringQueryTest() {
		String expectedTitle = "Crime and Punishment";
		String queryString = "title:crime^10 +title:punishment -otitle:cat +author:(+Fyodor +dostoevsky)";

		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
				queryString(queryString)).build();
		Page<Book> books = bookRepository.search(searchQuery);
		assertThat(books.getContent().size(), is(1));

		Book foundBook = books.getContent().get(0);
		LOGGER.info("foundBook:{} ", foundBook);
		assertThat(foundBook.getTitle(), is(equalTo(expectedTitle)));
	}

	@Test
	public void identifiersQueryTest() {
		List<String> ids = new ArrayList<String>();
		ids.add("1");
		ids.add("2");
		ids.add("3");
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
				idsQuery("book").ids("1", "2", "3")).build();
		Page<Book> books = bookRepository.search(searchQuery);
		assertThat(books.getContent().size(), is(3));
	}

	@Test
	public void prefixQueryTest() {
		String expectedTitle = "Crime and Punishment";
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
				prefixQuery("title", "cri")).build();
		Page<Book> books = bookRepository.search(searchQuery);
		assertThat(books.getContent().size(), is(1));

		Book foundBook = books.getContent().get(0);
		LOGGER.info("foundBook:{} ", foundBook);
		assertThat(foundBook.getTitle(), is(equalTo(expectedTitle)));
	}

	@Test
	public void prefixAndBoostQueryTest() {
		String expectedTitle = "Crime and Punishment";
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
				prefixQuery("title", "cri").boost(3.0f)).build();
		Page<Book> books = bookRepository.search(searchQuery);
		assertThat(books.getContent().size(), is(1));

		Book foundBook = books.getContent().get(0);
		LOGGER.info("foundBook:{} ", foundBook);
		assertThat(foundBook.getTitle(), is(equalTo(expectedTitle)));
	}

	@Test
	public void fuzzyLikeThisQueryTest() {
		String likeText = "crime punishment";
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
				fuzzyLikeThisQuery("title", "otitle").likeText(likeText))
				.build();
		Page<Book> books = bookRepository.search(searchQuery);

		assertThat(books.getContent().size(), is(1));

		Book foundBook = books.getContent().get(0);
		LOGGER.info("foundBook:{} ", foundBook);

	}

}
