//
//  QSDiscountTaobaoInfoCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSAbstractDiscountTableViewCell.h"

@protocol QSDiscountTaobaoInfoCellDelegate <NSObject>

- (void)disCountBtnPressed:(NSArray *)btnArray btnIndex:(NSInteger)infoIndex;

@end

@interface QSDiscountTaobaoInfoCell : QSAbstractDiscountTableViewCell

@property (assign, nonatomic) int infoIndex;
@property (strong, nonatomic) NSArray* btnArray;

@property (weak, nonatomic)NSObject <QSDiscountTaobaoInfoCellDelegate>* delegate;

@property (weak, nonatomic) IBOutlet UILabel* titleLabel;
@property (weak, nonatomic) IBOutlet UIView* lineView;
- (NSString*)getResult;

@end
