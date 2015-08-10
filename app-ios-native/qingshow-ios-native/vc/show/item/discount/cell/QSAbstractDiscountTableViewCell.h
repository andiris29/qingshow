//
//  QSAbstractDiscountTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#define DISCOUNT_CELL_CORNER_RADIUS 4.f;
#define DISCOUNT_CELL_WIDTH ([UIScreen mainScreen].bounds.size.width - 50)

@protocol QSDiscountTableViewCellDelegate <NSObject>

- (void)updateTotalPrice;

@end

@interface QSAbstractDiscountTableViewCell : UITableViewCell

@property (weak, nonatomic) NSObject<QSDiscountTableViewCellDelegate>* delegate;

+ (instancetype)generateCell;

- (CGFloat)getHeight:(NSDictionary*)itemDict;
- (void)bindWithData:(NSDictionary*)itemDict;
- (BOOL)checkComplete;


@end
