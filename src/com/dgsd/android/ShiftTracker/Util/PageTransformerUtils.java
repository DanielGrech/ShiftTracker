/*
 * Copyright 2013 Daniel Grech
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
