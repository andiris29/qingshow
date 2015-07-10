//
//  QSMatcherItemImageView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSMatcherItemPageSelectionImageView : UIControl

@property (weak, nonatomic) IBOutlet UIImageView* imgView;
@property (weak, nonatomic) IBOutlet UILabel* priceLabel;

@property (assign, nonatomic) BOOL hovered;

+ (instancetype)generateView;

- (void)bindWithItem:(NSDictionary*)itemDict;

@end
