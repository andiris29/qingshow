//
//  QSTopShowOneDayCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#define kQSTopShowOneDayCellIdentifier @"QSTopShowOneDayCell"

@interface QSTopShowOneDayCell : UICollectionViewCell

@property (weak, nonatomic) IBOutlet UIImageView* imgView;
@property (weak, nonatomic) IBOutlet UILabel* numLikeLabel;
- (void)bindWithShow:(NSDictionary*)showDict;

@end
