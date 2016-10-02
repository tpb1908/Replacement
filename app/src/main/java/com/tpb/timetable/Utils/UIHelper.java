package com.tpb.timetable.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tpb.timetable.Home.Interfaces.Themable;
import com.tpb.timetable.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/**
 * Created by theo on 30/05/16.
 */
public class UIHelper {
    private static final String TAG = "UIHelper";
    private static boolean darkTheme;
    private  Context mContext;
    private static UIHelper instance;
    private static final ArrayList<Themable> mListeners = new ArrayList<>();
    private static SharedPreferences.Editor mEditor;

    private static int primary;
    private static int primaryDark;
    private static int primaryLight;
    private static int accent;
    private static int iconsText;
    private static int primaryText;
    private static int primaryTextLight;
    private static int secondaryText;
    private static int secondaryTextLight;
    private static int hintText;
    private static int hintTextLight;
    private static int background;
    private static int backgroundDark;
    private static int cardBackground;
    private static int cardBackgroundDark;
    private static int divider;
    private static int dividerDark;
    private static int iconBlack;
    private static int iconGrey;
    private static int iconWhite;

    private UIHelper(Context context) {
        mContext = context;
    }

    public static UIHelper getColorResources(Context context, Themable listener) {
        if(instance == null) {
            
            instance = new UIHelper(context);
            final SharedPreferences prefs = context.getSharedPreferences("colors", Context.MODE_PRIVATE);
            mEditor = prefs.edit();
            if(!prefs.contains("colorPrimary")) writeDefaultValues(mEditor);
            darkTheme = prefs.getBoolean("darkTheme", false);
            primary = prefs.getInt("colorPrimary", ContextCompat.getColor(instance.mContext, R.color.colorPrimary));
            primaryDark = prefs.getInt("colorPrimaryDark", ContextCompat.getColor(instance.mContext, R.color.colorPrimaryDark));
            primaryLight = prefs.getInt("colorPrimaryLight", ContextCompat.getColor(instance.mContext, R.color.colorPrimaryLight));
            accent = prefs.getInt("colorAccent", ContextCompat.getColor(instance.mContext, R.color.colorAccent));
            primaryText = prefs.getInt("colorPrimaryText", ContextCompat.getColor(instance.mContext, R.color.colorPrimaryText));
            primaryTextLight = prefs.getInt("colorPrimaryTextLight", ContextCompat.getColor(instance.mContext, R.color.colorPrimaryTextLight));
            secondaryText = prefs.getInt("colorSecondaryText", ContextCompat.getColor(instance.mContext, R.color.colorSecondaryText));
            secondaryTextLight = prefs.getInt("colorSecondaryTextLight", ContextCompat.getColor(instance.mContext, R.color.colorSecondaryTextLight));
            hintText = prefs.getInt("colorHintText", ContextCompat.getColor(instance.mContext, R.color.colorHintText));
            hintTextLight = prefs.getInt("colorHintTextLight", ContextCompat.getColor(instance.mContext, R.color.colorHintTextLight));
            iconsText = prefs.getInt("colorIconsText", ContextCompat.getColor(instance.mContext, R.color.colorIconsText));
            background = prefs.getInt("colorBackground", ContextCompat.getColor(instance.mContext, R.color.background));
            backgroundDark = prefs.getInt("colorBackgroundDark", ContextCompat.getColor(instance.mContext, R.color.backgroundDark));
            cardBackground = prefs.getInt("colorCard", ContextCompat.getColor(instance.mContext, R.color.card));
            cardBackgroundDark = prefs.getInt("colorCardDark", ContextCompat.getColor(instance.mContext, R.color.cardDark));
            divider = prefs.getInt("colorDivider", ContextCompat.getColor(instance.mContext, R.color.divider));
            dividerDark = prefs.getInt("colorDividerDark", ContextCompat.getColor(instance.mContext, R.color.dividerDark));
            iconBlack = prefs.getInt("colorIconBlack", ContextCompat.getColor(instance.mContext, R.color.black));
            iconGrey = prefs.getInt("colorIconGrey", ContextCompat.getColor(instance.mContext, R.color.icon_grey));
            iconWhite = prefs.getInt("colorIconWhite", ContextCompat.getColor(instance.mContext, R.color.white));
            mEditor.apply();
        }
        addListener(listener);

        return instance;
    }

