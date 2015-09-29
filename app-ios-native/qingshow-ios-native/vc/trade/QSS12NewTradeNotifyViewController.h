//
//  QSS11NewTradeNotifyViewController.h
//  qingshow-ios-native
//
//  Created by mhy on 15/8/7.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSS12TextCell.h"

@class QSS12NewTradeNotifyViewController;
@protocol QSS12NewTradeNotifyViewControllerDelegate <NSObject>

- (void)didClickClose:(QSS12NewTradeNotifyViewController*)vc;
- (void)didClickPay:(QSS12NewTradeNotifyViewController*)vc;
@end

@interface QSS12NewTradeNotifyViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, QSS12TextCellDelegate>
@property (weak, nonatomic) IBOutlet UIImageView *backgroundImgView;
@property (weak, nonatomic) NSObject<QSS12NewTradeNotifyViewControllerDelegate>* delelgate;
@property (strong, nonatomic) NSDictionary* tradeDict;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (strong, nonatomic) NSNumber *expectablePrice;

- (instancetype)initWithDict:(NSDictionary*)tradeDict;
@end
