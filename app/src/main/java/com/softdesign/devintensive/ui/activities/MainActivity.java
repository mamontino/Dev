package com.softdesign.devintensive.ui.activities;


import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.PhotoUploadService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.utils.AvatarRounded;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.RoundImage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener
{
    private static final String TAG = ConstantManager.TAG_PREFIX + "MainActivity";
    private DataManager mDataManager;
    private int mCurrentEditMode = 0;
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    private FloatingActionButton mFab;
    private ImageView mCollImage;
    private ImageView mSendEmailImage;
    private ImageView mOpenVkImage;
    private ImageView mOpenRepositoryImage;
    private RelativeLayout mProfilePlaceholder;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage;
    private AppBarLayout mAppBarLayout;
    private ImageView mAvatarImage;
    private EditText mUserPhone, mUserMail, mUserVk, mUserGit, mUserBio;
    private List<EditText> mUserInfoViews;
    private String mUserName, mMail;
    private TextView mUserValueRating, mUserValueCodeLines, mUserValueProjects;
    private List<TextView> mUserValueViews;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();
        mUserName = (String) getText(R.string.user_name);
        mMail = (String) getText(R.string.mail_text);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordination_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mCollImage = (ImageView) findViewById(R.id.coll_img);
        mSendEmailImage = (ImageView) findViewById(R.id.send_email_img);
        mOpenVkImage = (ImageView) findViewById(R.id.open_vk_img);
        mOpenRepositoryImage = (ImageView) findViewById(R.id.open_repository_img);

        mProfilePlaceholder = (RelativeLayout) findViewById(R.id.profile_placeholder);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mAvatarImage = (ImageView) findViewById(R.id.user_photo_img); // или id = coll_img

        mUserPhone = (EditText) findViewById(R.id.phone_et);
        mUserMail = (EditText) findViewById(R.id.mail_et);
        mUserVk = (EditText) findViewById(R.id.vk_profil_id_et);
        mUserGit = (EditText) findViewById(R.id.repositorii_et);
        mUserBio = (EditText) findViewById(R.id.about_et);

        mUserValueRating = (TextView) findViewById(R.id.rating_value_tv);
        mUserValueCodeLines = (TextView) findViewById(R.id.lines_code_value_tv);
        mUserValueProjects = (TextView) findViewById(R.id.project_value_tv);

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVk);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserBio);

        mUserValueViews = new ArrayList<>();
        mUserValueViews.add(mUserValueRating);
        mUserValueViews.add(mUserValueCodeLines);
        mUserValueViews.add(mUserValueProjects);

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);
        mCollImage.setOnClickListener(this);
        mSendEmailImage.setOnClickListener(this);
        mOpenVkImage.setOnClickListener(this);
        mOpenRepositoryImage.setOnClickListener(this);

        setToolbar();
        setupDrawer();
        initUserFields();
        initUserInfoValue();
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_foto_256)
                .into(mAvatarImage);
        loadAvatar();

        if (savedInstanceState == null)
        {
        } else
        {
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause");
        saveUserFields();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.fab:  // Кнопка переключения редактирования профиля
                if (mCurrentEditMode == 0)
                {
                    changeEditMode(1);
                    mCurrentEditMode = 1;
                } else
                {
                    changeEditMode(0);
                    mCurrentEditMode = 0;
                }
                break;
            case R.id.profile_placeholder:
                // TODO Сделат выбор откуда загружать фото
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
            case R.id.coll_img: // Совершение звонка по нажатию иконки звонка
                callPhone();
                break;
            case R.id.send_email_img: // Создание и Отправка электронного письма по нажатию иконки звонка
                sendEmail();
                break;
            case R.id.open_vk_img: // Открытие vk.com профиля по нажатию иконки звонка
                openUrl(mUserVk);
                break;
            case R.id.open_repository_img: // Открытие репозитория по нажатию иконки звонка
                openUrl(mUserGit);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    private void showSnackbar(String message)
    {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setToolbar()
    {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
        if (actionBar != null)
        {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer()
    {
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        setupDrawerHeader(navigationView, getRoundBitmap(R.drawable.user_foto_256), mUserName, mMail);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                showSnackbar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);

                if (item.getItemId() == R.id.user_profile_menu)
                {

                }

                if (item.getItemId() == R.id.team_menu)
                {
                    Intent authIntent = new Intent(MainActivity.this, UserListActivity.class);
                    finish();
                    startActivity(authIntent);
                }

                return false;
            }
        });
        mAvatarImage = navigationView.getHeaderView(ConstantManager.NULL).findViewById(R.id.coll_img);
        TextView mUserName = navigationView.getHeaderView(ConstantManager.NULL).findViewById(R.id.user_name_text);
        TextView mUserEmail = navigationView.getHeaderView(ConstantManager.NULL).findViewById(R.id.user_email_text);
        mUserEmail.setText(mDataManager.getPreferencesManager().loadUserProfileData().get(ConstantManager.NUMBER_VIEW_IN_ARRAY_EMAIL));
        mDataManager.getPreferencesManager().loadFirstSecondNameUser();
        mUserName.setText(String.format("%s %s", mDataManager.getPreferencesManager().loadFirstSecondNameUser().get(0),
                mDataManager.getPreferencesManager().loadFirstSecondNameUser().get(1)));
    }

    private Bitmap getRoundBitmap(int drawableRes)
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableRes);
        bitmap = RoundImage.getRoundedBitmap(bitmap);
        return bitmap;
    }

    private void setupDrawerHeader(NavigationView parent, Bitmap avatar, String name, String email)
    {
        View view = parent.getHeaderView(0);
        if (avatar != null)
        {
            ImageView imageView = view.findViewById(R.id.coll_img);
            imageView.setImageBitmap(avatar);
        }
        if (name != null)
        {
            TextView textView = view.findViewById(R.id.user_name_text);
            textView.setText(name);
        }
        if (email != null)
        {
            TextView textView = view.findViewById(R.id.user_email_text);
            textView.setText(email);
        }
    }

    private void changeEditMode(int mode)
    {
        if (mode == 1)
        {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews)
            {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
                showProfilePlaceholder();
                lockToolbar();
                mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
            }
        } else
        {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews)
            {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
                hideProfilePlaceholder();
                unlockTootbar();
                mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.color_white));
                saveUserFields();
            }
        }
    }

    private void initUserFields()
    {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++)
        {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    private void saveUserFields()
    {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews)
        {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void initUserInfoValue()
    {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValues();
        for (int i = 0; i < userData.size(); i++)
        {
            mUserValueViews.get(i).setText(userData.get(i));
        }
    }

    @Override
    public void onBackPressed()
    {
        if (mNavigationDrawer.isDrawerVisible(GravityCompat.START))
        {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }

    }

    private void loadPhotoFromGallery()
    {
        Intent takeGaleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takeGaleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGaleryIntent, getString(R.string.user_profile_chose_messege)), ConstantManager.REQUEST_GALLERY_PICTURE);
    }

    private void loadPhotoFromCamera()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try
            {
                mPhotoFile = createImageFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            if (mPhotoFile != null)
            {
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
                uploadPhoto(mPhotoFile.toURI());
            } else
            {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

                Snackbar.make(mCoordinatorLayout, R.string.message_need_take_permissions, Snackbar.LENGTH_LONG)
                        .setAction("Разрешить", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                openApplicationSettings();
                            }
                        }).show();
            }
        }
    }

    private void uploadPhoto(URI uri)
    {
        PhotoUploadService service = ServiceGenerator.createService(PhotoUploadService.class);
        File file = new File(uri);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);
        String descriptionString = "hello, this is description speaking";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
        Call<ResponseBody> call = service.upload(description, body);
        call.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length == 2)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {

            }
        }
    }

    private void hideProfilePlaceholder()
    {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    private void showProfilePlaceholder()
    {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    private void lockToolbar()
    {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbarLayout.setLayoutParams(mAppBarParams);
    }

    private void unlockTootbar()
    {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbarLayout.setLayoutParams(mAppBarParams);
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_galerey), getString(R.string.user_profile_dialog_camera), getString(R.string.user_profile_dialog_cancel)};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_dialog_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem)
                    {
                        switch (choiceItem)
                        {
                            case 0:
                                // TODO: 03.07.2016   Загрузить из галереи
                                loadPhotoFromGallery();
                                break;
                            case 1:
                                // TODO: 03.07.2016 Загрузить из камеры
                                loadPhotoFromCamera();
                                break;
                            case 2:
                                // TODO: 03.07.2016 Отмена
                                dialog.cancel();
                                break;
                        }
                    }
                });
                return builder.create();
            default:
                return null;
        }
    }

    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return image;
    }

    private void loadAvatar()
    {
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadAvatarImage())
                .placeholder(R.drawable.ava_mini)
                .transform(new AvatarRounded())
                .into(mAvatarImage);
    }


    public void openApplicationSettings()
    {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }

    private void callPhone()
    {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mUserPhone.getText().toString()));
        startActivity(intent);
    }

    private void sendEmail()
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{String.valueOf(mUserMail.getText().toString())});
        startActivity(Intent.createChooser(intent, "Сообщение отправляется"));
    }

    private void openUrl(EditText editText)
    {
        Uri uri = Uri.parse("https://" + editText.getText());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
