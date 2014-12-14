//
//  ServerPath.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/2/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#ifndef qingshow_ios_native_ServerPath_h
#define qingshow_ios_native_ServerPath_h

#define HOST_ADDRESS @"192.168.1.100:30001/"
//#define HOST_ADDRESS @"localhost:30001/"
//#define HOST_ADDRESS @"chingshow.com:30001/"
#define HOST_NAME [NSString stringWithFormat:@"%@%@",HOST_ADDRESS, @"services"]
#define kImageUrlBase [NSString stringWithFormat:@"http://%@%@",HOST_ADDRESS, @"images"]
#define kVideoUrlBase [NSString stringWithFormat:@"http://%@%@",HOST_ADDRESS, @"videos"]

#endif
