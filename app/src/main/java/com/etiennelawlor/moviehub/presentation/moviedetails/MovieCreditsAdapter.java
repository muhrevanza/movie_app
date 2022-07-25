package com.etiennelawlor.moviehub.presentation.moviedetails;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etiennelawlor.moviehub.R;
import com.etiennelawlor.moviehub.presentation.base.BaseAdapter;
import com.etiennelawlor.moviehub.presentation.common.widget.DynamicHeightImageView;
import com.etiennelawlor.moviehub.presentation.models.MovieCreditPresentationModel;
import com.etiennelawlor.moviehub.util.AnimationUtility;
import com.etiennelawlor.moviehub.util.ColorUtility;
import com.etiennelawlor.moviehub.util.DisplayUtility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by etiennelawlor on 12/17/16.
 */

public class MovieCreditsAdapter extends BaseAdapter<MovieCreditPresentationModel> {

    // region Constants
    // endregion

    // region Static Variables
    private static int ivWidth;
    // endregion

    // region Member Variables
    private FooterViewHolder footerViewHolder;
    // endregion

    // region Constructors

    public MovieCreditsAdapter(Context context) {
        int screenWidth = DisplayUtility.getScreenWidth(context);
        int peekWidth = DisplayUtility.dp2px(context, 32);
        ivWidth = (screenWidth - peekWidth) / 3;
    }

    // endregion

    @Override
    public int getItemViewType(int position) {
        return (isLastPosition(position) && isFooterAdded) ? FOOTER : ITEM;
    }

