package com.focosee.qingshow.Fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.widget.FlowRadioGroup;
import com.focosee.qingshow.widget.ImageRadio;
import com.focosee.qingshow.widget.ImageRadioGroup;


/**
 * Created by Administrator on 2015/3/11.
 */
public class S11DetailsFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private Button addButton;
    private Button cutButton;
    private TextView numView;
    private FlowRadioGroup sizeGroup;
    private ImageRadioGroup itemGroup;
    private ImageView reference;

    private int num = 1;

    private String size[] = new String[]{"S", "M", "L", "XL"};
    private int item[] = new int[]{R.drawable.s11_item_01,R.drawable.s11_item_02};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_s11_details, container, false);
        initView();
        initSize(size);
        initItem(item);

        return rootView;
    }


    private void initView() {
        addButton = (Button) rootView.findViewById(R.id.S11_add_num);
        cutButton = (Button) rootView.findViewById(R.id.S11_cut_num);
        numView = (TextView) rootView.findViewById(R.id.S11_num);
        sizeGroup = (FlowRadioGroup) rootView.findViewById(R.id.s11_size_group);
        itemGroup = (ImageRadioGroup) rootView.findViewById(R.id.s11_item_group);
        reference = (ImageView) rootView.findViewById(R.id.s11_reference);

        addButton.setOnClickListener(this);
        cutButton.setOnClickListener(this);
        numView.setText(num + "");

        rootView.findViewById(R.id.s11_show_reference).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reference.getVisibility() == View.VISIBLE){
                    reference.setVisibility(View.GONE);
                }else{
                    reference.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void initSize(String size[]) {



        for (int i = 0; i < size.length; i++) {
            RadioGroup.LayoutParams itemParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            RadioButton sizeItem = new RadioButton(getActivity());
            sizeItem.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            sizeItem.setBackgroundResource(R.drawable.trade_size_bg);
            sizeItem.setTextColor(getResources().getColor(R.color.black));
            sizeItem.setGravity(Gravity.CENTER);
            sizeItem.setLayoutParams(itemParams);
            sizeItem.setText(size[i]);
            sizeGroup.addView(sizeItem);
        }

        sizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    ((RadioButton) group.getChildAt(i)).setTextColor(getResources().getColor(R.color.darker_gray));
                }
                ((RadioButton) group.findViewById(checkedId)).setTextColor(getResources().getColor(R.color.white));
            }
        });
    }

    private void initItem(int item[]) {


        for (int i = 0; i < item.length; i++) {
            RadioGroup.LayoutParams itemParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            ImageRadio colorItem = new ImageRadio(getActivity());
            colorItem.setBackgroundResource(item[i]);
            colorItem.setLayoutParams(itemParams);
            itemGroup.addView(colorItem);
        }

    }



    @Override
    public void onClick(View v) {
        if (v == addButton) {
            num++;
            numView.setText(num + "");
        }
        if (v == cutButton) {
            if (1 == num) {
                return;
            }
            num--;
            numView.setText(num + "");
        }
    }
}
