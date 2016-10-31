package com.codepath.twitter.helpers;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.codepath.twitter.R;

/**
 * Created by sdevired on 10/28/16.
 * Utility to create snackback
 */
public class SnackBar {
    static Snackbar mSnackBar;

    /**
     * snackbar for displaying error conditions
     * @param message
     * @param a
     * @return
     */
    public static Snackbar getSnackBar(String message, Activity a){
        mSnackBar = Snackbar.make(a.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        TextView textView = (TextView) mSnackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.RED);
        mSnackBar.setActionTextColor(Color.CYAN);
        mSnackBar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSnackBar.dismiss();
            }
        });

        return mSnackBar;
    }

    /**
     * snackbar for displaying success messages
     * @param message
     * @param a
     * @return
     */
    public static Snackbar getSnackBarForSuccess(String message, Activity a){
        mSnackBar = Snackbar.make(a.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        TextView textView = (TextView) mSnackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(a.getResources().getColor(R.color.colorAccent));
        return mSnackBar;
    }

}