    @Override
    protected RecyclerView.ViewHolder createHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    protected RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_credit_card, parent, false);

        final MovieCreditViewHolder holder = new MovieCreditViewHolder(v);

        holder.itemView.setOnClickListener(v1 -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(adapterPos, holder.itemView);
                }
            }
        });

        return holder;
    }

    @Override
    protected RecyclerView.ViewHolder createFooterViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_footer, parent, false);
        StaggeredGridLayoutManager.LayoutParams layoutParams = ((StaggeredGridLayoutManager.LayoutParams) v.getLayoutParams());
        layoutParams.setFullSpan(true);
        v.setLayoutParams(layoutParams);

        final FooterViewHolder holder = new FooterViewHolder(v);
        holder.reloadButton.setOnClickListener(v1 -> {
            if(onReloadClickListener != null){
                onReloadClickListener.onReloadClick();
            }
        });

        return holder;
    }

    @Override
    protected void bindHeaderViewHolder(RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    protected void bindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final MovieCreditViewHolder holder = (MovieCreditViewHolder) viewHolder;

        final MovieCreditPresentationModel movieCredit = getItem(position);
        if (movieCredit != null) {
            holder.bind(movieCredit);
        }
    }

    @Override
    protected void bindFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        FooterViewHolder holder = (FooterViewHolder) viewHolder;
        footerViewHolder = holder;
    }

    @Override
    protected void displayLoadMoreFooter() {
        if(footerViewHolder!= null){
            footerViewHolder.errorRelativeLayout.setVisibility(View.GONE);
            footerViewHolder.loadingFrameLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void displayErrorFooter() {
        if(footerViewHolder!= null){
            footerViewHolder.loadingFrameLayout.setVisibility(View.GONE);
            footerViewHolder.errorRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void addHeader() {

    }

    @Override
    public void addFooter() {
        isFooterAdded = true;
        add(new MovieCreditPresentationModel());
    }

    // region Inner Classes

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        // region Constructors
        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
        // endregion
    }

    public static class MovieCreditViewHolder extends RecyclerView.ViewHolder {
        // region Views
        @BindView(R.id.thumbnail_iv)
        DynamicHeightImageView thumbnailImageView;
        @BindView(R.id.info_ll)
        LinearLayout infoLinearLayout;
        @BindView(R.id.title_tv)
        TextView titleTextView;
        @BindView(R.id.subtitle_tv)
        TextView subtitleTextView;
        // endregion

        // region Constructors
        public MovieCreditViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
        // endregion

        // region Helper Methods
        private void bind(MovieCreditPresentationModel movieCredit){
            resetInfoBackgroundColor(infoLinearLayout);
            resetTitleTextColor(titleTextView);
            resetSubtitleTextColor(subtitleTextView);

            setUpThumbnail(this, movieCredit);
            setUpTitle(titleTextView, movieCredit);
            setUpSubtitle(subtitleTextView, movieCredit);
        }

        private void setUpThumbnail(final MovieCreditViewHolder vh, final MovieCreditPresentationModel movieCredit){
            final DynamicHeightImageView iv = vh.thumbnailImageView;
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) iv.getLayoutParams();
            layoutParams.width = ivWidth;
            iv.setLayoutParams(layoutParams);

            double heightRatio = 3.0D/2.0D;

            iv.setHeightRatio(heightRatio);

            String posterUrl = movieCredit.getProfileUrl();
            if (!TextUtils.isEmpty(posterUrl)) {
                Picasso.with(iv.getContext())
                        .load(posterUrl)
                        .resize(ivWidth, (int)(heightRatio*ivWidth))
                        .centerCrop()
                        .into(iv, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap bitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
                                Palette.from(bitmap).generate(palette -> {
                                    setUpInfoBackgroundColor(vh.infoLinearLayout, palette);
                                    setUpTitleTextColor(vh.titleTextView, palette);
                                    setUpSubtitleTextColor(vh.subtitleTextView, palette);
                                });
                            }

                            @Override
                            public void onError() {

                            }
                        });

            }
        }

        private void resetInfoBackgroundColor(LinearLayout ll) {
            ll.setBackgroundColor(ContextCompat.getColor(ll.getContext(), R.color.grey_800));
        }

        private void setUpInfoBackgroundColor(LinearLayout ll, Palette palette) {
            Palette.Swatch swatch = ColorUtility.getMostPopulousSwatch(palette);
            if(swatch != null){
                int startColor = ContextCompat.getColor(ll.getContext(), R.color.grey_800);
                int endColor = swatch.getRgb();

                AnimationUtility.animateBackgroundColorChange(ll, startColor, endColor);
            }
        }

        private void setUpTitle(TextView tv, MovieCreditPresentationModel movieCredit){
            String name = movieCredit.getName();
            if (!TextUtils.isEmpty(name)) {
                tv.setText(name);
            }
        }

        private void resetTitleTextColor(TextView tv) {
            tv.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.primary_text_light));
        }

        private void setUpTitleTextColor(final TextView tv, Palette palette){
            Palette.Swatch swatch = ColorUtility.getMostPopulousSwatch(palette);
            if(swatch != null){
                int startColor = ContextCompat.getColor(tv.getContext(), R.color.primary_text_light);
                int endColor = swatch.getTitleTextColor();

                AnimationUtility.animateTextColorChange(tv, startColor, endColor);
            }
        }

        private void setUpSubtitle(TextView tv, MovieCreditPresentationModel movieCredit){
            String job = movieCredit.getJob();
            String character = movieCredit.getCharacter();
            if (!TextUtils.isEmpty(job)) {
                tv.setText(job);
            } else if (!TextUtils.isEmpty(character)) {
                tv.setText(character);
            }
        }

        private void resetSubtitleTextColor(TextView tv) {
            tv.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.secondary_text_light));
        }

        private void setUpSubtitleTextColor(final TextView tv, Palette palette){
            Palette.Swatch swatch = ColorUtility.getMostPopulousSwatch(palette);
            if(swatch != null){
                int startColor = ContextCompat.getColor(tv.getContext(), R.color.secondary_text_light);
                int endColor = swatch.getBodyTextColor();

                AnimationUtility.animateTextColorChange(tv, startColor, endColor);
            }
        }
        // endregion
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        // region Views
        @BindView(R.id.loading_fl)
        FrameLayout loadingFrameLayout;
        @BindView(R.id.error_rl)
        RelativeLayout errorRelativeLayout;
        @BindView(R.id.retry_btn)
        Button reloadButton;
        // endregion

        // region Constructors
        public FooterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
        // endregion
    }
    // endregion
}
