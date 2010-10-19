/**
 * 
 */
package com.googlecode.flickr2twitter.impl.picasa;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.api.client.googleapis.auth.authsub.AuthSubSingleUseTokenRequestUrl;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.Person;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.photos.UserFeed;
import com.googlecode.flickr2twitter.datastore.MyPersistenceManagerFactory;
import com.googlecode.flickr2twitter.datastore.model.GlobalApplicationConfig;
import com.googlecode.flickr2twitter.datastore.model.GlobalServiceConfiguration;
import com.googlecode.flickr2twitter.datastore.model.GlobalSourceApplicationService;
import com.googlecode.flickr2twitter.datastore.model.User;
import com.googlecode.flickr2twitter.datastore.model.UserSourceServiceConfig;
import com.googlecode.flickr2twitter.exceptions.TokenAlreadyRegisteredException;
import com.googlecode.flickr2twitter.intf.ISourceServiceProvider;
import com.googlecode.flickr2twitter.model.IPhoto;
import com.googlecode.flickr2twitter.org.apache.commons.lang3.StringUtils;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 *
 */
public class SourceServiceProviderPicasa implements ISourceServiceProvider<IPhoto> {
	public static final String ID = "picasa";
	public static final String DISPLAY_NAME = "Picasa Web Album";
	public static final String TIMEZONE_CST = "CST";
	public static final String KEY_TOKEN = "token";
	private static final Logger log = Logger.getLogger(SourceServiceProviderPicasa.class.getName());
	
	public static final String HOSTED_DOMAIN = "flickr2twitter.googlecode.com";
	public static final String CONSUMER_KEY = "anonymous";
	public static final String CONSUMER_SECRET = "anonymous";
	
	private static final String SCOPE = "http://picasaweb.google.com/data";
	
	/**
	 * 
	 */
	public SourceServiceProviderPicasa() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.googlecode.flickr2twitter.intf.ISourceServiceProvider#getLatestItems(com.googlecode.flickr2twitter.datastore.model.GlobalServiceConfiguration, com.googlecode.flickr2twitter.datastore.model.UserSourceServiceConfig)
	 */
	@Override
	public List<IPhoto> getLatestItems(GlobalServiceConfiguration globalConfig,
			UserSourceServiceConfig sourceService) throws Exception {
		PicasawebService webService = new PicasawebService(HOSTED_DOMAIN);
		String sessionToken = sourceService.getServiceAccessToken();
		webService.setAuthSubToken(sessionToken, null);
		URL feedUrl = new URL("http://picasaweb.google.com/data/feed/api/user/default?kind=photo&access=visibility");

		AlbumFeed feed = webService.getFeed(feedUrl, AlbumFeed.class);

		for(PhotoEntry photo : feed.getPhotoEntries()) {
			System.out.println(photo.getTitle().getPlainText() + ", uploaded date: " + photo.getUpdated());
		}
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see com.googlecode.flickr2twitter.intf.IServiceAuthorizer#readyAuthorization(java.lang.String, java.util.Map)
	 */
	@Override
	public String readyAuthorization(String userEmail, Map<String, Object> data)
			throws Exception {
		if (data == null || data.containsKey(KEY_TOKEN) == false) {
			throw new IllegalArgumentException("Invalid data: " + data);
		}
		User user = MyPersistenceManagerFactory.getUser(userEmail);
		if (user == null) {
			throw new IllegalArgumentException(
					"Can not find the specified user: " + userEmail);
		}
		String token = String.valueOf(data.get("token"));
		
		PicasawebService webService = new PicasawebService(HOSTED_DOMAIN);
		webService.setAuthSubToken(token, null);
		URL feedUrl = new URL("http://picasaweb.google.com/data/feed/api/user/default?kind=album");
		UserFeed myUserFeed = webService.getFeed(feedUrl, UserFeed.class);
		List<Person> persons = myUserFeed.getAuthors();
		Person person = null;
		if (persons.isEmpty() == false) {
			person = persons.get(0);
		}
		
		StringBuffer buf = new StringBuffer();
		buf.append("Authentication success\n");
		// This token can be used until the user revokes it.
		buf.append("Token: " + token);
		buf.append("\n");
		buf.append("Realname: " + person.getName());
		buf.append("\n");
		buf.append("User Site: " + person.getUri());
		
		for (UserSourceServiceConfig service : MyPersistenceManagerFactory
				.getUserSourceServices(user)) {
			if (token.equals(service.getServiceAccessToken())) {
				throw new TokenAlreadyRegisteredException(token, userEmail);
			}
		}
		
		UserSourceServiceConfig serviceConfig = new UserSourceServiceConfig();
		serviceConfig.setServiceUserId("1");
		serviceConfig.setServiceUserName(person != null ? person.getName() : "default");
		serviceConfig.setServiceAccessToken(token);
		serviceConfig.setServiceProviderId(ID);
		serviceConfig.setUserEmail(userEmail);
		
		if (person != null) {
			serviceConfig.setUserSiteUrl(person.getUri());
		}
		
		MyPersistenceManagerFactory.addSourceServiceApp(userEmail, serviceConfig);

		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see com.googlecode.flickr2twitter.intf.IServiceAuthorizer#requestAuthorization()
	 */
	@Override
	public Map<String, Object> requestAuthorization(String baseUrl) throws Exception {
		GlobalSourceApplicationService globalAppConfig = MyPersistenceManagerFactory
		.getGlobalSourceAppService(ID);
		if (globalAppConfig == null
				|| ID.equalsIgnoreCase(globalAppConfig.getProviderId()) == false) {
			throw new IllegalArgumentException(
					"Invalid source service provider: " + globalAppConfig);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		if (baseUrl.endsWith("/oauth")) {
			baseUrl = StringUtils.left(baseUrl, baseUrl.length() - "/oauth".length());
		}
		String nextUrl = baseUrl + "/picasacallback.jsp";
		String scope = SCOPE;
		AuthSubSingleUseTokenRequestUrl authorizeUrl = new AuthSubSingleUseTokenRequestUrl();
		authorizeUrl.hostedDomain = HOSTED_DOMAIN;
		authorizeUrl.nextUrl = nextUrl;
		authorizeUrl.scope = scope;
		authorizeUrl.session = 1;
		String authorizationUrl = authorizeUrl.build();
		
		System.out.println(authorizationUrl);
		result.put("url", authorizationUrl);
		return result;
	}

	/* (non-Javadoc)
	 * @see com.googlecode.flickr2twitter.intf.IServiceProvider#createDefaultGlobalApplicationConfig()
	 */
	@Override
	public GlobalApplicationConfig createDefaultGlobalApplicationConfig() {
		GlobalSourceApplicationService result = new GlobalSourceApplicationService();
		result.setAppName(DISPLAY_NAME);
		result.setProviderId(ID);
		result.setDescription("The Google's online photo storage service");
		result.setSourceAppApiKey(CONSUMER_KEY);
		result.setSourceAppSecret(CONSUMER_SECRET);
		result.setAuthPagePath(null); // TODO set the default auth page path
		result.setImagePath(null); // TODO set the default image path
		return result;
	}

	/* (non-Javadoc)
	 * @see com.googlecode.flickr2twitter.intf.IServiceProvider#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}

}