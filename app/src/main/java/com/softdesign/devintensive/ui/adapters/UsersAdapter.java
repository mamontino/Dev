package com.softdesign.devintensive.ui.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>
{
    private Context context;
    private List<UserListRes.UserData> users;
    private UserViewHolder.CustomClickListener customClickListener;

    public UsersAdapter(List<UserListRes.UserData> users, UserViewHolder.CustomClickListener customClickListener)
    {
        this.users = users;
        this.customClickListener = customClickListener;
    }

    @Override
    public UsersAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(convertView, customClickListener);
    }

    @Override
    public void onBindViewHolder(UsersAdapter.UserViewHolder holder, int position)
    {
        UserListRes.UserData user = users.get(position);
        Picasso.with(context)
                .load(user.getPublicInfo().getPhoto())
                .resize(context.getResources().getDimensionPixelSize(R.dimen.profile_image_size),
                        context.getResources().getDimensionPixelSize(R.dimen.profile_image_size))
                .placeholder(context.getResources().getDrawable(R.drawable.header_bg_1))
                .error(context.getResources().getDrawable(R.drawable.header_bg_1))
                .into(holder.userPhoto);
        holder.fullName.setText(String.valueOf(user.getFullName()));
        holder.rating.setText(String.valueOf(user.getProfileValues().getRating()));
        holder.codeLines.setText(String.valueOf(user.getProfileValues().getLinesCode()));
        holder.projects.setText(String.valueOf(user.getProfileValues().getProjects()));
        if (user.getPublicInfo().getBio() == null || user.getPublicInfo().getBio().isEmpty())
        {
            holder.bio.setVisibility(View.GONE);
        } else
        {
            holder.bio.setVisibility(View.VISIBLE);
            holder.bio.setText(user.getPublicInfo().getBio());
        }
    }

    @Override
    public int getItemCount()
    {
        return users.size();
    }

    public void setFilter(List<UserListRes.UserData> users)
    {
        this.users = new ArrayList<>();
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public UserListRes.UserData getUser(int position)
    {
        return users.get(position);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        protected AspectRatioImageView userPhoto;
        protected TextView fullName, rating, codeLines, projects, bio;
        protected Button showMore;

        protected CustomClickListener clickListener;

        public UserViewHolder(View itemView, CustomClickListener customClickListener)
        {
            super(itemView);
            this.clickListener = customClickListener;

            userPhoto = itemView.findViewById(R.id.user_photo_img);
            fullName = itemView.findViewById(R.id.user_full_name);
            rating = itemView.findViewById(R.id.rating_txt);
            codeLines = itemView.findViewById(R.id.code_lines_txt);
            projects = itemView.findViewById(R.id.projects_txt);
            bio = itemView.findViewById(R.id.bio_txt);
            showMore = itemView.findViewById(R.id.more_info_btn);

            showMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if (clickListener != null)
            {
                clickListener.onUserItemClickListener(getAdapterPosition());
            }
        }

        public interface CustomClickListener
        {
            void onUserItemClickListener(int position);
        }
    }
}
