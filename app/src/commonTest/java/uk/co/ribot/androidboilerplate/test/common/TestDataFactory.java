package uk.co.ribot.androidboilerplate.test.common;

import com.google.gson.Gson;

import java.util.UUID;

import uk.co.ribot.androidboilerplate.data.model.Result;

/**
 * Factory class that makes instances of data models with random field values.
 * The aim of this class is to help setting up test fixtures.
 */
public class TestDataFactory {

    public static String randomUuid() {
        return UUID.randomUUID().toString();
    }

    public static Result getDirectionsPathFoundResponse() {
        Result directionResponse = null;
        String result = "{   \"geocoded_waypoints\" : [      {         \"geocoder_status\" : \"OK\",         \"place_id\" : \"ChIJyWw6-WXiqIcRH4GHGir90vw\",         \"types\" : [ \"route\" ]      },      {         \"geocoder_status\" : \"OK\",         \"place_id\" : \"ChIJyWw6-WXiqIcRHoGHGir90vw\",         \"types\" : [ \"route\" ]      }   ],   \"routes\" : [      {         \"bounds\" : {            \"northeast\" : {               \"lat\" : 36.2354124,               \"lng\" : -99.6677881            },            \"southwest\" : {               \"lat\" : 36.2351822,               \"lng\" : -99.66778889999999            }         },         \"copyrights\" : \"Datos de mapas ©2017 Google\",         \"legs\" : [            {               \"distance\" : {                  \"text\" : \"85 pies\",                  \"value\" : 26               },               \"duration\" : {                  \"text\" : \"1 min\",                  \"value\" : 20               },               \"end_address\" : \"N1870 Rd, Gage, OK 73843, EE. UU.\",               \"end_location\" : {                  \"lat\" : 36.2351822,                  \"lng\" : -99.6677881               },               \"start_address\" : \"N1870 Rd, Gage, OK 73843, EE. UU.\",               \"start_location\" : {                  \"lat\" : 36.2354124,                  \"lng\" : -99.66778889999999               },               \"steps\" : [                  {                     \"distance\" : {                        \"text\" : \"85 pies\",                        \"value\" : 26                     },                     \"duration\" : {                        \"text\" : \"1 min\",                        \"value\" : 20                     },                     \"end_location\" : {                        \"lat\" : 36.2351822,                        \"lng\" : -99.6677881                     },                     \"html_instructions\" : \"Dirígete al \\u003cb\\u003esur\\u003c/b\\u003e por \\u003cb\\u003eN1870 Rd\\u003c/b\\u003e hacia \\u003cb\\u003eE0540 Rd\\u003c/b\\u003e\",                     \"polyline\" : {                        \"points\" : \"ifd|Etji_Rl@?\"                     },                     \"start_location\" : {                        \"lat\" : 36.2354124,                        \"lng\" : -99.66778889999999                     },                     \"travel_mode\" : \"WALKING\"                  }               ],               \"traffic_speed_entry\" : [],               \"via_waypoint\" : []            }         ],         \"overview_polyline\" : {            \"points\" : \"ifd|Etji_Rl@?\"         },         \"summary\" : \"N1870 Rd\",         \"warnings\" : [            \"Las rutas a pie están en versión beta. Ten cuidado. –  En esta ruta puede que no haya aceras o pasos para peatones.\"         ],         \"waypoint_order\" : []      }   ],   \"status\" : \"OK\"}";
        directionResponse = new Gson().fromJson(result, Result.class);
        return directionResponse;
    }

}