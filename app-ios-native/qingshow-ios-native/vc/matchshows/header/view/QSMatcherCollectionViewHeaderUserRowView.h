//
//  QSMatcherCollectionViewHeaderUserRowView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/24.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
@class QSMatcherCollectionViewHeaderUserRowView;

@protocol  QSMatcherCollectionViewHeaderUserRowViewDelegate <NSObject>

- (void)userRowView:(QSMatcherCollectionViewHeaderUserRowView*)view didClickIndex:(NSUInteger)index;

@end

@interface QSMatcherCollectionViewHeaderUserRowView : UIView

@property (weak, nonatomic) NSObject<QSMatcherCollectionViewHeaderUserRowViewDelegate>* delegate;
@property (assign, nonatomic) BOOL alignCenter;
- (instancetype)init;
- (void)bindWithUsers:(NSArray*)users;


@end
