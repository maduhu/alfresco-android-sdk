/*******************************************************************************
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * 
 * This file is part of the Alfresco Mobile SDK.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package org.alfresco.mobile.android.samples.ui.accounts;

import java.net.MalformedURLException;
import java.net.URL;

import org.alfresco.mobile.android.api.asynchronous.SessionLoader;
import org.alfresco.mobile.android.samples.activity.SessionLoaderCallback;
import org.alfresco.mobile.android.samples.utils.SessionUtils;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.fragments.BaseFragment;
import org.alfresco.mobile.android.ui.manager.MessengerManager;

import android.app.LoaderManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AccountDetailsFragment extends BaseFragment
{

    public static final String TAG = "AccountDetailsFragment";

    public static final String ARGUMENT_ACCOUNT_ID = "accountID";

    private String url = null, host = null, username = null, password = null;

    private View vRoot;

    public AccountDetailsFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (container == null) { return null; }

        vRoot = inflater.inflate(R.layout.sdkapp_account_details, container, false);

        SharedPreferences settings = getActivity().getSharedPreferences(TAG, 0);
        host = settings.getString("url", "");
        username = settings.getString("username", "");
        password = settings.getString("password", "");

        initValues(vRoot, true);

        return vRoot;
    }

    private void initValues(final View v, boolean isEditable)
    {

        Button advanced = (Button) v.findViewById(R.id.browse_document);
        advanced.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                retrieveFormValues(view);
                SharedPreferences settings = getActivity().getSharedPreferences(TAG, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("url", host);
                editor.putString("username", username);
                editor.putString("password", password);

                // Commit the edits!
                editor.commit();

                SessionUtils.setsession(getActivity(), null);
                SessionLoaderCallback call = new SessionLoaderCallback(getActivity(), host, username, password);
                LoaderManager lm = getActivity().getLoaderManager();
                lm.restartLoader(SessionLoader.ID, null, call);
                lm.getLoader(SessionLoader.ID).forceLoad();
            }
        });

        // Init values
        EditText form_value = (EditText) v.findViewById(R.id.repository_hostname);
        form_value.setText(host);
        form_value.setEnabled(isEditable);

        form_value = (EditText) v.findViewById(R.id.repository_username);
        form_value.setText(username);
        form_value.setEnabled(isEditable);

        form_value = (EditText) v.findViewById(R.id.repository_password);
        form_value.setText(password);
        form_value.setEnabled(isEditable);
    }

    private void retrieveFormValues(View v)
    {
        // Check values
        EditText form_value = (EditText) vRoot.findViewById(R.id.repository_hostname);
        if (form_value != null && form_value.getText() != null && form_value.getText().length() > 0)
        {
            host = form_value.getText().toString();
        }
        else
        {
            MessengerManager.showToast(getActivity(), R.string.error_login);
            return;
        }

        form_value = (EditText) vRoot.findViewById(R.id.repository_username);
        username = form_value.getText().toString();

        form_value = (EditText) vRoot.findViewById(R.id.repository_password);
        password = form_value.getText().toString();

        URL u = null;
        try
        {
            u = new URL(host);
        }
        catch (MalformedURLException e)
        {
            MessengerManager.showToast(getActivity(), R.string.error_login);
            return;
        }

        url = u.toString();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }
}