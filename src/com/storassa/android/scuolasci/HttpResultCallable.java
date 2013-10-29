package com.storassa.android.scuolasci;

interface HttpResultCallable {
	void resultAvailable(Request request, String[] result, Feature[] features);
}
