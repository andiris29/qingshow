//
//  QSFeedingCategory.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/22/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSFeedingCategory.h"

NSString* categoryToString(QSFeedingCategory type)
{
    NSString* retStr = nil;
    switch (type) {
        case FeedingCategoryShandian:
        {
            retStr = @"闪点推荐";
            break;
        }
        case FeedingCategoryBandan:
        {
            retStr =  @"美搭榜单";
            break;
        }
        case FeedingCategoryBaobao:
        {
            retStr = @"时尚包包";
            break;
        }
        case FeedingCategoryPeishi:
        {
            retStr =  @"百搭配饰";
            break;
        }
        case FeedingCategoryChaoxie:
        {
            retStr =  @"个性潮鞋";
            break;
        }
        case FeedingCategoryFengshan:
        {
            retStr =@"潮流时尚";
            break;
        }
    }
    return retStr;
}