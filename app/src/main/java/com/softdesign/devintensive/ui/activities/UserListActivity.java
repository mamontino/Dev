package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.ui.fragments.RetainFragment;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.RoundImage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends BaseActivity implements SearchView.OnQueryTextListener
{
    private static final String TAG = ConstantManager.TAG_PREFIX + " UserListActivity";
    private static final String TAG_RETAIN_FRAGMENT = "retain_fragment";
    private ImageView drawerUsrAvatar;
    private DataManager mDataManager;
    private UsersAdapter mUsersAdapter;
    private RetainFragment mRetainFragment;
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    private RecyclerView mRecyclerView;
    private NavigationView mNavigationView;
    private List<UserListRes.UserData> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mDataManager = DataManager.getInstance();
        mCoordinatorLayout = findViewById(R.id.main_coordination_container);
        mToolbar = findViewById(R.id.toolbar);
        mNavigationDrawer = findViewById(R.id.navigation_drawer);
        mRecyclerView = findViewById(R.id.user_list);
        mNavigationView = findViewById(R.id.navigation_view);

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRetainFragment = (RetainFragment) getSupportFragmentManager().findFragmentByTag(TAG_RETAIN_FRAGMENT);
        if (mRetainFragment == null)
        {
            mRetainFragment = new RetainFragment();
            getSupportFragmentManager().beginTransaction().add(mRetainFragment, TAG_RETAIN_FRAGMENT).commit();
        }

        mDataManager = DataManager.getInstance();

        setupToolbar();
        setupDrawer();
        loadUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        final List<UserListRes.UserData> filteredModelList = filter(mRetainFragment.getUsersList(), newText);
        mUsersAdapter.setFilter(filteredModelList);
        return false;
    }

    private List<UserListRes.UserData> filter(List<UserListRes.UserData> models, String query)
    {
        query = query.toLowerCase();

        final List<UserListRes.UserData> filteredModelList = new ArrayList<>();
        for (UserListRes.UserData model : models)
        {
            final String text = model.getFullName().toLowerCase();
            if (text.contains(query))
            {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
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

    private void showSnackbar(String message)
    {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setupDrawer()
    {
        drawerUsrAvatar = mNavigationView.getHeaderView(0).findViewById(R.id.coll_img);
        TextView drawerUserFullName = mNavigationView.getHeaderView(0).findViewById(R.id.user_name_text);
        TextView drawerUserEmail = mNavigationView.getHeaderView(0).findViewById(R.id.user_email_text);

        drawerUserFullName.setText(mDataManager.getPreferencesManager().getUserFullName());
        drawerUserEmail.setText(mDataManager.getPreferencesManager().getUserEmail());

        insertDrawerAvatar(mDataManager.getPreferencesManager().loadAvatarImage());

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                showSnackbar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);

                if (item.getItemId() == R.id.user_profile_menu)
                {
                    Intent authIntent = new Intent(UserListActivity.this, MainActivity.class);
                    finish();
                    startActivity(authIntent);
                }

                if (item.getItemId() == R.id.team_menu)
                {

                }

                return false;
            }
        });
    }

    private void insertDrawerAvatar(Uri selectedImage)
    {
        Picasso.with(this)
                .load(selectedImage)
                .resize(getResources().getDimensionPixelSize(R.dimen.drawer_header_avatar_size),
                        getResources().getDimensionPixelSize(R.dimen.drawer_header_avatar_size))
                .centerCrop()
                .transform(new RoundImage())
                .placeholder(R.drawable.header_bg_1)
                .into(drawerUsrAvatar);
    }

    private void loadUsers()
    {
        Call<UserListRes> call = mDataManager.getUserList();

        call.enqueue(new Callback<UserListRes>()
        {
            @Override
            public void onResponse(Call<UserListRes> call, Response<UserListRes> response)
            {
                try
                {
                    mUsers = response.body().getData();
                    mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener()
                    {
                        @Override
                        public void onUserItemClickListener(int position)
                        {
                            UserDTO userDTO = new UserDTO(mUsers.get(position));
                            Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                            profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                            startActivity(profileIntent);
                        }
                    });
                    mRecyclerView.setAdapter(mUsersAdapter);
                } catch (NullPointerException e)
                {
                    Log.e(TAG, e.toString());
                    showSnackbar("Что то пошло не так!");
                }
            }

            @Override
            public void onFailure(Call<UserListRes> call, Throwable t)
            {
            }
        });
    }

    private void setupToolbar()
    {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
