//
//  QSImageNameUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 2/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSInteger, QSImageNameType) {
    QSImageNameTypeS,
    QSImageNameTypeXS,
    QSImageNameTypeXXS,
    QSImageNameTypeXXXS,
    
};

@interface QSImageNameUtil : NSObject

+ (NSURL*)appendImageNameUrl:(NSURL*)imgUrl type:(QSImageNameType)type;
+ (NSArray*)appendImageNameUrls:(NSArray*)urls type:(QSImageNameType)type;
+ (NSString*)appendImageName:(NSString*)imgUrl type:(QSImageNameType)type;
+ (NSArray*)appendImageNames:(NSArray*)urls type:(QSImageNameType)type;

@end
