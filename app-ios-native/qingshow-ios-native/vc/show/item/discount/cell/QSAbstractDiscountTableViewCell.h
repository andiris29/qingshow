//
//  QSAbstractDiscountTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#define DISCOUNT_CELL_CORNER_RADIUS 4.f
#define DISCOUNT_CELL_WIDTH (320.f)

@class QSAbstractDiscountTableViewCell;

@protocol QSDiscountTableViewCellDelegate <NSObject>

- (void)discountCellUpdateTotalPrice:(QSAbstractDiscountTableViewCell*)cell;
- (void)discountCellDetailBtnPressed:(QSAbstractDiscountTableViewCell*)cell;
- (void)discountCellRemixBtnPressed:(QSAbstractDiscountTableViewCell*)cell;
- (void)discountCellPreviousRemixBtnPressed:(QSAbstractDiscountTableViewCell*)cell;
- (void)discountCellNextRemixBtnPressed:(QSAbstractDiscountTableViewCell*)cell;
- (void)discountCell:(QSAbstractDiscountTableViewCell*)cell didSelectItem:(NSDictionary*)item;

- (BOOL)discountCellHasPreviousRemix:(QSAbstractDiscountTableViewCell*)cell;
- (BOOL)discountCellHasNextRemix:(QSAbstractDiscountTableViewCell*)cell;

@end

@interface QSAbstractDiscountTableViewCell : UITableViewCell

@property (weak, nonatomic) NSObject<QSDiscountTableViewCellDelegate>* delegate;

+ (instancetype)generateCell;

- (CGFloat)getHeight:(NSDictionary*)itemDict;
- (void)bindWithData:(NSDictionary*)itemDict;
- (BOOL)checkComplete;


@end
