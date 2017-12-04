package com.softdesign.devintensive.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.softdesign.devintensive.data.network.res.UserListRes;

import java.util.ArrayList;

public class RetainFragment extends Fragment
{
    private ArrayList<UserListRes.UserData> userData;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public ArrayList<UserListRes.UserData> getUsersList()
    {
        return userData;
    }

    public void setUsersList(ArrayList<UserListRes.UserData> usersList)
    {
        userData = usersList;
    }
}