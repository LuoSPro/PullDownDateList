package com.example.popwindowdemo;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

public class AnimationUtil {

    public final static int ANIMATION_IN_TIME = 500;
    public final static int ANIMATION_OUT_TIME = 500;

    public static Animation createInAnimation(Context context, int fromYDelta) {
        AnimationSet set = new AnimationSet(context, null);
        //在动画链中，假定你有一个移动的动画紧跟一个淡出的动画，如果你不把移动的动画的setFillAfter置为true，
        // 那么移动动画结束后，View会回到原来的位置淡出，如果setFillAfter置为true， 就会在移动动画结束的位置淡出
        set.setFillAfter(false);
        TranslateAnimation animation = getTranslateAnimation(0,0,fromYDelta, 0,ANIMATION_IN_TIME);
        AlphaAnimation alphaAnimation = getAlphaAnimation(0, 1, ANIMATION_IN_TIME);
        //两个动画的结合，造成从上往下缓慢展开一个View
        set.addAnimation(animation);
        set.addAnimation(alphaAnimation);
        return set;
    }

    private static AlphaAnimation getAlphaAnimation(int fromAlpha, int toAlpha, int animationTime) {
        //从透明到完全显示
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setDuration(animationTime);
        return alphaAnimation;
    }

    private static TranslateAnimation getTranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta,int animationTime) {
        //从view的y轴的最下面到最上面
        TranslateAnimation animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        //设置动画时长
        animation.setDuration(animationTime);
        return animation;
    }

    public static Animation createOutAnimation(Context context, int toYDelta) {
        AnimationSet set = new AnimationSet(context, null);
        set.setFillAfter(false);
        TranslateAnimation animation = getTranslateAnimation(0, 0, 0, toYDelta,ANIMATION_OUT_TIME);
        set.addAnimation(animation);
        AlphaAnimation alphaAnimation = getAlphaAnimation(1, 0, ANIMATION_OUT_TIME);
        set.addAnimation(alphaAnimation);
        return set;
    }

}
