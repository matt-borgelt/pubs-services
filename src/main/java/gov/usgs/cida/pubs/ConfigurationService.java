package gov.usgs.cida.pubs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {

	@Value("${crossref.protocol}")
	private String crossrefProtocol;
	@Value("${crossref.host}")
	private String crossrefHost;
	@Value("${crossref.url}")
	private String crossrefUrl;
	@Value("${crossref.port:-1}")
	private Integer crossrefPort;
	@Value("${crossref.username}")
	private String crossRefUser;
	@Value("${crossref.password}")
	private String crossrefPwd;
	@Value("${crossref.schema.url}")
	private String crossrefSchemaUrl;
	@Value("${crossref.depositorEmail}")
	private String crossrefDepositorEmail;

	@Value("${pubs.authorities.admin}")
	private String[] adminAuthorities;
	@Value("${pubs.authorities.spn}")
	private String[] spnAuthorities;
	@Value("${pubs.authorities.authorized}")
	private String[] authorizedAuthorities;
	@Value("${pubs.emailList}")
	private String pubsEmailList;
	@Value("${pubs.mailHost}")
	private String mailHost;
	@Value("${pubs.lockTimeoutHours:3}")
	private Integer lockTimeoutHours;
	@Value("${pubs.warehouseEndpoint}")
	private String warehouseEndpoint;

	@Value("${springdoc.version}")
	private String appVersion;
	@Value("${swagger.serverUrl}")
	private String swaggerUrl;

	@Value("${security.oauth2.resource.id}")
	private String resourceId;
	@Value("${security.oauth2.resource.jwk.keySetUri}")
	private String keySetUri;

	@Value("${sipp.dissemination.list-url}")
	private String disseminationListUrl;
	@Value("${sipp.infoProduct.url}")
	private String infoProductUrl;

	@Value("${spn.image.url}")
	private String spnImageUrl;

	public String getCrossrefProtocol() {
		return crossrefProtocol;
	}
	public String getCrossrefHost() {
		return crossrefHost;
	}
	public String getCrossrefUrl() {
		return crossrefUrl;
	}
	public Integer getCrossrefPort() {
		return crossrefPort;
	}
	public String getCrossrefUser() {
		return crossRefUser;
	}
	public String getCrossrefPwd() {
		return crossrefPwd;
	}
	public String getCrossrefSchemaUrl() {
		return crossrefSchemaUrl;
	}
	public String getCrossrefDepositorEmail() {
		return crossrefDepositorEmail;
	}
	public String getPubsEmailList() {
		return pubsEmailList;
	}
	public String getMailHost() {
		return mailHost;
	}
	public Integer getLockTimeoutHours() {
		return lockTimeoutHours;
	}
	public String getWarehouseEndpoint() {
		return warehouseEndpoint;
	}
	public String getResourceId() {
		return resourceId;
	}
	public String getKeySetUri() {
		return keySetUri;
	}
	public String[] getAdminAuthorities() {
		return adminAuthorities;
	}
	public String[] getSpnAuthorities() {
		return spnAuthorities;
	}
	public String[] getAuthorizedAuthorities() {
		return authorizedAuthorities;
	}
	public String getDisseminationListUrl() {
		return disseminationListUrl;
	}
	public String getInfoProductUrl() {
		return infoProductUrl;
	}
	public String getSpnImageUrl() {
		return spnImageUrl;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public String getSwaggerUrl() {
		return swaggerUrl;
	}
}
