//
//  QSMatcherCollectionViewHeader.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSMatcherCollectionViewHeader.h"
#import "UINib+QSExtension.h"
@implementation QSMatcherCollectionViewHeader
+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QSMatcherCollectionViewHeader"];
}
- (void)bindWithOwners:(NSArray*)owners ownerCount:(int)count index:(int)index {
    NSLog(@"%d", index);
}
@end
