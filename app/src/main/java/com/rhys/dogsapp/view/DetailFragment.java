package com.rhys.dogsapp.view;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.rhys.dogsapp.R;
import com.rhys.dogsapp.databinding.FragmentDetailBinding;
import com.rhys.dogsapp.databinding.SendSmsDialogBinding;
import com.rhys.dogsapp.model.DogBreed;
import com.rhys.dogsapp.model.DogPalette;
import com.rhys.dogsapp.model.SmsInfo;
import com.rhys.dogsapp.viewmodel.DetailViewModel;

public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;
    private DetailViewModel viewModel;

    private Boolean sendSmsStarted = false;

    private DogBreed currentDog;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);

        if (getArguments() != null) {
            int dogUuid = DetailFragmentArgs.fromBundle(getArguments()).getDogUuid();
            viewModel.fetch(dogUuid);
        }

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.dog.observe(this, dog -> {
            if (dog != null && getContext() != null) {
                currentDog = dog;
                binding.setDog(dog);
                if (dog.imageUrl != null)
                    setupBackgroundColor(dog.imageUrl);
            }
        });
    }

    private void setupBackgroundColor(String url) {
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Palette.from(resource)
                                .generate(palette -> {
                                    int intColor = palette
                                            .getLightMutedSwatch()
                                            .getRgb();
                                    DogPalette myPalette = new DogPalette(intColor);
                                    binding.setPalette(myPalette);
                                });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_send_sms) {
            if (!sendSmsStarted) {
                sendSmsStarted = true;
                ((MainActivity) getActivity()).checkSmsPermission();
            }
        } else if (item.getItemId() == R.id.action_share) {
            Toast.makeText(getContext(), "Action share", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPermissionResult(Boolean permissionGranted) {
        //  Check fragment has been added to the activity
        if (isAdded() && sendSmsStarted && permissionGranted) {
            SmsInfo smsInfo = new SmsInfo("", currentDog.dogBreed + " bred for " + currentDog.bredFor, currentDog.imageUrl);

            SendSmsDialogBinding dialogBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(getContext()),
                    R.layout.send_sms_dialog,
                    null,
                    false
            );
            new AlertDialog.Builder(getContext())
                    .setView(dialogBinding.getRoot())
                    .setPositiveButton("Send SMS", (dialog, which) -> {
                        if(!dialogBinding.smsDestination.getText().toString().isEmpty()){
                            smsInfo.to = dialogBinding.smsDestination.getText().toString();
                            sendSms(smsInfo);
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {})
                    .show();
            sendSmsStarted = false;
            dialogBinding.setSmsInfo(smsInfo);
        }
    }

    private void sendSms(SmsInfo smsInfo){

    }
}