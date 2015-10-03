//
//  QSUserLocationTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#define QSUserLocationTableViewCellIdentifier @"QSUserLocationTableViewCellIdentifier"
#define QSUserLocationTableViewCellHeight 163.f


@class QSUserLocationTableViewCell;

@protocol QSUserLocationTableViewCellDelegate <NSObject>

- (void)didClickEditButtonOfCell:(QSUserLocationTableViewCell*)cell;
- (void)didClickDeleteButtonOfCell:(QSUserLocationTableViewCell*)cell;

@end

@interface QSUserLocationTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel* nameLabel;
@property (weak, nonatomic) IBOutlet UILabel* phoneLabel;
@property (weak, nonatomic) IBOutlet UILabel* addressLabel;
@property (weak, nonatomic) IBOutlet UIButton* selectedIndicator;
@property (weak, nonatomic) IBOutlet UIButton *editBtn;
@property (weak, nonatomic) IBOutlet UIButton *deleteBtn;

@property (weak, nonatomic) NSObject<QSUserLocationTableViewCellDelegate>* delegate;

@property (assign, nonatomic) BOOL isSelectedReceiver;

- (IBAction)editBtnPressed:(id)sender;
- (IBAction)deleteBtnPressed:(id)sender;
- (IBAction)selectedIndicatorPressed:(id)sender;

- (void)bindWithDict:(NSDictionary*)dict;
+ (float)getHeightWithDict:(NSDictionary*)dict;
@end
