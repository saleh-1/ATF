package com.sprojects.atf;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ATFViews
{
    // #####################################################################

    public static View getViewByResId(Context context, int resId)
    {
        try {

            LayoutInflater inflater = LayoutInflater.from(context);
            return inflater.inflate(resId, null);

        }catch (Exception e){}

        return null;
    }

    // #####################################################################

    public static View viewAlert(Context context, String text)
    {
        try{
            View v = getViewByResId(context, R.layout.view_alert);
            if(v == null) return null;

            TextView textView = v.findViewById(R.id.view_alert_text);
            textView.setText(text);

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }

    // #####################################################################

    public static View viewVerticalSpace(Context context, int height, boolean bottomSeparator)
    {
        try{
            View view = getViewByResId(context, R.layout.view_vertical_space);
            if(view == null) return null;

            View v = view.findViewById(R.id.view_vertical_space_view);

            v.getLayoutParams().height = height;

            if(!bottomSeparator)
            {
                View hl = view.findViewById(R.id.horizontal_line);
                hl.setVisibility(View.INVISIBLE);
            }

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }


    // #####################################################################

    public static View navBarButtonView(Context context, int imageResource, final Runnable runnable)
    {
        try{
            View v = getViewByResId(context, R.layout.view_navbar_button_icon);
            if(v == null) return null;

            ImageView img = v.findViewById(R.id.view_navbar_button_icon_image);
            img.setImageResource(imageResource);

            if(runnable != null)
            {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runnable.run();
                    }
                });
            }

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }


    // #####################################################################

    public static View navBarButtonView(Context context, String title, final Runnable runnable)
    {
        try{
            View v = getViewByResId(context, R.layout.view_navbar_button_title);
            if(v == null) return null;

            TextView tvTitle = v.findViewById(R.id.view_navbar_button_title_text);
            tvTitle.setText(title);

            if(runnable != null)
            {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runnable.run();
                    }
                });
            }

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }


    // #####################################################################

    public static View viewTitleIconAction(Context context, String title, int imageResource, final Runnable runnable)
    {
        try{
            View v = ATFViews.getViewByResId(context, R.layout.view_title_icon_action);
            if(v == null) return null;

            TextView tvTitle = v.findViewById(R.id.view_title_icon_action_text);
            tvTitle.setText(title);

            ImageView img = v.findViewById(R.id.view_title_icon_action_image);

            if(imageResource == 0)
            {
                img.setVisibility(View.INVISIBLE);

            }else{

                img.setImageResource(imageResource);
            }

            if(runnable != null)
            {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runnable.run();
                    }
                });
            }

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }

    // #####################################################################

    public static View viewButton(Context context, String title, final Runnable runnable)
    {
        try{
            View v = ATFViews.getViewByResId(context, R.layout.view_button);
            if(v == null) return null;

            TextView Title = v.findViewById(R.id.view_button_text);
            Title.setText(title);

            if(runnable != null)
            {
                Title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runnable.run();
                    }
                });
            }

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }

    // #####################################################################

    public static View viewListItemWithImage(Context context, String title, String img_url, String details, String extra, final Runnable runnable, final Runnable optionsRunnable)
    {
        try{
            View v = ATFViews.getViewByResId(context, R.layout.view_list_item_with_image);
            if(v == null) return null;

            //

            ImageView img = v.findViewById(R.id.view_list_item_with_image_image);
            ATFUtils.showImageFromURL(context, img, img_url);

            //
            TextView tvTitle = v.findViewById(R.id.view_list_item_with_image_title);
            tvTitle.setText(title);

            //
            TextView tvDetails = v.findViewById(R.id.view_list_item_with_image_details);
            tvDetails.setText(details);

            //
            TextView tvExtra = v.findViewById(R.id.view_list_item_with_image_extra);
            tvExtra.setText(extra);

            //
            ImageView options = v.findViewById(R.id.view_list_item_with_image_options);

            if(optionsRunnable != null)
            {
                options.setImageResource(R.drawable.menu);
                options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optionsRunnable.run();
                    }
                });
            }else {

                options.setVisibility(View.INVISIBLE);
            }


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runnable.run();
                }
            });

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }


    // #####################################################################

    public static View viewImage(final Context context, String img_url, final Runnable runnable)
    {
        try{
            View v = ATFViews.getViewByResId(context, R.layout.view_image);
            if(v == null) return null;

            //
            final ImageView img = v.findViewById(R.id.view_image_image);
            ATFUtils.showImageFromURL(context, img, img_url);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(runnable != null) runnable.run();

                    //Bitmap curBitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();
                    //Bitmap newBitmap = curBitmap.copy(curBitmap.getConfig(), true);
                    //Utils.imageViewer(context, newBitmap);
                }
            });

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }

    // #####################################################################

    public static View viewListItem(Context context, String title, String details, String extra, boolean isBoldTitle, final Runnable runnable, int optionsIconResource, final Runnable optionsRunnable)
    {
        try{
            View v = ATFViews.getViewByResId(context, R.layout.view_list_item);
            if(v == null) return null;

            TextView tvTitle = v.findViewById(R.id.view_list_item_title);
            tvTitle.setText(title);

            if(!isBoldTitle) tvTitle.setTypeface(null, Typeface.BOLD);

            TextView tvDetails = v.findViewById(R.id.view_list_item_details);
            tvDetails.setText(details);

            TextView tvExtra = v.findViewById(R.id.view_list_item_extra);
            tvExtra.setText(extra);


            ImageView icon = v.findViewById(R.id.view_list_item_options);

            if(optionsIconResource == 0)
            {
                icon.setVisibility(View.INVISIBLE);

            }else{

                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(optionsIconResource);

                if(optionsRunnable != null)
                {
                    icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            optionsRunnable.run();
                        }
                    });
                }
            }

            if(runnable != null)
            {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runnable.run();
                    }
                });
            }

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }


    // #####################################################################

    public static View viewTitleWithText(Context context, String title, String text, final Runnable runnable)
    {
        try{
            View v = ATFViews.getViewByResId(context, R.layout.view_title_with_text);
            if(v == null) return null;

            TextView tvTitle = v.findViewById(R.id.view_title_with_text_title);
            tvTitle.setText(title);

            TextView tvText = v.findViewById(R.id.view_title_with_text_text);
            tvText.setText(text);

            if(runnable != null)
            {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runnable.run();
                    }
                });
            }

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }


    // #####################################################################

    public static View viewDashboard(Context context, String title, int iconResource, String iconUrl, final Runnable runnable)
    {
        try{
            View v = ATFViews.getViewByResId(context, R.layout.view_dashboard);
            if(v == null) return null;

            TextView tvTitle = v.findViewById(R.id.view_dashboard_title);
            tvTitle.setText(title);

            ImageView icon = v.findViewById(R.id.view_dashboard_icon);

            if(iconResource > 0)
            {
                icon.setImageResource(iconResource);

            }else{

                ATFUtils.showImageFromURL(context, icon, iconUrl);
            }

            if(runnable != null)
            {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runnable.run();
                    }
                });
            }

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }


    // #####################################################################

    public static View viewGroupTitle(Context context, String title, final Runnable runnable)
    {
        try{
            View v = ATFViews.getViewByResId(context, R.layout.view_group_title);
            if(v == null) return null;

            TextView tvTitle = v.findViewById(R.id.view_group_title_text);
            tvTitle.setText(title);

            if(runnable != null)
            {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runnable.run();
                    }
                });
            }

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }


    // #####################################################################

    public static View viewActionSheetTitle(Context context, String text)
    {
        try{

            View v = ATFViews.getViewByResId(context, R.layout.view_actionsheet_title);
            if(v == null) return null;

            TextView textView = v.findViewById(R.id.view_actionsheet_title_text);
            textView.setText(text);

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }


    // #####################################################################

    public static View viewEditText(Context context, int tag, String title, String text)
    {
        return viewEditText(context, tag, title, text, 0, false);
    }


    // #####################################################################

    public static View viewEditText(Context context, int tag, String title, String text, int inputType, boolean focus)
    {
        try{
            View v = ATFViews.getViewByResId(context, R.layout.view_edit_text);
            if(v == null) return null;

            TextView tvTitle = v.findViewById(R.id.view_edit_text_title);
            tvTitle.setText(title);

            EditText etText = v.findViewById(R.id.view_edit_text_et);
            etText.setTag(tag);
            etText.setText(text);

            if(focus) ATFUtils.makeFocus(context, etText);

            if(inputType > 0) etText.setInputType(inputType);

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }


    // #####################################################################

    public static View viewEditTextDescription(Context context, int tag, String title, String text, boolean focus)
    {
        try{

            View v = ATFViews.getViewByResId(context, R.layout.view_edit_description);
            if(v == null) return null;

            TextView tvTitle = v.findViewById(R.id.view_edit_description_title);
            tvTitle.setText(title);

            EditText etText = v.findViewById(R.id.view_edit_description_et);
            etText.setTag(tag);
            etText.setText(text);

            if(focus) ATFUtils.makeFocus(context, etText);

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }


    // #####################################################################

    public static View viewSearchBar(final Context context, int tag, String hint, final ATFUtils.RunnableWithObject runnableWithObject)
    {
        try{
            View v = ATFViews.getViewByResId(context, R.layout.view_search_bar);
            if(v == null) return null;

            final EditText etText = v.findViewById(R.id.view_search_bar_et);
            etText.setTag(tag);
            etText.setHint(hint);
            etText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    v.clearFocus();
                    ATFUtils.hideKeyboard((FragmentActivity) context);

                    if(runnableWithObject == null) return false;

                    if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED)
                    {
                        runnableWithObject.run(etText.getText().toString());
                        return true;
                    }
                    return false;
                }
            });

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }


    // #####################################################################

    public static View viewSlideViews(Context context, ArrayList<View> views)
    {
        try{
            if(views == null || views.size() == 0) return null;

            View v = ATFViews.getViewByResId(context, R.layout.view_slide_views);
            if(v == null) return null;

            //
            ViewPager vp = v.findViewById(R.id.view_slide_views_pager);
            TabLayout tabLayout = v.findViewById(R.id.view_slide_views_dots);
            tabLayout.setupWithViewPager(vp, true);

            CustomPagerAdapter adapter = new CustomPagerAdapter(context, views);

            vp.setAdapter(adapter);
            vp.setRotationY(180);

            return v;

        }catch (Exception ex){
            //
        }

        return null;
    }


    // #####################################################################

    public static class CustomPagerAdapter extends PagerAdapter
    {
        private Context context;
        private ArrayList<View> views;

        public CustomPagerAdapter(Context context, ArrayList<View> views)
        {
            this.context = context;
            this.views = views;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position)
        {
            ViewGroup layout = (ViewGroup)views.get(position);
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view)
        {
            collection.removeView((View) view);
        }

        @Override
        public int getCount()
        {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return "";
        }
    }

}