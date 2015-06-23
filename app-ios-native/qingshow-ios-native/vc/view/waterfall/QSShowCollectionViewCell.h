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

@optional

- (void)favorBtnPressed:(QSShowCollectionViewCell*)cell;
- (void)playBtnPressed:(QSShowCollectionViewCell*)cell;

@end



@interface QSShowCollectionViewCell : UICollectionViewCell

@property (strong, nonatomic) IBOutlet UIImageView *photoImageView;
@property (strong, nonatomic) IBOutlet UIImageView* backgroundImageView;
@property (strong, nonatomic) IBOutlet UIImageView* foregroundImageView;

@property (strong, nonatomic) IBOutlet UIImageView *headIconImageView;
@property (strong, nonatomic) IBOutlet UILabel *nameLabel;
@property (strong, nonatomic) IBOutlet UILabel *favorNumberLabel;
@property (strong, nonatomic) IBOutlet UIButton* favorButton;
@property (strong, nonatomic) IBOutlet UIView* modelTapView;



@property (weak, nonatomic) NSObject<QSShowCollectionViewCellDelegate>* delegate;

- (IBAction)favorBtnPressed:(id)sender;
//Show
- (void)bindData:(NSDictionary*)showData;

//static
+ (float)getHeightWithData:(NSDictionary*)showData;
+ (CGSize)getSizeWithData:(NSDictionary*)showData;

@end
