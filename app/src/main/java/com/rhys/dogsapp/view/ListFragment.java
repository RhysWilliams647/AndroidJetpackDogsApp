package com.rhys.dogsapp.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.rhys.dogsapp.R;
import com.rhys.dogsapp.viewmodel.ListViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListFragment extends Fragment {

    @BindView(R.id.dogsList)
    RecyclerView dogsList;

    @BindView(R.id.listError)
    TextView listError;

    @BindView(R.id.loadingView)
    ProgressBar loadingView;

    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    private ListViewModel viewModel;
    private DogsListAdapter dogsListAdapter = new DogsListAdapter(new ArrayList<>());

    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ListViewModel.class);
        viewModel.refresh();

        //  Linear layout for our recycle view, i.e. display in a list
        //  as opposed to a grid (matrix layout manager)
        dogsList.setLayoutManager(new LinearLayoutManager(getContext()));
        dogsList.setAdapter(dogsListAdapter);

        refreshLayout.setOnRefreshListener(() -> {
            dogsList.setVisibility(View.GONE);
            listError.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
            viewModel.refreshBypassCache();
            refreshLayout.setRefreshing(false);
        });

        observeViewModel();
    }

    private void observeViewModel(){
        viewModel.dogs.observe(this, dogs -> {
            if(dogs != null){
                dogsList.setVisibility(View.VISIBLE);
                dogsListAdapter.updateDogsList(dogs);
            }
        });

        viewModel.dogsLoadError.observe(this, loadError -> {
            if(loadError != null)
                listError.setVisibility(loadError ? View.VISIBLE : View.GONE);
        });

        viewModel.loading.observe(this, isLoading -> {
            if(isLoading != null){
                if(isLoading){
                    loadingView.setVisibility(View.VISIBLE);
                    listError.setVisibility(View.GONE);
                    dogsList.setVisibility(View.GONE);
                    return;
                }
                loadingView.setVisibility(View.GONE);
            }
        });
    }
}