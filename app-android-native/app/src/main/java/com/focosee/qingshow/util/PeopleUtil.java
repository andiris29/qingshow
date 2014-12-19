package com.focosee.qingshow.util;

public class PeopleUtil {

    public static boolean checkUserIdEqual(String leftPeopleId, String rightPeopleId) {
        if (null == leftPeopleId || null == rightPeopleId)
            return false;
        // TODO .equal?
        return (leftPeopleId == rightPeopleId) ? true : false;
    }
}