    public static void addListener(Themable t) {
        if(t != null) mListeners.add(t);
    }

    private static void writeDefaultValues(SharedPreferences.Editor editor) {
        editor.putBoolean("darkTheme", false);
        editor.putInt("colorPrimary", ContextCompat.getColor(instance.mContext, R.color.colorPrimary));
        editor.putInt("colorPrimaryDark", ContextCompat.getColor(instance.mContext, R.color.colorPrimaryDark));
        editor.putInt("colorPrimaryLight", ContextCompat.getColor(instance.mContext, R.color.colorPrimaryLight));
        editor.putInt("colorAccent", ContextCompat.getColor(instance.mContext, R.color.colorAccent));
        editor.putInt("colorPrimaryText", ContextCompat.getColor(instance.mContext, R.color.colorPrimaryText));
        editor.putInt("colorPrimaryTextLight", ContextCompat.getColor(instance.mContext, R.color.colorPrimaryTextLight));
        editor.putInt("colorSecondaryText", ContextCompat.getColor(instance.mContext, R.color.colorSecondaryText));
        editor.putInt("colorSecondaryTextLight", ContextCompat.getColor(instance.mContext, R.color.colorSecondaryTextLight));
        editor.putInt("colorHintText", ContextCompat.getColor(instance.mContext, R.color.colorHintText));
        editor.putInt("colorHintTextLight", ContextCompat.getColor(instance.mContext, R.color.colorHintTextLight));
        editor.putInt("colorIconsText", ContextCompat.getColor(instance.mContext, R.color.colorIconsText));
        editor.putInt("colorBackground", ContextCompat.getColor(instance.mContext, R.color.background));
        editor.putInt("colorBackgroundDark", ContextCompat.getColor(instance.mContext, R.color.backgroundDark));
        editor.putInt("colorCard", ContextCompat.getColor(instance.mContext, R.color.card));
        editor.putInt("colorCardDark", ContextCompat.getColor(instance.mContext, R.color.cardDark));
        editor.putInt("colorDivider", ContextCompat.getColor(instance.mContext, R.color.divider));
        editor.putInt("colorDividerDark", ContextCompat.getColor(instance.mContext, R.color.dividerDark));
        editor.putInt("colorIconBlack", ContextCompat.getColor(instance.mContext, R.color.black));
        editor.putInt("colorIconGrey", ContextCompat.getColor(instance.mContext, R.color.icon_grey));
        editor.putInt("colorIconWhite", ContextCompat.getColor(instance.mContext, R.color.white));
        editor.commit();
    }

    /**
     * Begin setters
     */

    public static void setPrimary(int primary) {
        UIHelper.primary = primary;
    }

    public static void setPrimaryDark(int primaryDark) {
        UIHelper.primaryDark = primaryDark;
    }

    public static void setPrimaryLight(int primaryLight) {
        UIHelper.primaryLight = primaryLight;
    }

    public static void setAccent(int accent) {
        UIHelper.accent = accent;
    }

    public static void setDarkTheme(boolean isDark) {
        darkTheme = isDark;
        mEditor.putBoolean("darkTheme", isDark);
        mEditor.commit();
        themeViews();
    }

    /**
     * End setters
     */


    /**
     * Start variable gettesrs
     */


    public static boolean isDarkTheme() {
        return darkTheme;
    }

    public static int getPrimary() {
        return primary;
    }

    public static int getPrimaryDark() {
        return primaryDark;
    }

    public static int getPrimaryLight() {
        return primaryLight;
    }

    public static int getAccent() {
        return accent;
    }

    public static int getIconsText() {
        return iconsText;
    }

    public static int getPrimaryText() {
        return darkTheme ? primaryTextLight : primaryText;
    }

    public static int getSecondaryText() {
        return darkTheme ? secondaryTextLight : secondaryText;
    }

