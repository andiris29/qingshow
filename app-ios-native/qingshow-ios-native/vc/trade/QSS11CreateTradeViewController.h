//
//  QSCreateTradeViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/15/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSS11CreateTradeViewController : UIViewController <UITableViewDataSource, UITableViewDelegate>

#pragma mark - Item Info Cells
@property (strong, nonatomic) IBOutlet UITableViewCell *itemInfoTitleCell;
@property (strong, nonatomic) IBOutlet UITableViewCell *itemInfoColorCell;
@property (strong, nonatomic) IBOutlet UITableViewCell *itemInfoSizeCell;
@property (strong, nonatomic) IBOutlet UITableViewCell *itemInfoQuantityCell;

#pragma mark - Receiver Info Cell
@property (strong, nonatomic) IBOutlet UITableViewCell *receiverInfoTitleCell;
@property (strong, nonatomic) IBOutlet UITableViewCell *receiverInfoNameCell;
@property (strong, nonatomic) IBOutlet UITableViewCell *receiverInfoPhoneCell;
@property (strong, nonatomic) IBOutlet UITableViewCell *receiverInfoLocationCell;
@property (strong, nonatomic) IBOutlet UITableViewCell *receiverInfoDetailLocationCell;

#pragma mark - Pay Info Cell
@property (strong, nonatomic) IBOutlet UITableViewCell *payInfoTitleCell;
@property (strong, nonatomic) IBOutlet UITableViewCell *payInfoWechatCell;
@property (strong, nonatomic) IBOutlet UITableViewCell *payInfoAllipayCell;
@property (strong, nonatomic) IBOutlet UITableViewCell *payInfoBandCell;


#pragma mark - Total Cell
@property (strong, nonatomic) IBOutlet UITableViewCell *totalCell;

@end
