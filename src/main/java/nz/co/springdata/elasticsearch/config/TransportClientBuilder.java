package nz.co.springdata.elasticsearch.config;

import static org.apache.commons.lang.StringUtils.split;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;
import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class TransportClientBuilder {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TransportClientBuilder.class);

	@Value("${cluster.nodes:127.0.0.1:9300}")
	private String clusterNodes;

	@Value("${cluster.name:elasticsearch}")
	private String clusterName;

	@Value("${client.transport.sniff:true}")
	private Boolean clientTransportSniff;

	@Value("${client.transport.ignore_cluster_name:true}")
	private Boolean clientIgnoreClusterName;

	@Value("${client.transport.ping_timeout:5s}")
	private String clientPingTimeout;

	@Value("${client.transport.nodes_sampler_interval:5s}")
	private String clientNodesSamplerInterval;

	private static final String CLUSTER_NAME = "cluster.name";
	private static final String CLIENT_TRANSPORT_SNIFF = "client.transport.sniff";
	private static final String CLIENT_TRANSPORT_IGNORE_CLUSTER_NAME = "client.transport.ignore_cluster_name";
	private static final String CLIENT_TRANSPORT_PING_TIMEOUT = "client.transport.ping_timeout";
	private static final String CLIENT_TRANSPORT_NOTES_SAMPLER_INTERVAL = "client.transport.nodes_sampler_interval";

	static final String COLON = ":";
	static final String COMMA = ",";

	public TransportClient buildTransportClient() {
		LOGGER.info("buildTransportClient start:{} ", clusterNodes);
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
