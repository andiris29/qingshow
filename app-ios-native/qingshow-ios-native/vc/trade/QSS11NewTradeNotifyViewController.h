//
//  QSS11NewTradeNotifyViewController.h
//  qingshow-ios-native
//
//  Created by mhy on 15/8/7.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSS11NewTradeNotifyViewController;
@protocol QSS11NewTradeNotifyViewControllerDelegate <NSObject>

- (void)didClickClose:(QSS11NewTradeNotifyViewController*)vc;
- (void)didClickPay:(QSS11NewTradeNotifyViewController*)vc;
@end

@interface QSS11NewTradeNotifyViewController : UIViewController <UITableViewDataSource, UITableViewDelegate>
@property (weak, nonatomic) IBOutlet UIImageView *backgroundImgView;
@property (weak, nonatomic) NSObject<QSS11NewTradeNotifyViewControllerDelegate>* delelgate;
@property (strong, nonatomic) NSDictionary* tradeDict;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (weak, nonatomic) IBOutlet UIButton *payBtn;
@property (strong, nonatomic) NSNumber *actualPrice;

- (instancetype)initWithDict:(NSDictionary*)tradeDict actualPrice:(NSNumber*)actualPrice;
@end
