package nz.co.springdata.elasticsearch.config;

//import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import javax.annotation.Resource;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@ComponentScan(basePackages = "nz.co.springdata.elasticsearch")
@EnableElasticsearchRepositories(basePackages = "nz/co/springdata/elasticsearch/data")
public class ApplicationContextConfig {

	@Resource
	private TransportClientBuilder transportClientBuilder;

	@Bean
	public static PropertyPlaceholderConfigurer properties() {
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		org.springframework.core.io.Resource[] resources = new ClassPathResource[] { new ClassPathResource(
				"elasticsearch.properties") };
		ppc.setLocations(resources);
		ppc.setIgnoreUnresolvablePlaceholders(true);
		return ppc;
	}

	@Bean
	public ElasticsearchOperations elasticsearchTemplate() {
		return new ElasticsearchTemplate(
				transportClientBuilder.buildTransportClient());
	}

	// @Bean
	// public ElasticsearchOperations elasticsearchTemplate() {
	// ImmutableSettings.Builder settings = ImmutableSettings
	// .settingsBuilder().put("http.enabled", String.valueOf(true));
	//
	// return new ElasticsearchTemplate(nodeBuilder().local(true)
	// .settings(settings).node().client());
	// }

}
