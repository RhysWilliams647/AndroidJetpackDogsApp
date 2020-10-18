package com.rhys.dogsapp.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.rhys.dogsapp.model.DogBreed;
import com.rhys.dogsapp.model.DogDatabase;
import com.rhys.dogsapp.model.DogsApiService;
import com.rhys.dogsapp.model.IDogDao;
import com.rhys.dogsapp.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ListViewModel extends AndroidViewModel {

    public MutableLiveData<List<DogBreed>> dogs = new MutableLiveData<>();
    public MutableLiveData<Boolean> dogsLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    private DogsApiService dogsService = new DogsApiService();
    private CompositeDisposable disposable = new CompositeDisposable();

    private AsyncTask<List<DogBreed>, Void, List<DogBreed>> insertTask;
    private AsyncTask<Void, Void, List<DogBreed>> retrieveTask;

    //  Prefs stores last time we refreshed the data
    private SharedPreferencesHelper prefHelper = SharedPreferencesHelper.getInstance(getApplication());
    //  minutes/seconds/milliseconds/microseconds/nanoseconds
    //  Required as system clock works in nanoseconds
    //  How often we wish to refresh data
    private long refreshTime = 5 * 60 * 1000 * 1000 * 1000L;

    public ListViewModel(@NonNull Application application) {
        super(application);
    }

    public void refresh() {
        long updateTime = prefHelper.getUpdateTime();
        long currentTime = System.nanoTime();

        if(updateTime != 0 && currentTime - updateTime < refreshTime)
            fetchFromDatabase();
        else
            fetchFromRemote();
    }

    public void refreshBypassCache(){
        fetchFromRemote();
    }

    private void fetchFromDatabase() {
        loading.setValue(true);
        retrieveTask = new RetrieveDogsTask();
        retrieveTask.execute();
    }

    private void fetchFromRemote() {
        loading.setValue(true);
        disposable.add(
                dogsService.getDogs()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<DogBreed>>() {
                            @Override
                            public void onSuccess(List<DogBreed> dogBreeds) {
                                insertTask = new InsertDogsTask();
                                insertTask.execute(dogBreeds);
                            }

                            @Override
                            public void onError(Throwable e) {
                                dogsLoadError.setValue(true);
                                loading.setValue(false);
                                e.printStackTrace();
                            }
                        }));
    }

    private void dogsRetrieved(List<DogBreed> dogList) {
        dogs.setValue(dogList);
        dogsLoadError.setValue(false);
        loading.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();

        if (insertTask != null) {
            insertTask.cancel(true);
            insertTask = null;
        }

        if (retrieveTask != null) {
            retrieveTask.cancel(true);
            retrieveTask = null;
        }
    }

    private class InsertDogsTask extends AsyncTask<List<DogBreed>, Void, List<DogBreed>> {

        @Override
        protected List<DogBreed> doInBackground(List<DogBreed>... lists) {
            List<DogBreed> dogList = lists[0];
            IDogDao dao = DogDatabase.getInstance(getApplication())
                    .dogDao();
            dao.deleteAll();
            ArrayList<DogBreed> newList = new ArrayList<>(dogList);
            List<Long> result = dao.insertAll(newList.toArray(new DogBreed[0]));

            int i = 0;
            while (i < result.size()) {
                dogList.get(i).uuid = result.get(i).intValue();
                ++i;
            }

            return dogList;
        }

        @Override
        protected void onPostExecute(List<DogBreed> dogBreeds) {
            dogsRetrieved(dogBreeds);
            prefHelper.saveUpdateTime(System.nanoTime());
            Toast.makeText(getApplication(), "Dogs retrieved remotely", Toast.LENGTH_SHORT).show();
        }
    }

    private class RetrieveDogsTask extends AsyncTask<Void, Void, List<DogBreed>> {

        @Override
        protected List<DogBreed> doInBackground(Void... voids) {
            return DogDatabase
                    .getInstance(getApplication())
                    .dogDao()
                    .getAll();
        }

        @Override
        protected void onPostExecute(List<DogBreed> dogBreeds) {
            dogsRetrieved(dogBreeds);
            Toast.makeText(getApplication(), "Dogs retrieved locally", Toast.LENGTH_SHORT).show();
        }
    }
}
