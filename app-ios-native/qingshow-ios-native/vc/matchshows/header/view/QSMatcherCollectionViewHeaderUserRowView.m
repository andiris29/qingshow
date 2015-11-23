//
//  QSMatcherCollectionViewHeaderUserRowView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/24.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSMatcherCollectionViewHeaderUserRowView.h"
#import "QSMatcherCollectionViewHeaderUserView.h"

#define HEAD_NUMBER 8
#define SPACE_X 6.f

@interface QSMatcherCollectionViewHeaderUserRowView()
@property (strong, nonatomic) NSMutableArray* headerViews;
@end

@implementation QSMatcherCollectionViewHeaderUserRowView
- (instancetype)init {
    self = [super init];
    if (self) {
        self.headerViews = [@[] mutableCopy];
        for (int i = 0; i < HEAD_NUMBER; i++) {
            UIView* v = [QSMatcherCollectionViewHeaderUserView generateView];
            
            [self.headerViews addObject:v];
        }
    }
    return self;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    CGSize size = self.bounds.size;
    CGFloat blockWidth = size.width / HEAD_NUMBER;
    CGFloat headRadius = blockWidth - SPACE_X;
#warning TODO
}
@end
