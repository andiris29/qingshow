//
//  QSMatcherItemScrollSelectionCollectionViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#define QSMatcherItemScrollSelectionCollectionViewCellIdentifier @"QSMatcherItemScrollSelectionCollectionViewCell"
@interface QSMatcherItemScrollSelectionCollectionViewCell : UICollectionViewCell

@property (weak, nonatomic) IBOutlet UIImageView* imgView;
@property (weak, nonatomic) IBOutlet UILabel* label;
@property (weak, nonatomic) IBOutlet UIImageView* checkmarkImgView;
@property (assign, nonatomic) BOOL hover;

- (void)bindWithDict:(NSDictionary*)dict;


@end

