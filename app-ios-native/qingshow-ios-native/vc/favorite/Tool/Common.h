//
//  Common.h
//  TestForImageView
//
//  Created by 刘少毅 on 15/5/21.
//  Copyright (c) 2015年 刘少毅. All rights reserved.
//

#ifndef TestForImageView_Common_h
#define TestForImageView_Common_h

//判断iphone6
#define iPhone6 ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(750, 1334), [[UIScreen mainScreen] currentMode].size) : NO)

//判断iphone6+

#define iPhone6Plus ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(1242, 2208), [[UIScreen mainScreen] currentMode].size) : NO)

#endif
