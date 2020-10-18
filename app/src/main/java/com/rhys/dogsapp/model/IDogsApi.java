package com.rhys.dogsapp.model;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface IDogsApi {
    @GET("DevTides/DogsApi/master/dogs.json")
    Single<List<DogBreed>> getDogs();
}
