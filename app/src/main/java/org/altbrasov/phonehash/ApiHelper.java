package org.altbrasov.phonehash;

import retrofit.RestAdapter;

/**
 * Created by adi on 11/23/14
 */
public class ApiHelper {

    private static PublicApi publicApi;

    public static PublicApi getPublicApi() {
        if (publicApi == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder();
            builder.setEndpoint("http://rslbrasov.rosoftlab.net:6036");

            RestAdapter adapter = builder.build();
            publicApi = adapter.create(PublicApi.class);
        }


        return publicApi;
    }
}
