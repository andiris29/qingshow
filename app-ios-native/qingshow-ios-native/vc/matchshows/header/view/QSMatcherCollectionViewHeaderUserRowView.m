//
//  QSMatcherCollectionViewHeaderUserRowView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/24.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSMatcherCollectionViewHeaderUserRowView.h"
#import "QSMatcherCollectionViewHeaderUserView.h"
#import "QSPeopleUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "NSArray+QSExtension.h"

#define HEAD_NUMBER 8
#define SPACE_X 2.f

@interface QSMatcherCollectionViewHeaderUserRowView()
@property (strong, nonatomic) NSMutableArray* headerViews;
@end

@implementation QSMatcherCollectionViewHeaderUserRowView
- (void)setKindomIconHidden:(BOOL)kindomIconHidden {
    _kindomIconHidden = kindomIconHidden;
    for (QSMatcherCollectionViewHeaderUserView* view in self.headerViews) {
        view.iconImgView.hidden = kindomIconHidden;
    }
}
- (instancetype)init {
    self = [super init];
    if (self) {
        self.headerViews = [@[] mutableCopy];
        for (int i = 0; i < HEAD_NUMBER; i++) {
            UIView* v = [QSMatcherCollectionViewHeaderUserView generateView];
            v.userInteractionEnabled = YES;
            UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapHead:)];
            [v addGestureRecognizer:ges];
            [self.headerViews addObject:v];
            [self addSubview:v];
        }
    }
    return self;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    CGSize containerSize = self.bounds.size;
    CGFloat blockWidth = containerSize.width / HEAD_NUMBER;
    CGFloat headRadius = blockWidth - SPACE_X;
    CGFloat centerY = containerSize.height / 2;
    for (int i = 0; i < self.headerViews.count; i++) {
        UIView* headerView = self.headerViews[i];
        headerView.bounds = CGRectMake(0, 0, headRadius, headRadius);
        headerView.center = CGPointMake(i * blockWidth + headRadius / 2, centerY);
    }
    
}

- (void)bindWithUsers:(NSArray*)users {
    users = [users filteredArrayUsingBlock:^BOOL(id u) {
        return ![QSEntityUtil checkIsNil:u];
    }];
    for (int i = 0; i < self.headerViews.count; i++) {
        QSMatcherCollectionViewHeaderUserView* imgView = self.headerViews[i];
        if (i < users.count) {
            NSDictionary* u = users[i];
            imgView.hidden = NO;
            NSURL* url = [QSPeopleUtil getHeadIconUrl:u type:QSImageNameType50];
            [imgView.headerImgView setImageFromURL:url];
        } else {
            imgView.hidden = YES;
        }
    }
}

- (void)didTapHead:(UITapGestureRecognizer*)ges {
    UIView* v = ges.view;
    NSUInteger i = [self.headerViews indexOfObject:v];
    if (i != NSNotFound) {
        if ([self.delegate respondsToSelector:@selector(userRowView:didClickIndex:)]) {
            [self.delegate userRowView:self didClickIndex:i];
        }
    }
    
}

@end
