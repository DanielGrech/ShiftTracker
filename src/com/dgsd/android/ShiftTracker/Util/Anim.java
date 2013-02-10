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

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.*;

import java.lang.ref.WeakReference;

/** 
 *
 * @author Daniel Grech
 */
public class Anim {

    private static LayoutAnimationController mFadeInAnimationController;
    private static LayoutAnimationController mCascadeAnimationController;
    private static LayoutAnimationController mSlideInAnimationController;
    private static LayoutAnimationController mDealAnimationController;
    private static Animation mSlideUpAnimation;
    private static Animation mSlideDownAnimation;
    private static Animation mSlideOutToRightAnimation;
    private static Animation mSlideInFromLeftAnimation;

    public static LayoutAnimationController getListViewDealAnimator() {
        if(mDealAnimationController != null) {
            return mDealAnimationController;
        }
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator());
        set.setDuration(300);

        set.addAnimation(new AlphaAnimation(0.0f, 1.0f));
        set.addAnimation(new RotateAnimation(20, 0));
        set.addAnimation(new TranslateAnimation(Animation.ABSOLUTE, -100,
                Animation.ABSOLUTE,  0,
                Animation.ABSOLUTE, 720,
                Animation.ABSOLUTE, 0));

        mDealAnimationController = new LayoutAnimationController(set, 0.5f);
        return mDealAnimationController;
    }

    public static LayoutAnimationController getListViewFadeInAnimator() {
        if(mFadeInAnimationController != null) {
            return mFadeInAnimationController;
        }

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        animation.setInterpolator(new DecelerateInterpolator());


        mFadeInAnimationController = new LayoutAnimationController(animation, 0.0f);
        return mFadeInAnimationController;
    }

	public static LayoutAnimationController getListViewCascadeAnimator() {
        if(mCascadeAnimationController != null) {
            return mCascadeAnimationController;
        }
		AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(200);
		animation.setInterpolator(new DecelerateInterpolator());
        set.addAnimation(animation);

        animation = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(200);
		animation.setInterpolator(new DecelerateInterpolator());
        set.addAnimation(animation);

        ScaleAnimation scaleAnim = new ScaleAnimation(2.0F, 1.0F, 2.0F, 1.0F,
                Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        scaleAnim.setDuration(200);
        set.addAnimation(scaleAnim);

        mCascadeAnimationController = new LayoutAnimationController(set, 0.5f);
        return mCascadeAnimationController;
	}

	public static LayoutAnimationController getListViewSlideInFromLeftAnimator() {
        if(mSlideInAnimationController != null) {
            return mSlideInAnimationController;
        }

		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(200);
		animation.setInterpolator(new DecelerateInterpolator());
		set.addAnimation(animation);

		animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f
		);
		animation.setInterpolator(new DecelerateInterpolator());
		animation.setDuration(200);
		set.addAnimation(animation);

        mSlideInAnimationController = new LayoutAnimationController(set, 0.4f);
		return mSlideInAnimationController;
	}

	public static Animation getSlideUpAnimation() {
        if(mSlideUpAnimation != null) {
            return mSlideUpAnimation;
        }

		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setDuration(300);
		animation.setInterpolator(new DecelerateInterpolator());
		set.addAnimation(animation);

		animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f, // x{from,to}
				Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, -1.0f // y{from,to}
		);
		animation.setInterpolator(new DecelerateInterpolator());
		animation.setDuration(200);
		set.addAnimation(animation);

        mSlideUpAnimation = set;
		return mSlideUpAnimation;
	}

	public static Animation getSlideDownAnimation() {
        if(mSlideDownAnimation != null) {
            return mSlideDownAnimation;
        }
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(200);
		animation.setInterpolator(new DecelerateInterpolator());
		set.addAnimation(animation);

		animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f
		);
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setDuration(200);
		set.addAnimation(animation);

        mSlideDownAnimation = set;
		return mSlideDownAnimation;
	}

    public static Animation getSlideOutToRightAnimator() {
        if(mSlideOutToRightAnimation != null) {
            return mSlideOutToRightAnimation;
        }
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(200);
        animation.setInterpolator(new DecelerateInterpolator());
        set.addAnimation(animation);


        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 1.0f, // x{from,to}
                Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f // y{from,to}
        );
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(200);
        set.addAnimation(animation);

        mSlideOutToRightAnimation = set;
        return mSlideOutToRightAnimation;
    }

    public static Animation getSlideInFromLeftAnimator() {
        if(mSlideInFromLeftAnimation != null) {
            return mSlideInFromLeftAnimation;
        }

        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(200);
        animation.setInterpolator(new DecelerateInterpolator());
        set.addAnimation(animation);


        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f, // x{from,to}
                Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f // y{from,to}
        );
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(200);
        set.addAnimation(animation);

        mSlideInFromLeftAnimation = set;
        return mSlideInFromLeftAnimation;
    }

    public static void show(View v, Animation anim) {
        show(v, anim, null);
    }

	public static void show(View v, Animation anim, Runnable onEnd) {
		if(v == null || v.getVisibility() == View.VISIBLE) {
			return;
		}

		anim.setAnimationListener(new ShowAnimationListener(v, onEnd));

		// start the animation
		v.startAnimation(anim);
	}

    public static void hide(View v, Animation anim) {
        hide(v, anim, null);
    }

	public static void hide(View v, Animation anim, Runnable onEnd) {
		if(v == null || v.getVisibility() == View.GONE) {
			return;
		}

		anim.setAnimationListener(new HideAnimationListener(v, onEnd));

		// start the animation
		v.startAnimation(anim);
	}

    public static void replace(final View toHide, final View toShow, final Animation inAnim, final Animation outAnim) {
        if(toHide == null || toShow == null) {
            return;
        }

        outAnim.setAnimationListener(new HideAnimationListener(toHide, null));
        inAnim.setAnimationListener(new ShowAnimationListener(toShow, null));

        AnimationSet set = new AnimationSet(false);
        set.addAnimation(outAnim);
        set.addAnimation(inAnim);

        toHide.startAnimation(set);
    }

	/**
	 * An animation that rotates the view on the Y axis between two specified angles.
	 * This animation also adds a translation on the Z axis (depth) to improve the effect.
	 */
	public static class Rotate3dAnimation extends Animation {
		public static final int DURATION = 450; //ms 
		public static final float DEPTH_Z = 310.0F;
		
	    private final float mFromDegrees;
	    private final float mToDegrees;
	    private final float mCenterX;
	    private final float mCenterY;
	    private final float mDepthZ;
	    private final boolean mReverse;
	    private Camera mCamera;

	    /**
	     * Creates a new 3D rotation on the Y axis. The rotation is defined by its
	     * start angle and its end angle. Both angles are in degrees. The rotation
	     * is performed around a center point on the 2D space, definied by a pair
	     * of X and Y coordinates, called centerX and centerY. When the animation
	     * starts, a translation on the Z axis (depth) is performed. The length
	     * of the translation can be specified, as well as whether the translation
	     * should be reversed in time.
	     *
	     * @param fromDegrees the start angle of the 3D rotation
	     * @param toDegrees the end angle of the 3D rotation
	     * @param centerX the X center of the 3D rotation
	     * @param centerY the Y center of the 3D rotation
	     * @param reverse true if the translation should be reversed, false otherwise
	     */
	    public Rotate3dAnimation(float fromDegrees, float toDegrees,
	            float centerX, float centerY, float depthZ, boolean reverse) {
	        mFromDegrees = fromDegrees;
	        mToDegrees = toDegrees;
	        mCenterX = centerX;
	        mCenterY = centerY;
	        mDepthZ = depthZ;
	        mReverse = reverse;
	    }

	    @Override
	    public void initialize(int width, int height, int parentWidth, int parentHeight) {
	        super.initialize(width, height, parentWidth, parentHeight);
	        mCamera = new Camera();
	    }

	    @Override
	    protected void applyTransformation(float interpolatedTime, Transformation t) {
	        final float fromDegrees = mFromDegrees;
	        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

	        final float centerX = mCenterX;
	        final float centerY = mCenterY;
	        final Camera camera = mCamera;

	        final Matrix matrix = t.getMatrix();

	        camera.save();
	        
	        if (mReverse) {
	            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
	        } else {
	            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
	        }
	        
	        camera.rotateY(degrees);
	        camera.getMatrix(matrix);
	        camera.restore();
	        
	        /*if(mFlipSecondView && interpolatedTime > 0.45F && interpolatedTime < 0.55F) {
	        	//If we are half way through the animation, check if we need 
	        	//to flip our 'back side'
	        	
				camera.rotateY(180);
			}*/
	        
	        matrix.preTranslate(-centerX, -centerY);
	        matrix.postTranslate(centerX, centerY);
	    }
	    
	    
	}

    private static class HideAnimationListener implements Animation.AnimationListener {
        private WeakReference<View> mViewRef;
        private Runnable mOnEnd;

        public HideAnimationListener(View v, Runnable r) {
            mViewRef = new WeakReference<View>(v);
            mOnEnd = r;
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            View v = mViewRef.get();
            if(v != null) {
                v.setVisibility(View.GONE);
            }

            if(mOnEnd != null) {
                mOnEnd.run();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    private static class ShowAnimationListener implements Animation.AnimationListener {
        private WeakReference<View> mViewRef;
        private Runnable mOnEnd;

        public ShowAnimationListener(View v, Runnable r) {
            mViewRef = new WeakReference<View>(v);
            mOnEnd = r;
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            View v = mViewRef.get();
            if(v != null) {
                v.setVisibility(View.VISIBLE);
            }

            if(mOnEnd != null) {
                mOnEnd.run();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
}
