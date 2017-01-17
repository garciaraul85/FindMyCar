package uk.co.ribot.androidboilerplate.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import uk.co.ribot.androidboilerplate.BuildConfig;
import uk.co.ribot.androidboilerplate.data.model.Result;
import uk.co.ribot.androidboilerplate.util.MyGsonTypeAdapterFactory;

public interface RibotsService {

    @GET("/maps/api/directions/json")
    Observable<Result> getRibots(@Query("mode") String mode,
                                 @Query("origin") String origin,
                                 @Query("destination") String destination,
                                 @Query("sensor") boolean sensor);

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static RibotsService newRibotsService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.MAPS_API)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(RibotsService.class);
        }
    }
}
