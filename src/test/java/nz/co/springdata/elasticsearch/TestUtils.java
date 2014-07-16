package nz.co.springdata.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import nz.co.springdata.elasticsearch.data.entities.Book;

public class TestUtils {

	public static List<Book> getBuckBooks() {
		List<Book> books = new ArrayList<Book>();

		Book book = Book.getBuilder("1", "Erich Maria Remarque",
				"All Quiet on the Western Front", "Im Westen nichts Neues", 1,
				true, 1929).build();
		book.addCharacters("Paul Bäumer", "Albert Kropp", "Haie Westhus",
				"Fredrich Müller", "Stanislaus Katczinsky", "Tjaden").addTags(
				"novel");
		books.add(book);

		book = Book.getBuilder("2", "Joseph Heller", "Catch-22", null, 6,
				false, 1961).build();
		book.addCharacters("John Yossarian", "Captain Aardvark",
				"Chaplain Tappman", "Colonel Cathcart", "Doctor Daneeka")
				.addTags("novel");
		books.add(book);

		book = Book.getBuilder("3", "Arthur Conan Doyle",
				"The Complete Sherlock Holmes", null, 0, false, 1936).build();
		book.addCharacters("Sherlock Holmes", "Dr. Watson", "G. Lestrade");
		books.add(book);

		book = Book.getBuilder("4", "Fyodor Dostoevsky",
				"Crime and Punishment", "Преступлéние и наказáние", 0, true,
				1886).build();
		book.addCharacters("Raskolnikov", "Sofia Semyonovna Marmeladova");
		books.add(book);

		book = Book.getBuilder("5", "Scott Cranton", "Camel Cookbook", null, 7,
				true, 2014).build();
		book.addTags("education");
		books.add(book);

		return books;
	}

}
