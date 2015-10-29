package com.dgsd.android.shifttracker.util;

import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AdUtils {

    public static AdRequest getAdRequest() {
        final AdRequest.Builder builder = new AdRequest.Builder();

        // TODO: Add keywords to add request
        // builder.addKeyword("games");

        return builder.build();
    }

    public static void loadAndShowAd(final AdView ad) {
        loadAndShowAd(ad, null);
    }

    public static void loadAndShowAd(final AdView ad, final OnAdShownListener listener) {
        if (ad == null) {
            return;
        }
        ad.loadAd(getAdRequest());
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                ViewUtils.beginAnimation((ViewGroup) ad.getParent());
                ViewUtils.hide(ad);
            }

            @Override
            public void onAdLoaded() {
                ViewUtils.beginAnimation((ViewGroup) ad.getParent());
                ViewUtils.show(ad);

                if (listener != null) {
                    ViewUtils.onPreDraw(ad, new Runnable() {
                        @Override
                        public void run() {
                            listener.onAdShown(ad);
                        }
                    });
                }
            }
        });
    }

    public interface OnAdShownListener {
        void onAdShown(AdView view);
    }
}
