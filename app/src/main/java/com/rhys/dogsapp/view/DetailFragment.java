package com.rhys.dogsapp.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.rhys.dogsapp.R;
import com.rhys.dogsapp.viewmodel.DetailViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment {

    @BindView(R.id.dogImage)
    ImageView image;

    @BindView(R.id.dogName)
    TextView name;

    @BindView(R.id.dogPurpose)
    TextView purpose;

    @BindView(R.id.dogTemperament)
    TextView temperament;

    @BindView(R.id.dogLifespan)
    TextView lifespan;


    private DetailViewModel viewModel;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);

        if(getArguments() != null){
            int dogUuid = DetailFragmentArgs.fromBundle(getArguments()).getDogUuid();
            viewModel.fetch(dogUuid);
        }

        observeViewModel();
    }

    private void observeViewModel(){
        viewModel.dog.observe(this, dog -> {
           if(dog != null){
               name.setText(dog.dogBreed);
               purpose.setText(dog.bredFor);
               temperament.setText(dog.temperament);
               lifespan.setText(dog.lifeSpan);
           }
        });
    }
}