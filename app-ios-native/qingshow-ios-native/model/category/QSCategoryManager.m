//
//  QSCategoryManager.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCategoryManager.h"
#import "QSNetworkKit.h"
#import "QSCategoryUtil.h"
#import "QSEntityUtil.h"

@interface QSCategoryManager ()

@end

@implementation QSCategoryManager

#pragma mark - Static
+ (void)initialize {
    [self updateCategory:3];
}

+ (void)updateCategory:(int)retryCount {
    if (retryCount <= 0) {
        return;
    }
    [SHARE_NW_ENGINE matcherQueryCategoriesOnSucceed:^(NSArray *array, NSDictionary* modelCategory, NSDictionary *metadata) {
        [self getInstance].categories = array;
    } onError:^(NSError *error) {
        [self updateCategory:retryCount - 1];
    }];
}


+ (QSCategoryManager*)getInstance
{
    static QSCategoryManager* s_userManager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        s_userManager = [[QSCategoryManager alloc] init];
    });
    return s_userManager;
}

- (NSDictionary*)findCategoryOfId:(NSString*)categoryId {
    if (!categoryId) {
        return nil;
    }
    return [QSCategoryManager _findCategoryId:categoryId fromArray:self.categories];
}

+ (NSDictionary*)_findCategoryId:(NSString*)categoryId fromArray:(NSArray*)array; {
    for (NSDictionary* dict in array) {
        if ([[QSEntityUtil getIdOrEmptyStr:dict] isEqualToString:categoryId]) {
            return dict;
        }
        
        NSArray* children = [QSCategoryUtil getChildren:dict];
        if (children) {
            NSDictionary* d = [self _findCategoryId:categoryId fromArray:children];
            if (d) {
                return d;
            }
        }
    }
    return nil;
}
@end
