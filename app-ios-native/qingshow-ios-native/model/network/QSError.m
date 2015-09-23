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
                          @"暂无更多信息",
                          @"暂无更多信息",
                          @"用户不存在", //1005
                          @"当前品牌不存在",
                          @"邮箱格式错误",
                          @"参数不足",
                          @"没有更多了",     //1009
                          @"邮箱已存在，请重新输入", //10
                          @"已经喜欢过show",
                          @"请先登陆",
                          @"已经关注该用户",
                          @"还未关注该用户",
                          @"已经关注该品牌",       //15
                          @"还未关注该品牌",
                          @"暂无更多信息",
                          @"请求不合法",
                          @"已经关注",
                          @"已经取消关注",        //20
                          @"当前密码不正确",
                          @"",
                          @"",
                          @"",
                          @"",//25
                          @"",
                          @"",
                          @"",
                          @"手机号已存在",
                          @"验证码错误",//30
                          @"已超过每日发送次数",
                          @""];
    NSString* desc = @"";

    if (self.code <= 1031 && self.code >= 1000) {
        desc = desArray[self.code - 1000];
    }
    if (!desc || !desc.length) {
        desc = desArray[0];
    }
    return desc;
}
@end
