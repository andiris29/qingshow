package com.focosee.qingshow.widget;


import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2015/7/30.
 */
public abstract class AbsDialog extends Fragment implements View.OnClickListener {

    public enum Status {
        SHOW,
        DISMISS
    }

    public interface OnDialgStatusChangeListener {
        void onChanged(AbsDialog absDialog, Status status);
    }

    private View rootView;
    private ViewGroup group;
    private FrameLayout frameLayout;
    private boolean isDismissed = false;

    private OnDialgStatusChangeListener onDialgStatusChangeListener;


    public AbsDialog(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    private FragmentManager fragmentManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = getRootView(inflater, container);
        group = (ViewGroup) getActivity().getWindow().getDecorView();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        frameLayout = new FrameLayout(getActivity());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.setOnClickListener(this);

        LinearLayout mPanel = new LinearLayout(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(size.x / 5, 0, size.x / 5, 0);
        params.gravity = Gravity.CENTER;
        mPanel.setLayoutParams(params);
        mPanel.setOrientation(LinearLayout.VERTICAL);

        mPanel.addView(rootView);
        frameLayout.addView(mPanel);
        group.addView(frameLayout);

        return group;
    }

    public abstract View getRootView(LayoutInflater inflater, ViewGroup container);

    public void show(String tag) {
        if (fragmentManager.findFragmentByTag(tag) == null) {
            fragmentManager.beginTransaction().add(this, tag).addToBackStack(null).commit();
        } else {
            fragmentManager.beginTransaction().show(this).commit();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (onDialgStatusChangeListener != null)
            onDialgStatusChangeListener.onChanged(this, Status.SHOW);
    }

    public void dismiss() {
        if (onDialgStatusChangeListener != null)
            onDialgStatusChangeListener.onChanged(this, Status.DISMISS);
        if (isDismissed) {
            return;
        }
        isDismissed = true;
        getFragmentManager().popBackStack();
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onDestroyView() {
        group.removeView(frameLayout);
        frameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                group.removeView(frameLayout);
            }
        }, 300);
        super.onDestroyView();
    }


    @Override
    public void onClick(View v) {

    }

    public void setOnDialgStatusChangeListener(OnDialgStatusChangeListener onDialgStatusChangeListener) {
        this.onDialgStatusChangeListener = onDialgStatusChangeListener;
    }

}
