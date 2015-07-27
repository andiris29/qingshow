//
//  QSCategoryManager.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSCategoryManager : NSObject

@property (strong, nonatomic) NSArray* categories;

+ (QSCategoryManager*)getInstance;
- (NSDictionary*)findCategoryOfId:(NSString*)categoryId;
@end
