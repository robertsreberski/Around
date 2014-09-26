package com.StrapleGroup.around.ui.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.StrapleGroup.around.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by Robert on 2014-09-19.
 */
public class UserFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View pView = inflater.inflate(R.layout.fragment_user, container, false);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(new ImageLoaderConfiguration.Builder(getActivity().getApplicationContext()).build());
        DisplayImageOptions pOptions = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(330)).build();
        ImageView pProfileView = (ImageView) pView.findViewById(R.id.profilePhoto);
        imageLoader.displayImage("drawable://" + R.drawable.facebook_example, pProfileView, pOptions);
        return pView;
    }
}