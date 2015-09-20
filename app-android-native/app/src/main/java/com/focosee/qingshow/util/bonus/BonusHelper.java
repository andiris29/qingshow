package com.focosee.qingshow.util.bonus;

import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.StringUtil;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2015/9/6.
 */
public class BonusHelper {

    private static final int NOT_WITHDRAW = 0;
    private static final int WITHDRAWING = 1;
    private static final int WITHDRAWED = 2;

    public static String getBonusesNotWithDraw(List<MongoPeople.Bonuses> bonuses) {
        return StringUtil.FormatPrice(String.valueOf(getBonusesWithFloat(bonuses)));
    }

    public static float getBonusesWithFloat(List<MongoPeople.Bonuses> bonuses) {
        float total = 0;
        for (MongoPeople.Bonuses bonus : bonuses) {
            if (bonus.status.intValue() == NOT_WITHDRAW) {
                total += bonus.money.floatValue();
            }
        }
        return total;
    }

    public static String getTotalBonuses(List<MongoPeople.Bonuses> bonuses) {
        float total = 0;
        if(null != bonuses){
            for (MongoPeople.Bonuses bonus : bonuses){
                total += bonus.money.floatValue();
            }
        }
        return StringUtil.FormatPrice(String.valueOf(total));
    }

    public static String getBonusesMoneySign(MongoPeople.Bonuses bonuses){
        if(null == bonuses)return "+ 0.00";
        String sign;
        if(bonuses.status.intValue() == NOT_WITHDRAW){
            sign = "+ ";
        }else{
            sign = "- ";
        }
        return sign + StringUtil.formatPriceWithoutSign(String.valueOf(bonuses.money));
    }

    public static void bonusSort(List<MongoPeople.Bonuses> bonusesList){
        Collections.sort(bonusesList, new Comparator<MongoPeople.Bonuses>() {
            @Override
            public int compare(MongoPeople.Bonuses lhs, MongoPeople.Bonuses rhs) {
                return rhs.create.compareTo(lhs.create);
            }
        });
    }
}