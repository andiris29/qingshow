//
//  QSMatcherCollectionViewHeader.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSMatcherCollectionViewHeader : UICollectionViewCell

@property (weak, nonatomic) IBOutlet UILabel* dateLabel;
@property (weak, nonatomic) IBOutlet UIView* userHeadContaienr;
@property (weak, nonatomic) IBOutlet UILabel* numberLabel;
@property (weak, nonatomic) IBOutlet UIImageView* headImgView;


+ (instancetype)generateView;
- (void)bindWithOwners:(NSArray*)owners ownerCount:(int)count index:(int)index;

@end
