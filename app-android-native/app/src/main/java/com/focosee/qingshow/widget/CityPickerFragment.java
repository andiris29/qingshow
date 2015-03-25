package com.focosee.qingshow.widget;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/3/24.
 */
public class CityPickerFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_CANCEL_BUTTON_TITLE = "cancel_button_title";
    private static final String ARG_OTHER_BUTTON_TITLES = "other_button_titles";
    private static final String ARG_CANCELABLE_ONTOUCHOUTSIDE = "cancelable_ontouchoutside";
    private static final int CANCEL_BUTTON_ID = 100;
    private static final int BG_VIEW_ID = 10;
    private static final int TRANSLATE_DURATION = 200;
    private static final int ALPHA_DURATION = 300;

    private View mView;
    private LinearLayout mPanel;
    private ViewGroup mGroup;
    private View mBg;
    private boolean mDismissed = true;
    private CityPicker cityPicker;
    private Button confirm;
    private Button cancel;
    private String confirmButtonTitle = "确定";
    private CityPicker.OnSelectingListener onSelectingListener;

    public CityPickerFragment show(FragmentManager manager, String tag, Context context) {
        if (!mDismissed) {
            return null;
        }
        CityPickerFragment cityPickerFragment = (CityPickerFragment) Fragment.instantiate(
                context, CityPickerFragment.class.getName(), prepareArguments());
//        actionSheet.setActionSheetListener(mListener);
        mDismissed = false;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.addToBackStack(null);
        ft.commit();
        return cityPickerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View focusView = getActivity().getCurrentFocus();
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }

        View view = inflater.inflate(R.layout.dialog_citypicker, null);

        mGroup = (ViewGroup) getActivity().getWindow().getDecorView();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        cityPicker = (CityPicker)view.findViewById(R.id.dialog_citypicker);
        cityPicker.setOnSelectingListener(onSelectingListener);
        ViewGroup.LayoutParams params = cityPicker.getLayoutParams();
        params.width = size.x;
        params.height = size.y/3;
        cityPicker.setLayoutParams(params);

        mView = createView();

        mGroup.addView(mView);
        mPanel.addView(view);

        mBg.startAnimation(createAlphaInAnimation());
        mPanel.startAnimation(createTranslationInAnimation());

        return view;
    }

    public String getValue(){
        return cityPicker.getCity_string();
    }

    public CityPicker getCityPicker(){
        return cityPicker;
    }

    public void setConfirmOnClickListener(View.OnClickListener onClickListener){
        confirm.setOnClickListener(onClickListener);
    }

    public void setCancelOnClickListener(View.OnClickListener onClickListener){
        cancel.setOnClickListener(onClickListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        cityPicker.setVisibility(View.VISIBLE);
    }

    private View createView() {
        FrameLayout parent = new FrameLayout(getActivity());
        parent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mBg = new View(getActivity());
        mBg.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mBg.setBackgroundColor(Color.argb(136, 0, 0, 0));
        mBg.setId(BG_VIEW_ID);
        mBg.setOnClickListener(this);

        mPanel = new LinearLayout(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        mPanel.setLayoutParams(params);
        mPanel.setOrientation(LinearLayout.VERTICAL);

        parent.addView(mBg);
        parent.addView(mPanel);
        return parent;
    }

    @Override
    public void onDestroyView() {
        mPanel.startAnimation(createTranslationOutAnimation());
        mBg.startAnimation(createAlphaOutAnimation());
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGroup.removeView(mView);
            }
        }, ALPHA_DURATION);
//        if (mListener != null) {
//            mListener.onDismiss(this, isCancel);
//        }
        super.onDestroyView();
    }

    public void setOnSelectedListener(CityPicker.OnSelectingListener onSelectedListener){
        this.onSelectingListener = onSelectedListener;
        if(null != cityPicker) {
            cityPicker.setOnSelectingListener(this.onSelectingListener);
        }
    }

    public void setConfirmButtonTitle(String confirmButtonTitle){
        this.confirmButtonTitle = confirmButtonTitle;
    }
//
    public Bundle prepareArguments() {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_CANCEL_BUTTON_TITLE, confirmButtonTitle);
//        bundle.putStringArray(ARG_OTHER_BUTTON_TITLES, mOtherButtonTitles);
//        bundle.putBoolean(ARG_CANCELABLE_ONTOUCHOUTSIDE,
//                mCancelableOnTouchOutside);
        return bundle;
    }

    private Animation createTranslationInAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
                1, type, 0);
        an.setDuration(TRANSLATE_DURATION);
        return an;
    }

    private Animation createAlphaInAnimation() {
        AlphaAnimation an = new AlphaAnimation(0, 1);
        an.setDuration(ALPHA_DURATION);
        return an;
    }

    private Animation createTranslationOutAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
                0, type, 1);
        an.setDuration(TRANSLATE_DURATION);
        an.setFillAfter(true);
        return an;
    }

    private Animation createAlphaOutAnimation() {
        AlphaAnimation an = new AlphaAnimation(1, 0);
        an.setDuration(ALPHA_DURATION);
        an.setFillAfter(true);
        return an;
    }

    private boolean getCancelableOnTouchOutside() {
        return getArguments().getBoolean(ARG_CANCELABLE_ONTOUCHOUTSIDE);
    }

    public void dismiss() {
        if (mDismissed) {
            return;
        }
        mDismissed = true;
        getFragmentManager().popBackStack();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == BG_VIEW_ID && !getCancelableOnTouchOutside()) {
//            return;
//        }
        dismiss();
//        if (v.getId() != CANCEL_BUTTON_ID && v.getId() != BG_VIEW_ID) {
////            if (mListener != null) {
////                mListener.onOtherButtonClick(this, v.getId() - CANCEL_BUTTON_ID
////                        - 1);
////            }
////            isCancel = false;
//        }
    }



}
