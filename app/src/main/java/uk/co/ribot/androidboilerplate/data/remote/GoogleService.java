package uk.co.ribot.androidboilerplate.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import uk.co.ribot.androidboilerplate.data.model.Result;
import uk.co.ribot.androidboilerplate.util.MyGsonTypeAdapterFactory;

/**
 * Created by Raul on 30/12/2016.
 */
public interface GoogleService {
    String ENDPOINT = "https://maps.googleapis.com/";

    @GET("/maps/api/directions/json")
    Observable<Result> getLocationRoute(@Query("mode") String mode,
                                        @Query("origin") String origin,
                                        @Query("destination") String destination,
                                        @Query("sensor") boolean sensor);

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static GoogleService newGoogleService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GoogleService.ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(GoogleService.class);
        }
    }

}