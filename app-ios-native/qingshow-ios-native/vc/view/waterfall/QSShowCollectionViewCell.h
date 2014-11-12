//
//  QSWaterFallCollectionViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSShowCollectionViewCell;

@protocol QSShowCollectionViewCellDelegate

- (void)favorBtnPressed:(QSShowCollectionViewCell*)cell;

@end

@interface QSShowCollectionViewCell : UICollectionViewCell
@property (strong, nonatomic) IBOutlet UIImageView *photoImageView;
@property (strong, nonatomic) IBOutlet UIImageView *headIconImageView;
@property (strong, nonatomic) IBOutlet UILabel *nameLabel;
@property (strong, nonatomic) IBOutlet UILabel *statusLabel;
@property (strong, nonatomic) IBOutlet UILabel *contentLabel;
@property (strong, nonatomic) IBOutlet UILabel *favorNumberLabel;
@property (strong, nonatomic) IBOutlet UIButton* favorButton;

- (IBAction)favorBtnPressed:(id)sender;

@property (weak, nonatomic) NSObject<QSShowCollectionViewCellDelegate>* delegate;

//Show
- (void)bindData:(NSDictionary*)showData;

//static
+ (float)getHeightWithData:(NSDictionary*)showData;

@end
