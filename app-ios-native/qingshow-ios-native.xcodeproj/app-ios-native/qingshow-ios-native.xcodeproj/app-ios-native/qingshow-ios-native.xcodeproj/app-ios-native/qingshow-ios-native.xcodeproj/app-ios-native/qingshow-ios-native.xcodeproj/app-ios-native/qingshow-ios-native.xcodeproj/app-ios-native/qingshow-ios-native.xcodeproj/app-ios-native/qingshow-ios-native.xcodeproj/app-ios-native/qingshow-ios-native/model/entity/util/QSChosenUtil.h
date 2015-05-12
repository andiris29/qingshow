//
//  QSChosenUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/6/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSInteger, QSChosenRefType) {
    QSChosenRefTypeShow,
    QSChosenRefTypeItem,
    QSChosenRefTypePreview,
    QSChosenRefTypeUnknown
};

@interface QSChosenUtil : NSObject

+ (QSChosenRefType)getChosenRefType:(NSDictionary*)chosen;
+ (NSDictionary*)getRef:(NSDictionary*)chosen;
+ (NSDate*)getChosenDate:(NSDictionary*)chosen;

@end
