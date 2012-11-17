package com.dgsd.android.ShiftTracker.Util;

import android.support.v4.view.ViewPager;
import android.view.View;

public class PageTransformerUtils {
    private static ViewPager.PageTransformer mInnerCubeTransformer;
    private static ViewPager.PageTransformer mOuterCubeTransformer;
    private static ViewPager.PageTransformer mTwistTransformer;
    private static ViewPager.PageTransformer mCompressTransformer;

    public static ViewPager.PageTransformer getInnerCubeTransformer() {
        if (mInnerCubeTransformer == null) {
            mInnerCubeTransformer = new ViewPager.PageTransformer() {
                @Override
                public void transformPage(View view, float position) {
                    if(!Api.isMin(Api.HONEYCOMB))
                        return;

                    final float distFromZero = Math.abs(position);
                    view.setAlpha(1.0f - distFromZero);
                    view.setRotationY(-45 * position);
                }
            };
        }

        return mInnerCubeTransformer;
    }

    public static ViewPager.PageTransformer getOuterCubeTransformer() {
        if (mOuterCubeTransformer == null) {
            mOuterCubeTransformer = new ViewPager.PageTransformer() {
                @Override
                public void transformPage(View view, float position) {
                    if(!Api.isMin(Api.HONEYCOMB))
                        return;

                    final float distFromZero = Math.abs(position);
                    view.setAlpha(1.0f - distFromZero);
                    view.setRotationY(45 * position);
                }
            };
        }

        return mOuterCubeTransformer;
    }

    public static ViewPager.PageTransformer getTwistTransformer() {
        if (mTwistTransformer == null) {
            mTwistTransformer = new ViewPager.PageTransformer() {
                @Override
                public void transformPage(View view, float position) {
                    if(!Api.isMin(Api.HONEYCOMB))
                        return;

                    final float distFromZero = Math.abs(position);
                    view.setAlpha(1.0f - distFromZero);
                    view.setPivotX(0);
                    view.setPivotY(0);
                    view.setRotation(90 * position);
                }
            };
        }

        return mTwistTransformer;
    }

    public static ViewPager.PageTransformer getCompressTransformer() {
        if (mCompressTransformer == null) {
            mCompressTransformer = new ViewPager.PageTransformer() {
                @Override
                public void transformPage(View view, float position) {
                    if(!Api.isMin(Api.HONEYCOMB))
                        return;

                    final float distFromZero = Math.abs(position);
                    view.setAlpha(1.0f - distFromZero);
                    view.setScaleX(1.0f - distFromZero);
                    view.setScaleY(1.0f - distFromZero);
                }
            };
        }

        return mCompressTransformer;
    }


}
