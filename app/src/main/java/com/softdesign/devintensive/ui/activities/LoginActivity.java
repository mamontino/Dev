package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends BaseActivity implements View.OnClickListener
{
    public static final String TAG = ConstantManager.TAG_PREFIX + "Login Activity";

    private Button signIn;
    private TextView rememberPassword;
    private EditText login, password;
    private CoordinatorLayout coordinationLayout;
    private DataManager dataManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate");

        dataManager = DataManager.getInstance();

        signIn = (Button) findViewById(R.id.email_sign_in_button);
        coordinationLayout = (CoordinatorLayout) findViewById(R.id.main_coordination_container);
        rememberPassword = (TextView) findViewById(R.id.remember_txt);
        login = (EditText) findViewById(R.id.sing_in_email);
        password = (EditText) findViewById(R.id.sing_in_password);

        signIn.setOnClickListener(this);
        rememberPassword.setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Log.d(TAG, "onPause");
        super.onPause();
    }


    @Override
    protected void onStop()
    {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onRestart()
    {
        Log.d(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.email_sign_in_button:
                loginButton();
                break;
            case R.id.remember_txt:
                rememberPassword();
                break;
        }
    }

    private void loginSuccess(UserModelRes userModel)
    {
        showSnackbar(userModel.getData().getToken());
        dataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        dataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());
        dataManager.getPreferencesManager().saveUserPhoto(Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto()));
        dataManager.getPreferencesManager().saveAvatarImage(Uri.parse(userModel.getData().getUser().getPublicInfo().getAvatar()));
        dataManager.getPreferencesManager().saveUserFullName(userModel.getData().getUser().getFirstName() + " " + userModel.getData().getUser().getSecondName());
        saveUserValues(userModel);
        saveUserData(userModel);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void loginButton()
    {
        if (NetworkStatusChecker.isNetworkAvailable(this))
        {
            Call<UserModelRes> call = dataManager.loginUser(new UserLoginReq(login.getText().toString(), password.getText().toString()));
            call.enqueue(new Callback<UserModelRes>()
            {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response)
                {
                    if (response.code() == 200)
                    {
                        loginSuccess(response.body());
                    } else if (response.code() == 404)
                    {
                        showSnackbar("Неверный логин или пароль");
                    } else
                    {
                        showSnackbar("Всё пропало Шев!");
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t)
                {
                    showSnackbar("Ошибка: " + t.getMessage());
                }
            });
        } else
        {
            showSnackbar("Сеть на данный момент не доступна, попробуйте позже.");
        }
    }

    private void showSnackbar(String massage)
    {
        Snackbar.make(coordinationLayout, massage, Snackbar.LENGTH_LONG).show();
    }

    private void rememberPassword()
    {
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(rememberIntent);
    }

    private void saveUserValues(UserModelRes userModel)
    {
        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };
        dataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }

    private void saveUserFieldsValue(UserModelRes userModel)
    {
        List<String> userFieldValues = new ArrayList<>();
        userFieldValues.add(userModel.getData().getUser().getContacts().getPhone());
        userFieldValues.add(userModel.getData().getUser().getContacts().getEmail());
        userFieldValues.add(userModel.getData().getUser().getContacts().getVk());
        userFieldValues.add(userModel.getData().getUser().getRepositories().getRepo().get(0).getGit());
        String bio = userModel.getData().getUser().getPublicInfo().getBio();
        userFieldValues.add(bio.isEmpty() ? getString(R.string.user_profile_empty_bio) : bio);
        dataManager.getPreferencesManager().saveUserProfileData(userFieldValues);
    }

    private void saveUserPhotoAndAvatar(UserModelRes userModel)
    {
        Uri photo = Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto());
        Uri avatar = Uri.parse(userModel.getData().getUser().getPublicInfo().getAvatar());
        dataManager.getPreferencesManager().saveUserPhoto(photo);
        dataManager.getPreferencesManager().saveAvatarImage(avatar);
    }

    private void saveFirstSecondNameUser(UserModelRes userModel)
    {
        String firstName = userModel.getData().getUser().getFirstName();
        String secondName = userModel.getData().getUser().getSecondName();
        dataManager.getPreferencesManager().saveFirstSecondNameUser(firstName, secondName);
    }

    private void saveUserData(UserModelRes userModel)
    {
        List<String> userData = new ArrayList<>();
        userData.add(userModel.getData().getUser().getContacts().getPhone());
        userData.add(userModel.getData().getUser().getContacts().getEmail());
        userData.add(userModel.getData().getUser().getContacts().getVk());
        userData.add(userModel.getData().getUser().getRepositories().getRepo().get(0).getGit());
        userData.add(userModel.getData().getUser().getPublicInfo().getBio());

        dataManager.getPreferencesManager().saveUserProfileData(userData);
    }
}
