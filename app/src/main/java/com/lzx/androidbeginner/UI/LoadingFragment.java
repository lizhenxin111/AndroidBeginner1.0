package com.lzx.androidbeginner.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.lzx.androidbeginner.R;

/**
 * Created by lizhenxin on 17-3-21.
 * 加载动画
 */

public class LoadingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.loading);
        TextView textView = (TextView)view.findViewById(R.id.loading_text);
        textView.setAnimation(animation);

        return view;
    }
}
