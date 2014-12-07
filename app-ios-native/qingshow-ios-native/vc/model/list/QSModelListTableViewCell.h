//
//  QSModelListTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSModelListTableViewCell;



@protocol QSModelListTableViewCellDelegate <NSObject>

- (void)favorBtnPressed:(QSModelListTableViewCell*)cell;

@end

@interface QSModelListTableViewCell : UITableViewCell

@property (strong, nonatomic) IBOutlet UILabel* nameLabel;
@property (strong, nonatomic) IBOutlet UILabel* detailLabel;
@property (strong, nonatomic) IBOutlet UILabel* showNumLabel;
@property (strong, nonatomic) IBOutlet UILabel* followerNumLabel;
@property (strong, nonatomic) IBOutlet UIButton* followBtn;
@property (strong, nonatomic) IBOutlet UIImageView* headPhotoImageView;
@property (weak, nonatomic) NSObject<QSModelListTableViewCellDelegate>* delegate;

- (IBAction)followButtonPressed:(id)sender;
- (void)bindWithPeople:(NSDictionary*)peopleDict;

@end
