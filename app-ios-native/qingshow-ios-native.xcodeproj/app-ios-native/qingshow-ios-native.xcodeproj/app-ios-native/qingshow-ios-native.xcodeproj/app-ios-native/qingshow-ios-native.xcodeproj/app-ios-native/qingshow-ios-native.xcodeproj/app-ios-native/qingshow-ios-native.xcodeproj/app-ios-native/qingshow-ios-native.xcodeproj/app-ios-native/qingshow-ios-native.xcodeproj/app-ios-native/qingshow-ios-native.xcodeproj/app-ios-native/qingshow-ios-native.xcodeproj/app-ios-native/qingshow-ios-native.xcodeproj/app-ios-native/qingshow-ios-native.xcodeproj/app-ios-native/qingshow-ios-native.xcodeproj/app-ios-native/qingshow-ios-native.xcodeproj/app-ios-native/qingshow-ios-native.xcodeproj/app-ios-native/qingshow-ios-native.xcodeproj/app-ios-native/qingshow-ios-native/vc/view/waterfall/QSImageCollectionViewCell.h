//
//  QSImageCollectionViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/9/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#define kQSImageCollectionViewCellHeight 270.f

@interface QSImageCollectionViewCell : UICollectionViewCell

@property (weak, nonatomic) IBOutlet UILabel* label1;
@property (weak, nonatomic) IBOutlet UILabel* label2;
@property (weak, nonatomic) IBOutlet UILabel* discountLabel;
@property (weak, nonatomic) IBOutlet UIImageView* imageView;;

- (void)bindWithShow:(NSDictionary*)showDict;
- (void)bindWithItem:(NSDictionary*)itemDict;
@end
