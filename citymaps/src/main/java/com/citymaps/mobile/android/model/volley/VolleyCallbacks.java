package com.citymaps.mobile.android.model.volley;

import com.android.volley.Response;

public interface VolleyCallbacks<T> extends Response.Listener<T>, Response.ErrorListener {
}