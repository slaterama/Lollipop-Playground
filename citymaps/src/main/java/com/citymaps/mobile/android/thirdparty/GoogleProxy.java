package com.citymaps.mobile.android.thirdparty;

import android.app.Activity;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.model.people.Person;

public class GoogleProxy extends ThirdPartyProxy {

	private GoogleConnection mConnection;

	public GoogleProxy(Activity activity) {
		super(activity);
	}

	@Override
	public Connection newConnection() {
		mConnection = new GoogleConnection();
		return mConnection;
	}

	@Override
	public void disconnect() {

	}

	public class GoogleConnection extends Connection<Scope> {

		@Override
		public void connect() {

		}
	}

	public static class PersonRequest extends Request<Person, Exception> {
		public PersonRequest(Listener<Person> listener, ErrorListener<Exception> errorListener) {
			super(listener, errorListener);
		}
	}
}
