//
//  QSMatcherCollectionViewHeaderUserRowView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/24.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSMatcherCollectionViewHeaderUserRowView : UIView

@property (assign, nonatomic) BOOL kindomIconHidden;

- (instancetype)init;
- (void)bindWithUsers:(NSArray*)users;

@end
