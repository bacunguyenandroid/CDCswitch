package com.bacunguyenandroid.cdcswitchclient;import android.content.Context;import android.os.Bundle;import android.support.annotation.IntRange;import android.support.annotation.NonNull;import android.support.v4.app.FragmentManager;import com.stepstone.stepper.Step;import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;import com.stepstone.stepper.viewmodel.StepViewModel;public class Viewstepadapter extends AbstractFragmentStepAdapter {    public Viewstepadapter(FragmentManager fm, Context context) {        super(fm, context);    }    @Override    public Step createStep(int position) {        switch (position){            case  0:                return new  mienone();            case 1:                return  new mientwo();            case  2:                return  new mienthree();        }        return new mienone();    }    @Override    public int getCount() {        return 3;    }    @NonNull    @Override    public StepViewModel getViewModel(@IntRange(from = 0) int position) {        //Override this method to set Step title for the Tabs, not necessary for other stepper types        return new StepViewModel.Builder(context)                .setTitle("cử") //can be a CharSequence instead                .setBackButtonVisible(false)                .create();    }}