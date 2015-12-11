package com.focosee.qingshow.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S01MatchShowsActivity;
import com.focosee.qingshow.activity.S20MatcherActivity;
import com.focosee.qingshow.activity.T01HighlightedTradeListActivity;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.activity.U02SettingsActivity;
import com.focosee.qingshow.activity.U19LoginGuideActivity;
import com.focosee.qingshow.activity.U09TradeListActivity;
import com.focosee.qingshow.activity.fragment.U02SelectExceptionFragment;
import com.focosee.qingshow.activity.fragment.U02SettingsFragment;
import com.focosee.qingshow.constants.config.QSPushAPI;
import com.focosee.qingshow.model.GoToWhereAfterLoginModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.util.user.UnreadHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/9/14.
 */
public class MenuView extends Fragment implements View.OnClickListener {

    private static final int SDK_INT = 18;

    @InjectView(R.id.navigation_btn_match)
    ImageButton navigationBtnMatch;
    @InjectView(R.id.navigation_btn_match_tv)
    TextView navigationBtnMatchTv;
    @InjectView(R.id.navigation_btn_good_match)
    ImageButton navigationBtnGoodMatch;
    @InjectView(R.id.navigation_btn_good_match_tv)
    TextView navigationBtnGoodMatchTv;
    @InjectView(R.id.navigation_btn_discount)
    ImageButton navigationBtnDiscount;
    @InjectView(R.id.navigation_btn_discount_tv)
    TextView navigationBtnDiscountTv;
    @InjectView(R.id.u01_people)
    ImageButton u01People;
    @InjectView(R.id.u01_people_tv)
    TextView u01PeopleTv;
    @InjectView(R.id.background)
    FrameLayout background;
    @InjectView(R.id.menu_blur)
    ImageView menuBlur;
    @InjectView(R.id.navigation)
    LinearLayout menuLayout;
    private ViewGroup mGroup;
    private boolean dismissed = true;
    private View mView;

    private long duration = 300;
    private View blurView;
    private Bitmap blurBitmap;

    public void show(FragmentManager manager, String tag, View blurView) {
        if (dismissed) {
            dismissed = false;
            this.blurView = blurView.getRootView();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.add(this, tag);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.menu, null);
        ButterKnife.inject(this, mView);
        mGroup = (ViewGroup) getActivity().getWindow().getDecorView();

        mGroup.addView(mView);

        setListener();
        if(Build.VERSION.SDK_INT >= SDK_INT && blurView != null) {
            blurView.setDrawingCacheEnabled(true);
            blurView.buildDrawingCache();
            blurBitmap = blurView.getDrawingCache();
            Thread thread = new Thread() {
                @Override
                public void run() {
                    blurBitmap = convertToBlur(blurBitmap, getActivity());
                    Message message = new Message();
                    message.obj = blurBitmap;
                    handler.sendMessage(message);
                }
            };
            thread.start();
        }

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in);

