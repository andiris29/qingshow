////1010,
//  QSError.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/9/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSError.h"

@implementation QSError
- (NSString*)toString
{
    NSArray* desArray = @[@"系统错误，请稍后再试",    //1000
                          @"邮箱或密码错误",
                          @"请重新登陆",
                          @"Show不存在",
                          @"Item不存在",
                          @"用户不存在", //1005
                          @"当前品牌不存在",
                          @"邮箱格式错误",
                          @"参数不足",
                          @"没有更多了",
                          @"邮箱已存在，请重新输入", //10
                          @"已经喜欢过show",
                          @"请先登陆",
                          @"已经关注该用户",
                          @"还未关注该用户",
                          @"已经关注该品牌",
                          @"还未关注该品牌",
                          @"PItem不存在"];
    if (self.code <= 1017 && self.code >= 1000) {
        return desArray[self.code - 1000];
    } else {
        return desArray[0];
    }
}
@end
