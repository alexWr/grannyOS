package com.grannyos.login;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.grannyos.MainActivity;
import com.grannyos.R;
import com.grannyos.ViewPagerFragment;


public class LoginFragment extends Fragment implements View.OnClickListener{


    private final static String     TAG = "LoginFragmentGrannyOS";
    private static final int        REQUEST_CODE_SIGN_IN = 1;//request code if result ok
    public static GoogleApiClient   googleApiClient;//google client
    private Resources               resources;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_layout, container, false);
        resources = getActivity().getResources();
        Button loginGoogle = (Button) rootView.findViewById(R.id.loginGoogle);
        loginGoogle.setOnClickListener(this);
        MainActivity.onStop = false;
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.loginGoogle){
            Fragment fragment = new ViewPagerFragment();
            ProgressDialog connectionProgressDialog = ProgressDialog.show(getActivity(), null, resources.getString(R.string.getDataFromGoogle), true);
            GooglePlusLogin googlePlus = new GooglePlusLogin(getActivity(), getActivity(), fragment, connectionProgressDialog);
            googleApiClient= googlePlus.getGoogleApiClient();
            googleApiClient.connect();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SIGN_IN ) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
                Log.d(TAG, "onActivityResult");
            }
            else{
                Toast.makeText(getActivity(), resources.getString(R.string.alert_message), Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
