package nz.co.springdata.elasticsearch.data.entities;

import static org.springframework.data.elasticsearch.annotations.FieldIndex.analyzed;
import static org.springframework.data.elasticsearch.annotations.FieldIndex.not_analyzed;
import static org.springframework.data.elasticsearch.annotations.FieldType.String;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.NestedField;

@Document(indexName = "library", type = "book", shards = 1, replicas = 0, refreshInterval = "-1", indexStoreType = "memory")
public class Book {

	@Id
	private String id;

	@Field(type = String)
	private String author;

	@Field(type = String)
	private String title;

	@Field(type = String)
	private String otitle;

	@MultiField(mainField = @Field(type = String, index = analyzed), otherFields = {
			@NestedField(dotSuffix = "untouched", type = String, store = true, index = not_analyzed),
			@NestedField(dotSuffix = "sort", type = String, store = true, indexAnalyzer = "keyword") })
	private List<String> characters = new ArrayList<String>();

	@Field
	private List<String> tags = new ArrayList<String>();

	@Field(type = FieldType.Long)
	private long copies;

	@Field(type = FieldType.Boolean)
	private boolean available;

	@Field(type = FieldType.Long, index = analyzed)
	private long year;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOtitle() {
		return otitle;
	}

	public void setOtitle(String otitle) {
		this.otitle = otitle;
	}

	public List<String> getCharacters() {
		return characters;
	}

	public void setCharacters(List<String> characters) {
		this.characters = characters;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public long getCopies() {
		return copies;
	}

	public void setCopies(long copies) {
		this.copies = copies;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public long getYear() {
		return year;
	}

	public void setYear(long year) {
		this.year = year;
	}

	public static Builder getBuilder(String id, String author, String title,
			String otitle, long copies, boolean available, long year) {
		return new Builder(id, author, title, otitle, copies, available, year);
	}

	public static class Builder {

		private Book built;

		public Builder(String id, String author, String title, String otitle,
				long copies, boolean available, long year) {
			built = new Book();
			built.id = id;
			built.author = author;
			built.title = title;
			built.otitle = otitle;
			built.copies = copies;
			built.available = available;
			built.year = year;
		}

		public Book build() {
			return built;
		}
	}

	@Override
	public boolean equals(Object obj) {
		EqualsBuilder builder = new EqualsBuilder();
		return builder.append(this.id, ((Book) obj).id)
				.append(this.title, ((Book) obj).title).isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		return builder.append(this.id).append(this.title).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.append("id", id).append("title", title)
				.append("otitle", otitle).append("characters", characters)
				.append("tags", tags).append("copies", copies)
				.append("available", available).append("year", year).toString();

	}

}
