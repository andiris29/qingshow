//
//  QSItemImageTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 1/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSItemImageTableViewCell : UITableViewCell

- (void)bindWithItem:(NSDictionary*)itemDict;

@property (strong, nonatomic) IBOutlet UIView* infoContainerView;
@property (strong, nonatomic) IBOutlet UIView* imageContainerView;
@property (strong, nonatomic) IBOutlet UILabel* discountLabel;

+ (CGFloat)getHeightWithItem:(NSDictionary*)itemDict;
@end