        animation.setDuration(duration);
        menuLayout.startAnimation(animation);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof Bitmap) {
                if(!dismissed)
                    menuBlur.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

    private void dismiss() {
        if (dismissed) return;

        dismissed = true;
        getFragmentManager().popBackStack();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commit();

    }

    private void setListener() {

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        navigationBtnMatch.setOnClickListener(this);
        navigationBtnGoodMatch.setOnClickListener(this);
        navigationBtnDiscount.setOnClickListener(this);
        u01People.setOnClickListener(this);
    }

    private void initBtnColor() {
        if (getActivity() instanceof S01MatchShowsActivity) {
            navigationBtnMatch.setImageResource(R.drawable.root_menu_icon_meida_gray);
            navigationBtnMatchTv.setTextColor(getResources().getColor(R.color.darker_gray));
        }
        if (getActivity() instanceof S20MatcherActivity) {
            navigationBtnGoodMatch.setImageResource(R.drawable.root_menu_match_gray);
            navigationBtnGoodMatchTv.setTextColor(getResources().getColor(R.color.darker_gray));
        }
        if (getActivity() instanceof U09TradeListActivity) {
            if (UnreadHelper.hasMyNotificationCommand(QSPushAPI.ITEM_EXPECTABLE_PRICEUPDATED)
                    || UnreadHelper.hasMyNotificationCommand(QSPushAPI.TRADE_INITIALIZED)
                    || UnreadHelper.hasMyNotificationCommand(QSPushAPI.TRADE_SHIPPED))
                navigationBtnDiscount.setImageResource(R.drawable.root_menu_discount_gray_dot);
            else
                navigationBtnDiscount.setImageResource(R.drawable.root_menu_discount_gray);
            navigationBtnDiscountTv.setTextColor(getResources().getColor(R.color.darker_gray));
        }
        if (getActivity() instanceof U01UserActivity) {
            if (UnreadHelper.hasMyNotificationCommand(QSPushAPI.NEW_RECOMMANDATIONS))
                u01People.setImageResource(R.drawable.root_menu_flash_gray_dot);
            else
                u01People.setImageResource(R.drawable.root_menu_flash_gray);
            u01PeopleTv.setTextColor(getResources().getColor(R.color.darker_gray));
        }
    }

    private void initUnread(){
        if (UnreadHelper.hasMyNotificationCommand(QSPushAPI.NEW_RECOMMANDATIONS)) {
            u01People.setImageResource(R.drawable.root_menu_flash_tip);
        }

        if (UnreadHelper.hasMyNotificationCommand(QSPushAPI.ITEM_EXPECTABLE_PRICEUPDATED)
                || UnreadHelper.hasMyNotificationCommand(QSPushAPI.TRADE_INITIALIZED)
                || UnreadHelper.hasMyNotificationCommand(QSPushAPI.TRADE_SHIPPED)) {
            navigationBtnDiscount.setImageResource(R.drawable.root_menu_discount_dot);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out);
        animation.setDuration(duration);
        menuLayout.startAnimation(animation);
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mView.setVisibility(View.GONE);
                blurView.destroyDrawingCache();
                if(null != blurBitmap)
                    blurBitmap.recycle();
                mGroup.removeView(mView);
            }
        }, duration);
        ButterKnife.reset(this);
    }

    public static Bitmap convertToBlur(Bitmap bmp, Context context) {
        final int radius = 20;
        if (Build.VERSION.SDK_INT >= SDK_INT) {
            Log.d(MenuView.class.getSimpleName(), "VERSION.SDK_INT " + Build.VERSION.SDK_INT);
            final RenderScript rs = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(rs, bmp, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius /* e.g. 3.f */);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bmp);
            return bmp;
        }
        return bmp;
    }

    @Override
    public void onClick(View v) {

        dismiss();

        if (v.getId() == R.id.navigation_btn_match) {
            if (getActivity() instanceof S01MatchShowsActivity) {
                return;
            }
            startActivity(new Intent(getActivity(), S01MatchShowsActivity.class));
            getActivity().finish();
            return;
        }
        Class _class = null;
        switch (v.getId()) {
            case R.id.navigation_btn_good_match:
                if (getActivity() instanceof S20MatcherActivity) return;
                _class = S20MatcherActivity.class;
                break;
            case R.id.navigation_btn_discount:
                if (getActivity() instanceof U09TradeListActivity) return;
                if(QSModel.INSTANCE.isGuest()){
                    startActivity(new Intent(getActivity(), U19LoginGuideActivity.class));
                    return;
                }
                _class = U09TradeListActivity.class;
                break;
            case R.id.u01_people:
                if (getActivity() instanceof U01UserActivity) return;
                _class = U01UserActivity.class;
                break;
        }

        if (!QSModel.INSTANCE.loggedin()) {
            GoToWhereAfterLoginModel.INSTANCE.set_class(_class);
            startActivity(new Intent(getActivity(), U19LoginGuideActivity.class));
            return;
        }

        if (null == _class) return;

        Intent intent = new Intent(getActivity(), _class);

        if (_class == U01UserActivity.class) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", QSModel.INSTANCE.getUser());
            intent.putExtras(bundle);
        }

        startActivity(intent);
        if (null != getFragmentManager().findFragmentByTag(U02SettingsFragment.class.getSimpleName()))
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag(U02SettingsFragment.class.getSimpleName()));
        if (null != getFragmentManager().findFragmentByTag(U02SelectExceptionFragment.class.getSimpleName()))
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag(U02SelectExceptionFragment.class.getSimpleName()));
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        initUnread();
        initBtnColor();
    }
}