    public static int getCardBackground() {
        return darkTheme ? cardBackgroundDark : cardBackground;
    }

    public static int getDivider() {
        return darkTheme ? dividerDark : divider;
    }

    public static int getBackground() {
        return darkTheme ? backgroundDark : background;
    }

    public static int getHintText() {
        return darkTheme ? hintTextLight : hintText;
    }

    /**
     * End variable getters
     */

    public static void setTaskDescription(Activity activity) {
        final Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), android.R.mipmap.sym_def_app_icon);
        final ActivityManager.TaskDescription desc = new ActivityManager.TaskDescription(activity.getString(R.string.app_name), bm, primaryDark);
        activity.setTaskDescription(desc);
    }

    /**
     * Start view theming methods
     */


    public static void theme(ViewGroup group) {
        final Stack<ViewGroup> groupStack = new Stack<>();
        groupStack.push(group);
        Log.i(TAG, "theme: Root viewgroup is " + group);
        //We only set the background of the highest level to avoid overdraw
        final int bg = group instanceof CardView ? getCardBackground() : getBackground();
        group.setBackgroundColor(bg);
        View v;
        while(!groupStack.isEmpty()) {
            //We get the current ViewGroup and iterate through its children
            group = groupStack.pop();

            for(int i = 0; i < group.getChildCount(); i++) {
                v = group.getChildAt(i);
                //We don't want to do anything to any of these views, only their children
                if(v instanceof RelativeLayout ||
                        v instanceof LinearLayout ||
                        v instanceof RecyclerView ||
                        v instanceof CoordinatorLayout ||
                        v instanceof CardView ||
                        v instanceof FrameLayout) {
                    //Unless they are a TextInputLayout, which needs different theming
                    if(v instanceof TextInputLayout) themeTextInputLayout((TextInputLayout) v);
                    if(v instanceof CardView) themeCardView((CardView) v);
                    if(v instanceof TabLayout) {
                        themeTabLayout((TabLayout) v);
                    } else {
                        /*All of these view types contain other views
                        *However, we don't want to theme the TextViews
                        * in the TabLayout as if they are part of a
                        * content view
                         */
                        groupStack.push((ViewGroup) v);
                    }

                } else { //The view is singular, so we theme it
                    if(v instanceof TextInputEditText || v instanceof EditText) {
                        //We can theme TextInputEditText in the same way as EditText
                        themeEditText((EditText) v);
                    } else if(v instanceof AppCompatCheckBox) {
                        themeCheckBox((AppCompatCheckBox) v);
                    } else if(v instanceof TextView) {
                        themeTextView((TextView) v);
                    } else if(v instanceof ColoredSpace) {
                        ((ColoredSpace) v).setColor(getDivider());
                    } else if(v instanceof ImageButton) {
                        setDrawableColor(((ImageButton) v).getDrawable(), getBackground());
                    } else if(v instanceof ImageView) {
                        setDrawableColor(((ImageView) v).getDrawable(), getBackground());
                    } else if(v instanceof Spinner) {
                        themeSpinner((Spinner) v);
                    }
                }
            }
        }
    }

    private static void themeViews() {
        for(Themable t : mListeners) {
            theme(t.getViewGroup());
        }
    }

    private static void themeTextInputLayout(TextInputLayout t) {
        try {
            final Field fFocusedTextColor = TextInputLayout.class.getDeclaredField("mFocusedTextColor");
            final Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
            fFocusedTextColor.setAccessible(true);
            fDefaultTextColor.setAccessible(true);
            fFocusedTextColor.set(t, ColorStateList.valueOf(primary));
            fDefaultTextColor.set(t, ColorStateList.valueOf(getHintText()));
        } catch(Throwable ignored) {}
    }

    private static void themeCardView(CardView c) {
        c.setCardBackgroundColor(getCardBackground());
    }

    public static void themeFAB(FloatingActionButton f) {
        f.setBackgroundTintList(ColorStateList.valueOf(accent));
        setDrawableColor(f.getDrawable(), accent);
    }

    private static void themeTabLayout(TabLayout t) {
        t.setSelectedTabIndicatorColor(accent);
        t.setTabTextColors(getPrimaryLight(), getPrimaryText());
    }

    private static void themeEditText(EditText t) {
        t.setTextColor(getPrimaryText());
        //By setting the background color filter we set the color of the bottom bar
        t.getBackground().setColorFilter(accent, PorterDuff.Mode.SRC_ATOP);
        t.setHighlightColor(accent);
        //This is horrid, but Google don't give us a way to do this properly
        try {
            //We set the cursor to the accent color
            final Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            final int mCursorDrawableRes = fCursorDrawableRes.getInt(t);
            final Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            final Object editor = fEditor.get(t);
            final Class<?> clazz = editor.getClass();
            final Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);

            /*
            https://android.googlesource.com/platform/frameworks/base/+/37960c7/core/java/android/widget/Editor.java#159
            Editor has an array of two drawables for its cursors
             */
            final Drawable[] drawables = new Drawable[5];
            drawables[0] = ContextCompat.getDrawable(t.getContext(), mCursorDrawableRes);
            drawables[1] = ContextCompat.getDrawable(t.getContext(), mCursorDrawableRes);
            drawables[0].setColorFilter(accent, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(accent, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);

            //We set the selection handles to the accent color
            //TODO- Cache the drawables? Performance doesn't seem to be a problem
            final Field fHandleRes = TextView.class.getDeclaredField("mTextSelectHandleRes");
            final Field fHandleLeftRes = TextView.class.getDeclaredField("mTextSelectHandleLeftRes");
            final Field fHandleRightRes = TextView.class.getDeclaredField("mTextSelectHandleRightRes");
            fHandleRes.setAccessible(true);
            fHandleLeftRes.setAccessible(true);
            fHandleRightRes.setAccessible(true);
            final int centreHandleRes = fHandleRes.getInt(t);
            final int leftHandleRes = fHandleLeftRes.getInt(t);
            final int rightHandleRes = fHandleRightRes.getInt(t);
            final Field fSelectHandleCenter = editor.getClass().getDeclaredField("mSelectHandleCenter");
            final Field fSelectHandleLeft = editor.getClass().getDeclaredField("mSelectHandleLeft");
            final Field fSelectHandleRight = editor.getClass().getDeclaredField("mSelectHandleRight");
            fSelectHandleCenter.setAccessible(true);
            fSelectHandleLeft.setAccessible(true);
            fSelectHandleRight.setAccessible(true);
            drawables[2] = ContextCompat.getDrawable(t.getContext(), centreHandleRes);
            drawables[3] = ContextCompat.getDrawable(t.getContext(), leftHandleRes);
            drawables[4] = ContextCompat.getDrawable(t.getContext(), rightHandleRes);
            for(Drawable d : drawables) d.setColorFilter(accent, PorterDuff.Mode.SRC_IN);
            fSelectHandleCenter.set(editor, drawables[2]);
            fSelectHandleLeft.set(editor, drawables[3]);
            fSelectHandleRight.set(editor, drawables[4]);
        } catch(Throwable ignored) {}
    }

    private static void themeCheckBox(AppCompatCheckBox c) {
        c.setSupportButtonTintList(ColorStateList.valueOf(accent));
        c.setHighlightColor(accent);
        c.setTextColor(getPrimaryText());
        //We have to get a custom ripple as Google don't let us change its color
        c.setBackgroundDrawable(getAdaptiveRippleDrawable(backgroundDark, accent));
    }

    private static void themeTextView(TextView t) {
        /*
         *   We set the text color based on text size (Is it a title?),
         *   line count (Does the TextView contain the body?),
         *   and a special clause for detail text which changes
         */
        if(t.getTextSize() >= instance.mContext.getResources().getDimension(R.dimen.text_title_size)
                || t.getLineCount() > 1
                || t.getId() == R.id.text_homework_detail) {
            t.setTextColor(getPrimaryText());
        } else {
            t.setTextColor(getSecondaryText());
        }
        t.setHintTextColor(getPrimaryText());
        /*
         * This clause is for the FAB sheet. If we don't set its background, the dark
         * theme won't show text. Also, this should never cause an exception. Every TextView
         * has a parent, and if the parent of that parent is null, then instanceof will
         * return false.
         */
        if(t.getParent().getParent() instanceof CardView) t.setBackgroundColor(getCardBackground());
    }

    private static void themeSpinner(Spinner s) {
        final Drawable d = s.getBackground().getConstantState().newDrawable();
        setDrawableColor(d, getCardBackground());
        s.setBackground(d);
    }

    /**
     * End view theming methods
     */


    //http://stackoverflow.com/questions/27787870/how-to-use-rippledrawable-programmatically-in-code-not-xml-with-android-5-0-lo
    private static Drawable getAdaptiveRippleDrawable(int normalColor, int pressedColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new RippleDrawable(ColorStateList.valueOf(pressedColor),
                    null, getRippleMask(normalColor));
        } else {
            return getStateListDrawable(normalColor, pressedColor);
        }
    }
    private static Drawable getRippleMask(int color) {
        final float[] outerRadii = new float[8];
        // 3 is radius of final ripple,
        // instead of 3 you can give required final radius
        Arrays.fill(outerRadii, 3);
        final RoundRectShape r = new RoundRectShape(outerRadii, null, null);
        final ShapeDrawable shapeDrawable = new ShapeDrawable(r);
        shapeDrawable.getPaint().setColor(color);
        return shapeDrawable;
    }

    private static StateListDrawable getStateListDrawable(int normalColor, int pressedColor) {
        final StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed},
                new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_focused},
                new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_activated},
                new ColorDrawable(pressedColor));
        states.addState(new int[]{},
                new ColorDrawable(normalColor));
        return states;
    }

    //Adds arguments for SlidingActivity to start from the bounds of a view
    public static void setExpandLocation(View v, Intent i) {
        if(v != null) {
            final int[] location = new int[2];
            v.getLocationInWindow(location);
            i.putExtra("leftOffset", location[0]);
            i.putExtra("topOffset", location[1]);
            i.putExtra("viewWidth", v.getWidth());
            i.putExtra("viewHeight", v.getHeight());
            i.putExtra("hasOpenPosition", true);
        } else {
            i.putExtra("hasOpenPosition", false);
        }
    }

    //TODO - Fix this
    public static void setDrawableColor(Drawable drawable, int bgColor) {
        if(drawable != null) {
            if(bgColor == cardBackground || bgColor == background) {
                drawable.setColorFilter(new PorterDuffColorFilter(iconGrey, PorterDuff.Mode.SRC_IN));
            } else if(bgColor == cardBackgroundDark || bgColor == accent) {

                drawable.setColorFilter(new PorterDuffColorFilter(iconWhite, PorterDuff.Mode.SRC_IN));
            } else if(bgColor == primary) {

            } else {
                final boolean darkIcon = perceivedBrightness(bgColor) > 0.5;
                if(darkIcon) {
                    drawable.setColorFilter(new PorterDuffColorFilter(iconGrey, PorterDuff.Mode.SRC_IN));
                } else {
                    drawable.setColorFilter(new PorterDuffColorFilter(iconWhite, PorterDuff.Mode.SRC_IN));
                }
            }

        }
    }

    public static void setDrawableColor(int resId, int background) {
        setDrawableColor(ContextCompat.getColor(instance.mContext, resId), background);
    }

    public static int getContrastingTextColor(int background) {
        return perceivedBrightness(background) > 0.5 ? Color.BLACK : Color.WHITE;
    }

    private static double perceivedBrightness(int c) {
        return 1 - (Color.red(c)* Color.red(c) * .299 +
                        Color.green(c) * Color.green(c) * .587 +
                        Color.blue(c) * Color.blue(c) * .114)/255D;
    }

    /** From and for Klinker sliding activity
     * Adjust the alpha of a color.
     * @param color the color [0x00000000, 0xffffffff]
     * @param factor the factor for the alpha [0,1]
     * @return the adjusted color
     */
    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
}
