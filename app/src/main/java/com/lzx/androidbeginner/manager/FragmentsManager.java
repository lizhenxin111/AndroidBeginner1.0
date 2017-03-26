package com.lzx.androidbeginner.manager;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.lzx.androidbeginner.R;
import com.lzx.androidbeginner.UI.LoadingFragment;
import com.lzx.androidbeginner.UI.MainActivity;

/**
 * Created by lizhenxin on 17-3-24.
 */

public class FragmentsManager {
    public void addLoading(Activity fragmentActivity, int parentId){
        FragmentActivity a = (FragmentActivity)fragmentActivity;
        FragmentManager fragmentManager = a.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(parentId, new LoadingFragment())
                .addToBackStack("loading")
                .commit();
    }

    public void addLoading(Fragment fragment, int parentId){
        Log.d("Main", 1.1 + "");
        FragmentTransaction transaction = fragment.getChildFragmentManager().beginTransaction();
        Log.d("Main", 1.2 + "");
        transaction.add(parentId, new LoadingFragment())
                .addToBackStack("loading")
                .commit();
        Log.d("Main", 1.3 + "");
    }

    public void removeLoading(Activity fragmentActivity){
        FragmentActivity a = (FragmentActivity)fragmentActivity;
        FragmentManager fragmentManager = a.getSupportFragmentManager();
        fragmentManager.popBackStack();
    }
    public void removeLoading(Fragment fragment){
        FragmentManager fragmentManager = fragment.getChildFragmentManager();
        fragmentManager.popBackStack();
    }


    public void changeFragment(FragmentActivity fragmentActivity, Fragment fragment){
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        MainActivity.fragmentStack.push(fragment);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
