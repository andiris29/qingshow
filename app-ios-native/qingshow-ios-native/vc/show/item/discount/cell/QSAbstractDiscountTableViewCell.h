//
//  QSAbstractDiscountTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol QSDiscountTableViewCellDelegate <NSObject>



@end

@interface QSAbstractDiscountTableViewCell : UITableViewCell

@property (weak, nonatomic) NSObject<QSDiscountTableViewCellDelegate>* delegate;

+ (instancetype)generateCell;

- (CGFloat)getHeight:(NSDictionary*)itemDict;
- (void)bindWithData:(NSDictionary*)itemDict;


@end
