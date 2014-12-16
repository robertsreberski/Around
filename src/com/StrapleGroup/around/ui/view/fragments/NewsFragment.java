package com.StrapleGroup.around.ui.view.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.StrapleGroup.around.R;

/**
 * Created by Robert on 2014-09-21.
 */
public class NewsFragment extends Fragment {

    FragmentTabHost tabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View pFrag = inflater.inflate(R.layout.fragment_news, container, false);
        tabHost = (FragmentTabHost) pFrag.findViewById(R.id.tabHost);
        tabHost.setup(getActivity().getApplicationContext(), getChildFragmentManager(), android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec("News").setIndicator("News"), NewsTab.class, null);
        tabHost.addTab(tabHost.newTabSpec("Social").setIndicator("Social"), SocialTab.class, null);
        return pFrag;

    }
}