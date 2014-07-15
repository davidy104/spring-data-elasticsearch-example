package nz.co.springdata.elasticsearch.config;

import static org.apache.commons.lang.StringUtils.split;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;
import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.util.Assert;

@Configuration
@PropertySource("classpath:elasticsearch.properties")
public class RemoteElasticSearchContextConfig {

	private String clusterNodes = "127.0.0.1:9300";
	private String clusterName = "elasticsearch";
	private Boolean clientTransportSniff = true;
	private Boolean clientIgnoreClusterName = Boolean.FALSE;
	private String clientPingTimeout = "5s";
	private String clientNodesSamplerInterval = "5s";

	private static final String CLUSTER_NODES = "cluster.nodes";
	private static final String CLUSTER_NAME = "cluster.name";
	private static final String CLIENT_TRANSPORT_SNIFF = "client.transport.sniff";
	private static final String CLIENT_TRANSPORT_IGNORE_CLUSTER_NAME = "client.transport.ignore_cluster_name";
	private static final String CLIENT_TRANSPORT_PING_TIMEOUT = "client.transport.ping_timeout";
	private static final String CLIENT_TRANSPORT_NOTES_SAMPLER_INTERVAL = "client.transport.nodes_sampler_interval";

	static final String COLON = ":";
	static final String COMMA = ",";

	@Resource
	private Environment environment;

	@Bean
	public ElasticsearchOperations elasticsearchTemplate() throws IOException {
		return new ElasticsearchTemplate(buildTransportClient());
	}

	@Bean
	public TransportClient buildTransportClient() {
		TransportClient client = new TransportClient(settings());
		for (String clusterNode : split(clusterNodes, COMMA)) {
			String hostName = substringBefore(clusterNode, COLON);
			String port = substringAfter(clusterNode, COLON);
			Assert.hasText(hostName,
					"[Assertion failed] missing host name in 'clusterNodes'");
			Assert.hasText(port,
					"[Assertion failed] missing port in 'clusterNodes'");
			client.addTransportAddress(new InetSocketTransportAddress(hostName,
					Integer.valueOf(port)));
		}
		client.connectedNodes();
		return client;
	}

	private Settings settings() {
		if (!StringUtils
				.isEmpty(environment.getRequiredProperty(CLUSTER_NODES))) {
			clusterNodes = environment.getRequiredProperty(CLUSTER_NODES);
		}

		if (!StringUtils.isEmpty(environment.getRequiredProperty(CLUSTER_NAME))) {
			clusterName = environment.getRequiredProperty(CLUSTER_NAME);
		}

		if (!StringUtils.isEmpty(environment
				.getRequiredProperty(CLIENT_TRANSPORT_SNIFF))) {
			clientTransportSniff = Boolean.valueOf(environment
					.getRequiredProperty(CLIENT_TRANSPORT_SNIFF));
		}

		if (!StringUtils.isEmpty(environment
				.getRequiredProperty(CLIENT_TRANSPORT_IGNORE_CLUSTER_NAME))) {
			clientIgnoreClusterName = Boolean.valueOf(environment
					.getRequiredProperty(CLIENT_TRANSPORT_IGNORE_CLUSTER_NAME));
		}

		if (!StringUtils.isEmpty(environment
				.getRequiredProperty(CLIENT_TRANSPORT_PING_TIMEOUT))) {
			clientPingTimeout = environment
					.getRequiredProperty(CLIENT_TRANSPORT_PING_TIMEOUT);
		}

		if (!StringUtils.isEmpty(environment
				.getRequiredProperty(CLIENT_TRANSPORT_NOTES_SAMPLER_INTERVAL))) {
			clientNodesSamplerInterval = environment
					.getRequiredProperty(CLIENT_TRANSPORT_NOTES_SAMPLER_INTERVAL);
		}

		return settingsBuilder()
				.put(CLUSTER_NAME, clusterName)
				.put(CLIENT_TRANSPORT_SNIFF, clientTransportSniff)
				.put(CLIENT_TRANSPORT_IGNORE_CLUSTER_NAME,
						clientIgnoreClusterName)
				.put(CLIENT_TRANSPORT_PING_TIMEOUT, clientPingTimeout)
				.put(CLIENT_TRANSPORT_NOTES_SAMPLER_INTERVAL,
						clientNodesSamplerInterval).build();
	}
}
