package nz.co.springdata.elasticsearch.config;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

@Configuration
public class LocalElasticSearchContextConfig {

	@Bean
	public ElasticsearchOperations elasticsearchTemplate() {
		ImmutableSettings.Builder settings = ImmutableSettings
				.settingsBuilder().put("http.enabled", String.valueOf(true));

		return new ElasticsearchTemplate(nodeBuilder().local(true)
				.settings(settings).node().client());
	}

}
