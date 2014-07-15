package nz.co.springdata.elasticsearch.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@ComponentScan(basePackages = "nz.co.springdata.elasticsearch")
@EnableElasticsearchRepositories(basePackages = "nz/co/springdata/elasticsearch/data")
public class ApplicationContextConfig {

}
