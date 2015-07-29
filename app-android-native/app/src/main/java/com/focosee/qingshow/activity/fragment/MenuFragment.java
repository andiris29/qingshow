package com.focosee.qingshow.activity.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S01MatchShowsActivity;
import com.focosee.qingshow.activity.S20MatcherActivity;
import com.focosee.qingshow.activity.S21CategoryActivity;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.activity.U02SettingsActivity;
import com.focosee.qingshow.activity.U07RegisterActivity;
import com.focosee.qingshow.model.GoToWhereAfterLoginModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.util.BitMapUtil;

import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment implements View.OnClickListener{

    DrawerLayout drawer;
    @InjectView(R.id.navigation)
    LinearLayout navigation;
    @InjectView(R.id.blur)
    ImageView blur;
    @InjectView(R.id.context)
    LinearLayout right;
    @InjectView(R.id.s17_settting)
    ImageView settingBtn;

    private boolean isFirstFocus = true;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    protected void initDrawer() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(getActivity(), drawer,
                R.string.menu_open, R.string.menu_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                openMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                closeMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset == 0.0) blur.setVisibility(View.INVISIBLE);
            }
        };
        drawer.setDrawerListener(drawerToggle);
    }

    public void menuSwitch(){
        if(isMenuOpened()){
            closeMenu();
        }else{
            openMenu();
        }
    }

    public void closeMenu() {
        blur.setVisibility(View.INVISIBLE);
        drawer.closeDrawer(navigation);
    }

    public boolean isMenuOpened() {
        return drawer.isDrawerOpen(navigation);
    }

    public void openMenu() {

        if (isFirstFocus) {
            applyBlur();
            if (Build.VERSION.SDK_INT > 16)
                isFirstFocus = true;
        }
        blur.setVisibility(View.VISIBLE);

        navigation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        drawer.openDrawer(navigation);
    }

    private void blur(Bitmap bkg) {
        Handler mHandler = new Handler() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void handleMessage(Message msg) {
                blur.setBackground(new BitmapDrawable(getActivity().getResources(),
                        (Bitmap) msg.obj));
                right.destroyDrawingCache();
            }
        };
        String str;
        Bitmap overlay = BitMapUtil.convertToBlur(bkg, getActivity());
        Message msg = mHandler.obtainMessage(1, 1, 1, overlay);
        mHandler.sendMessage(msg);
    }

    private void applyBlur() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                right.setDrawingCacheEnabled(true);
                right.buildDrawingCache();
                Bitmap bitmap = right.getDrawingCache();

                blur(bitmap);
            }
        };
        thread.run();
    }

    @Override
    public void onClick(View v) {

        closeMenu();
        if(v.getId() == R.id.navigation_btn_match){
            startActivity(new Intent(getActivity(), S01MatchShowsActivity.class));
            return;
        }

        Class _class = null;
        switch (v.getId()) {
            case R.id.navigation_btn_good_match:
                _class = S20MatcherActivity.class;
                break;
            case R.id.u01_people:
                _class = U01UserActivity.class;
                break;
            case R.id.s17_settting:
                _class = U02SettingsActivity.class;
                break;
        }

        if(!QSModel.INSTANCE.loggedin()){
            Toast.makeText(getActivity(), R.string.need_login, Toast.LENGTH_SHORT).show();
            GoToWhereAfterLoginModel.INSTANCE.set_class(_class);
            startActivity(new Intent(getActivity(), U07RegisterActivity.class));
            return;
        }

        Intent intent = new Intent(getActivity(), _class);

        if(_class == U01UserActivity.class){
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", QSModel.INSTANCE.getUser());
            intent.putExtras(bundle);
        }

        startActivity(intent);
        if(!(getActivity() instanceof U02SettingsActivity)) getActivity().finish();
    }

    public boolean onBackPressed() {
        if(drawer.isScrollbarFadingEnabled()){
            menuSwitch();
            return true;
        }
        return false;
    }

}
