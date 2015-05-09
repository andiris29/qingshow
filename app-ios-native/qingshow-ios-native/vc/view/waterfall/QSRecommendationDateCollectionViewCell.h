//
//  QSRecommendationDateCollectionViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/9/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSRecommendationDateCellModel.h"

@interface QSRecommendationDateCollectionViewCell : UICollectionViewCell

@property (weak, nonatomic) IBOutlet UILabel* yearLabel;
@property (weak, nonatomic) IBOutlet UILabel* monthLabel;
@property (weak, nonatomic) IBOutlet UILabel* dayLabel;
@property (weak, nonatomic) IBOutlet UILabel* weekdayLabel;
@property (weak, nonatomic) IBOutlet UILabel* descLabel;

- (void)bindWithDate:(QSRecommendationDateCellModel*)model;

@end
