//
//  QSUserLocationTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#define QSUserLocationTableViewCellIdentifier @"QSUserLocationTableViewCellIdentifier"
#define QSUserLocationTableViewCellHeight 158.f 
@interface QSUserLocationTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel* nameLabel;
@property (weak, nonatomic) IBOutlet UILabel* phoneLabel;
@property (weak, nonatomic) IBOutlet UILabel* addressLabel;
@property (weak, nonatomic) IBOutlet UIButton* selectedIndicator;

- (IBAction)editBtnPressed:(id)sender;
- (IBAction)deleteBtnPressed:(id)sender;
- (IBAction)selectedIndicatorPressed:(id)sender;

- (void)bindWithDict:(NSDictionary*)dict;
@end
