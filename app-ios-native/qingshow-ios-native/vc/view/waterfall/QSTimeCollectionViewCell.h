//
//  QSTimeCollectionViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/1/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSTimeCollectionViewCell : UICollectionViewCell
- (void)bindWithMetadata:(NSDictionary*)metaData;

@property (strong, nonatomic) IBOutlet UILabel* weekdayLabel;
@property (strong, nonatomic) IBOutlet UILabel* ymdLabel;
@property (strong, nonatomic) IBOutlet UILabel* timeLabel;
@end
