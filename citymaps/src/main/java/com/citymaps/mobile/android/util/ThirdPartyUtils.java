package com.citymaps.mobile.android.util;

import com.facebook.model.GraphUser;
import com.google.android.gms.plus.model.people.Person;

public class ThirdPartyUtils {

	private static final String PROPERTY_NAME_EMAIL = "email";

	/* Facebook */

	public static String getEmail(GraphUser user) {
		Object property = user.getProperty(PROPERTY_NAME_EMAIL);
		return (property == null ? null : property.toString());
	}

	public static String getBaseAvatarUrl(GraphUser user) {
		return (user == null || user.getId() == null ? null : String.format("http://graph.facebook.com/%s/picture", user.getId()));
	}

	/* Google */

	public static String getFirstName(Person person) {
		return (person == null || person.getName() == null ? null : person.getName().getGivenName());
	}

	public static String getLastName(Person person) {
		return (person == null || person.getName() == null ? null : person.getName().getFamilyName());
	}

	public static String getUsernameFromEmail(String email) {
		String username = null;
		if (email != null) {
			String[] tokens = email.split("@");
			if (tokens.length > 0) {
				username = tokens[0];
			}
		}
		return username;
	}

	public static String getBaseAvatarUrl(Person person) {
		String avatarUrl = null;
		if (person != null) {
			Person.Image image = person.getImage();
			if (image != null) {
				String url = image.getUrl();
				if (url != null) {
					avatarUrl = UriUtils.removeParameter(url, "sz");
				}
			}
		}
		return avatarUrl;
	}

	private ThirdPartyUtils() {
	}
}
