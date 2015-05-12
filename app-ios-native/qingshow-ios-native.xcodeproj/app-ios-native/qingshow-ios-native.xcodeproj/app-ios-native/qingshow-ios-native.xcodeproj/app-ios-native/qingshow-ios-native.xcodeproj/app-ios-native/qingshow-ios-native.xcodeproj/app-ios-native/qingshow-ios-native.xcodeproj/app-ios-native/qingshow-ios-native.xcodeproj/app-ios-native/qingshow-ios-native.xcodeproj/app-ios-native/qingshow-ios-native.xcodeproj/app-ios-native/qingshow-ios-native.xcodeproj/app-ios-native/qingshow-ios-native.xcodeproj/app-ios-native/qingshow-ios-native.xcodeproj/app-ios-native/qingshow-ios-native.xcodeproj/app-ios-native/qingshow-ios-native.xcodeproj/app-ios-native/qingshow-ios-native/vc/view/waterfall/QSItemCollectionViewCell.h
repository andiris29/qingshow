//
//  QSItemCollectionViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 2/1/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
@class UILabelStrikeThrough;
@interface QSItemCollectionViewCell : UICollectionViewCell

@property (strong, nonatomic) IBOutlet UILabelStrikeThrough* priceLabel;
@property (strong, nonatomic) IBOutlet UILabel* priceAfterDiscountLabel;
@property (strong, nonatomic) IBOutlet UIImageView* shadowImageView;
@property (strong, nonatomic) IBOutlet UIImageView* itemImageView;

#pragma mark - Static
+ (CGFloat)getHeight:(NSDictionary*)itemDict;
+ (CGSize)getSize:(NSDictionary*)itemDict;

#pragma mark - Binding
- (void)bindWithItem:(NSDictionary*)itemDict;

@end
