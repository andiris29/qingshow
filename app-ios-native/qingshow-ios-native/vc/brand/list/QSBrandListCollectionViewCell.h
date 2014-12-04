//
//  QSBrandListCollectionViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSBrandListCollectionViewCell : UICollectionViewCell

@property (strong, nonatomic) IBOutlet UIImageView* brandImageView;
@property (strong, nonatomic) IBOutlet UIImageView* infoImageView;
- (void)bindWithBrandDict:(NSDictionary*)brandDict;
@end
