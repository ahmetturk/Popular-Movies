package com.ahmetroid.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ahmetroid.popularmovies.R;
import com.ahmetroid.popularmovies.databinding.ItemVideoBinding;
import com.ahmetroid.popularmovies.model.Video;
import com.ahmetroid.popularmovies.ui.DetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {

    private final Context mContext;
    private List<Video> mList;

    public VideoAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public VideoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        ItemVideoBinding binding = ItemVideoBinding.inflate(layoutInflater, parent, false);
        return new VideoAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapterViewHolder holder, int position) {
        Video video = mList.get(position);
        holder.bind(video);
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    public void addVideosList(List<Video> videosList) {
        mList = videosList;
        notifyDataSetChanged();
    }

    public ArrayList<Video> getList() {
        return (ArrayList<Video>) mList;
    }

    public class VideoAdapterViewHolder extends RecyclerView.ViewHolder {

        final ItemVideoBinding binding;

        VideoAdapterViewHolder(ItemVideoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Video video) {
            binding.setVideo(video);
            binding.setPresenter((DetailActivity) mContext);

            String photoUrl = String.format("https://img.youtube.com/vi/%s/0.jpg", video.videoUrl);
            Picasso.get()
                    .load(photoUrl)
                    .error(R.drawable.error)
                    .into(binding.videoIv);
        }
    }
}