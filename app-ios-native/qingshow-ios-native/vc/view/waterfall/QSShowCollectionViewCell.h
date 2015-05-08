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

typedef NS_ENUM(NSUInteger, QSShowCollectionViewCellType)
{
    QSShowCollectionViewCellTypeNormal,
    QSShowCollectionViewCellTypeTopic
};


@interface QSShowCollectionViewCell : UICollectionViewCell

@property (strong, nonatomic) IBOutlet UIImageView *photoImageView;
@property (strong, nonatomic) IBOutlet UIImageView *headIconImageView;
@property (strong, nonatomic) IBOutlet UILabel *nameLabel;
@property (strong, nonatomic) IBOutlet UILabel *statusLabel;
@property (strong, nonatomic) IBOutlet UILabel *favorNumberLabel;
@property (strong, nonatomic) IBOutlet UIButton* favorButton;
@property (strong, nonatomic) IBOutlet UIImageView *shadowImageView;
@property (strong, nonatomic) IBOutlet UIView* modelTapView;
@property (strong, nonatomic) IBOutlet UIButton* playBtn;
- (IBAction)favorBtnPressed:(id)sender;

@property (assign, nonatomic) QSShowCollectionViewCellType type;
@property (weak, nonatomic) NSObject<QSShowCollectionViewCellDelegate>* delegate;

//Show
- (void)bindData:(NSDictionary*)showData;

//static
+ (float)getHeightWithData:(NSDictionary*)showData;
+ (CGSize)getSizeWithData:(NSDictionary*)showData;
//IBAction
- (void)peopleTap:(id)sender;
- (IBAction)playBtnPressed:(id)sender;
@end
