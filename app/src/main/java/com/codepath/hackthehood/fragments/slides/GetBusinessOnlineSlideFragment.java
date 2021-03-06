package com.codepath.hackthehood.fragments.slides;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.hackthehood.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 *
 * Created by ravi on 10/24/14.
 */
public class GetBusinessOnlineSlideFragment extends Fragment {

    @InjectView(R.id.tvStep1) TextView tvStep1;
    @InjectView(R.id.tvStep2) TextView tvStep2;
    @InjectView(R.id.tvStep3) TextView tvStep3;
    public GetBusinessOnlineSlideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_get_business_online_slide, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            tvStep1.setAlpha(0.0f);
            tvStep2.setAlpha(0.0f);
            tvStep3.setAlpha(0.0f);
            AnimatorSet set = new AnimatorSet();
            set.playSequentially(
                    ObjectAnimator.ofFloat(tvStep1, "alpha", 0.0f, 1.0f)
                            .setDuration(800),
                    ObjectAnimator.ofFloat(tvStep2, "alpha", 0.0f, 1.0f)
                            .setDuration(600),
                    ObjectAnimator.ofFloat(tvStep3, "alpha", 0.0f, 1.0f)
                            .setDuration(400)
            );
            set.start();
        }
    }
}
