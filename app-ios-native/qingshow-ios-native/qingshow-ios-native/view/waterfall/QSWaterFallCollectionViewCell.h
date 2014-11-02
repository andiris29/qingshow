//
//  QSWaterFallCollectionViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSWaterFallCollectionViewCell;

@protocol QSWaterFallCollectionViewCellDelegate

- (void)favorBtnPressed:(QSWaterFallCollectionViewCell*)cell;

@end

@interface QSWaterFallCollectionViewCell : UICollectionViewCell
@property (strong, nonatomic) IBOutlet UIImageView *photoImageView;
@property (strong, nonatomic) IBOutlet UIImageView *headIconImageView;
@property (strong, nonatomic) IBOutlet UILabel *nameLabel;
@property (strong, nonatomic) IBOutlet UILabel *statusLabel;
@property (strong, nonatomic) IBOutlet UILabel *contentLabel;
@property (strong, nonatomic) IBOutlet UILabel *favorNumberLabel;
@property (strong, nonatomic) IBOutlet UIButton* favorButton;

- (IBAction)favorBtnPressed:(id)sender;

@property (weak, nonatomic) NSObject<QSWaterFallCollectionViewCellDelegate>* delegate;

//Show
- (void)bindData:(NSDictionary*)showData;

//static
+ (float)getHeightWithData:(NSDictionary*)showData;

@end
