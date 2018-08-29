package com.sprojects.atf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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


}