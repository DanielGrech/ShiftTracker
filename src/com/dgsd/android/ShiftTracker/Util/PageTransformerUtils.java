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
                    view.animate().alpha(1.0f - distFromZero).rotationY(-45 * position).setDuration(0).start();
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
                    view.animate().alpha(1.0f - distFromZero).rotationY(45 * position).setDuration(0).start();
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

                    view.setPivotX(0);
                    view.setPivotY(0);

                    final float distFromZero = Math.abs(position);
                    view.animate().alpha(1.0f - distFromZero)
                                  .rotation(90 * position)
                                  .setDuration(0)
                                  .start();
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
                    view.animate().alpha(1.0f - distFromZero)
                                  .scaleX(1.0f - distFromZero)
                                  .scaleY(1.0f - distFromZero)
                                  .setDuration(0)
                                  .start();
                }
            };
        }

        return mCompressTransformer;
    }


}
