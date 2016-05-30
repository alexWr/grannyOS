package com.grannyos.utils;


import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Class hide previous/next button and their title
 */

public class HideViews {

    private ViewPager   viewPager = null;
    private int         count = 0;

    public HideViews(ViewPager viewPager){

        this.viewPager = viewPager;
        this.count = viewPager.getAdapter().getCount();
    }

    public void visibility(View prev, View prevTitle, View next,  View nextTitle){
        if( this.viewPager != null ) {
            if (count == 1 || count == 0) {
                prev.setVisibility(View.INVISIBLE);
                prevTitle.setVisibility(View.INVISIBLE);
                next.setVisibility(View.INVISIBLE);
                nextTitle.setVisibility(View.INVISIBLE);
            } else {
                if (viewPager.getCurrentItem() == 0) {
                    prev.setVisibility(View.INVISIBLE);
                    prevTitle.setVisibility(View.INVISIBLE);
                    next.setVisibility(View.VISIBLE);
                    nextTitle.setVisibility(View.VISIBLE);
                } else {
                    if (viewPager.getCurrentItem() == (count - 1)) {
                        prev.setVisibility(View.VISIBLE);
                        prevTitle.setVisibility(View.VISIBLE);
                        next.setVisibility(View.INVISIBLE);
                        nextTitle.setVisibility(View.INVISIBLE);
                    } else {
                        prev.setVisibility(View.VISIBLE);
                        prevTitle.setVisibility(View.VISIBLE);
                        next.setVisibility(View.VISIBLE);
                        nextTitle.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }
}
