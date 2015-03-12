//
//  QSOrderListHeaderView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSOrderListHeaderView : UIView

@property (weak, nonatomic) IBOutlet UIImageView* headerImageView;
@property (weak, nonatomic) IBOutlet UILabel* label1;
@property (weak, nonatomic) IBOutlet UILabel* label2;

+ (instancetype)makeView;

@end
