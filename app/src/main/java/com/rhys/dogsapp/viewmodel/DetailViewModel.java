package com.rhys.dogsapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rhys.dogsapp.model.DogBreed;

public class DetailViewModel extends ViewModel {

    public MutableLiveData<DogBreed> dog = new MutableLiveData<>();

    public void fetch(int breedId){
        DogBreed fetchedDog = new DogBreed(
                String.valueOf(breedId),
                "Chihuahua",
                "15 years",
                "",
                "",
                "",
                "");
        dog.setValue(fetchedDog);
    }
}
