package com.citymaps.mobile.android.config;

import static com.citymaps.mobile.android.config.Endpoint.*;

public class ApiBase extends Api {

	private static String getConfigEndpoint(Environment environment) {
		switch (environment.getType()) {
			case DEVELOPMENT:
				return "riak/appconfig/android_config_dev.json";
			case PRODUCTION:
			default:
				return "riak/appconfig/android_config.json";
		}
	}

	protected ApiBase(Environment environment, Version version) {
		super(environment, version);
	}

	@SuppressWarnings("SpellCheckingInspection")
	@Override
	void addEndpoints(Environment environment, Version version) {
		addEndpoint(new Endpoint(Type.TERMS_OF_SERVICE, Server.Type.CITYMAPS, "terms", APPEND_NONE));
		addEndpoint(new Endpoint(Type.PRIVACY_POLICY, Server.Type.CITYMAPS, "privacy", APPEND_NONE));
		addEndpoint(new Endpoint(Type.CONFIG, Server.Type.ASSETS, getConfigEndpoint(environment), APPEND_NONE));
		addEndpoint(new Endpoint(Type.VERSION, "v2/status/version", APPEND_NONE));
		addEndpoint(new Endpoint(Type.CATEGORY_ICON, Server.Type.ASSETS, "riak/category_icons4_app/%s", APPEND_NONE));
		addEndpoint(new Endpoint(Type.COLLECTIONS, "maps/%s"));
		addEndpoint(new Endpoint(Type.COLLECTIONS_FOR_USER, "v2/maps/user/%s"));
		addEndpoint(new Endpoint(Type.EXPLORE_FEATURED_COLLECTIONS, Server.Type.SEARCH, "explore/featured/maps?lon=%f&lat=%f&zoom=%d&radius=%f&search_id=%s&offset=%d&limit=%d"));
		addEndpoint(new Endpoint(Type.EXPLORE_FEATURED_DEALS, Server.Type.SEARCH, "explore/featured/deals?lon=%f&lat=%f&zoom=%d&radius=%f&search_id=%s&offset=%d&limit=%d"));
		addEndpoint(new Endpoint(Type.EXPLORE_FEATURED_HERO_ITEMS, Server.Type.SEARCH, "explore/featured?lon=%f&lat=%f&zoom=%d&radius=%f&search_id=%s&offset=%d&limit=%d"));
		addEndpoint(new Endpoint(Type.EXPLORE_FEATURED_MAPPERS, "v2/user/suggest/explorer/friends?lon=%f&lat=%f&radius=%f&offset=%d&limit=%d"));
		addEndpoint(new Endpoint(Type.FOURSQUARE_GET_PHOTOS, Server.Type.FOURSQUARE, "v2/venues/%s/photos?v=%s&client_id=%s&client_secret=%s", APPEND_NONE));
		addEndpoint(new Endpoint(Type.PLACE, "business/%s"));
		addEndpoint(new Endpoint(Type.PLACE_ICON, Server.Type.ASSETS, "riak/business_logos/%s", APPEND_NONE));
		addEndpoint(new Endpoint(Type.USER, "v2/user/%s"));
		addEndpoint(new Endpoint(Type.USER_LOGIN, "v2/user/login", APPEND_STANDARD));
		addEndpoint(new Endpoint(Type.USER_LOGIN_WITH_TOKEN, "v2/user/login?citymaps_token=%s", APPEND_STANDARD));
		addEndpoint(new Endpoint(Type.USER_LOGOUT, "v2/user/logout", APPEND_STANDARD));
		addEndpoint(new Endpoint(Type.USER_REGISTER, "v2/user/register?login=1", APPEND_STANDARD));
		addEndpoint(new Endpoint(Type.USER_RESET_PASSWORD, "user/resetpassword", APPEND_STANDARD));
		addEndpoint(new Endpoint(Type.USER_SETTINGS, "v2/user/%s/setting"));
		addEndpoint(new Endpoint(Type.USER_UPDATE, "v2/user/update/%s"));
	}
}
