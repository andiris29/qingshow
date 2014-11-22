//
//  QSFeedingCategory.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/22/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
typedef NS_ENUM(NSInteger, QSFeedingCategory) {
    FeedingCategoryShandian = 1,
    FeedingCategoryBandan = 2,
    FeedingCategoryBaobao = 5,
    FeedingCategoryPeishi = 6,
    FeedingCategoryChaoxie = 7,
    FeedingCategoryFengshan = 8
};

NSString* categoryToString(QSFeedingCategory type);
