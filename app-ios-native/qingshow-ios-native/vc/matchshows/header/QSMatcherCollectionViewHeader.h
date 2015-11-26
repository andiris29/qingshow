//
//  QSMatcherCollectionViewHeader.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSMatcherCollectionViewHeader;

@protocol QSMatcherCollectionViewHeaderDelegate <NSObject>

- (void)header:(QSMatcherCollectionViewHeader*)header didClickPeople:(NSDictionary*)peopleDict;

@end

@interface QSMatcherCollectionViewHeader : UICollectionViewCell

@property (weak, nonatomic) IBOutlet UILabel* dateLabel;
@property (weak, nonatomic) IBOutlet UIView* userHeadContaienr;
@property (weak, nonatomic) IBOutlet UILabel* numberLabel;
@property (weak, nonatomic) IBOutlet UIImageView* headImgView;
@property (weak, nonatomic) IBOutlet UILabel* topNumberLabel;
@property (weak, nonatomic) IBOutlet UILabel* topLabel;

@property (weak, nonatomic) NSObject<QSMatcherCollectionViewHeaderDelegate>* delegate;

+ (instancetype)generateView;
- (void)bindWithOwners:(NSArray*)owners ownerCount:(int)count index:(int)index;
- (void)updateDate:(NSDate*)date;
@end
