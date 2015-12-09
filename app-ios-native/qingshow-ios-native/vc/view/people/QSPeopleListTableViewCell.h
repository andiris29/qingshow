//
//  QSModelListTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSPeopleListTableViewCell;

@protocol QSPeopleListTableViewCellDelegate <NSObject>

- (void)favorBtnPressed:(QSPeopleListTableViewCell*)cell;

@end

@interface QSPeopleListTableViewCell : UITableViewCell

@property (strong, nonatomic) IBOutlet UILabel* nameLabel;
@property (strong, nonatomic) IBOutlet UILabel* detailLabel;
@property (strong, nonatomic) IBOutlet UILabel* showNumLabel;
@property (strong, nonatomic) IBOutlet UILabel* followerNumLabel;
//@property (strong, nonatomic) IBOutlet UIButton* followBtn;
@property (strong, nonatomic) IBOutlet UIImageView* headPhotoImageView;

@property (strong, nonatomic) IBOutlet UIImageView* leftIcon;
@property (strong, nonatomic) IBOutlet UIImageView* rightIcon;
@property (strong, nonatomic) IBOutlet UIImageView* rankImgView;
@property (weak, nonatomic) NSObject<QSPeopleListTableViewCellDelegate>* delegate;

//- (IBAction)followButtonPressed:(id)sender;
- (void)bindWithPeople:(NSDictionary*)peopleDict;

@end
