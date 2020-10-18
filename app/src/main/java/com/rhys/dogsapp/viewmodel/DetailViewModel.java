package com.rhys.dogsapp.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.rhys.dogsapp.model.DogBreed;
import com.rhys.dogsapp.model.DogDatabase;
import com.rhys.dogsapp.model.IDogDao;

public class DetailViewModel extends AndroidViewModel {

    public MutableLiveData<DogBreed> dog = new MutableLiveData<>();

    private AsyncTask<Integer, Void, DogBreed> retrieveTask;

    public DetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void fetch(int uuid){
        retrieveTask = new RetrieveDogTask();
        retrieveTask.execute(uuid);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(retrieveTask != null){
            retrieveTask.cancel(true);
            retrieveTask = null;
        }
    }

    private class RetrieveDogTask extends AsyncTask<Integer, Void, DogBreed>{

        @Override
        protected DogBreed doInBackground(Integer... ints) {
            int uuid = ints[0];

            IDogDao dao = DogDatabase.getInstance(getApplication())
                    .dogDao();

            DogBreed dogBreed = dao.get(uuid);
            return dogBreed;
        }

        @Override
        protected void onPostExecute(DogBreed dogBreed) {
            dog.setValue(dogBreed);
        }
    }
}
