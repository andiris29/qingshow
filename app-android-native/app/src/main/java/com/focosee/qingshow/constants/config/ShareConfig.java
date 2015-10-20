/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.focosee.qingshow.constants.config;

import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;

public class ShareConfig {
    //    public static final String SINA_APP_KEY = "2665932670";
    public static final String SINA_APP_KEY = "1213293589";
    //    public static final String APP_ID = "wxd930ea5d5a258f4f";
    public static final String APP_ID = "wx75cf44d922f47721";

    public static final String SINA_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    public static final String SHARE_URL = QSApplication.instance().getPreferences().getString("appWebRoot", "http://chingshow.com/app-web") + "?_id=";

    public static final int IMG = R.drawable.wx_share_trade;
}
