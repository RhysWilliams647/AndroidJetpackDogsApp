package com.rhys.dogsapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.rhys.dogsapp.model.DogBreed;

import java.util.ArrayList;
import java.util.List;

public class ListViewModel extends AndroidViewModel {

    public MutableLiveData<List<DogBreed>> dogs = new MutableLiveData<>();
    public MutableLiveData<Boolean> dogsLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public ListViewModel(@NonNull Application application) {
        super(application);
    }

    public void refresh() {
        DogBreed dog1 = Create("Corgi", "1");
        DogBreed dog2 = Create("Chihuahua", "2");
        DogBreed dog3 = Create("Alsatian", "3");

        DogBreed dog4 = Create("Scotty", "4");
        DogBreed dog5 = Create("Dalmatian", "5");
        DogBreed dog6 = Create("Jack Russel", "6");
        ArrayList<DogBreed> dogsList = new ArrayList<>();
        dogsList.add(dog1);
        dogsList.add(dog2);
        dogsList.add(dog3);
        dogsList.add(dog4);
        dogsList.add(dog5);
        dogsList.add(dog6);
        dogs.setValue(dogsList);
        dogsLoadError.setValue(false);
        loading.setValue(false);
    }

    private DogBreed Create(String name, String breedId){
        return new DogBreed(
                breedId,
                name,
                "15 years",
                "",
                "",
                "",
                "");
    }
}
