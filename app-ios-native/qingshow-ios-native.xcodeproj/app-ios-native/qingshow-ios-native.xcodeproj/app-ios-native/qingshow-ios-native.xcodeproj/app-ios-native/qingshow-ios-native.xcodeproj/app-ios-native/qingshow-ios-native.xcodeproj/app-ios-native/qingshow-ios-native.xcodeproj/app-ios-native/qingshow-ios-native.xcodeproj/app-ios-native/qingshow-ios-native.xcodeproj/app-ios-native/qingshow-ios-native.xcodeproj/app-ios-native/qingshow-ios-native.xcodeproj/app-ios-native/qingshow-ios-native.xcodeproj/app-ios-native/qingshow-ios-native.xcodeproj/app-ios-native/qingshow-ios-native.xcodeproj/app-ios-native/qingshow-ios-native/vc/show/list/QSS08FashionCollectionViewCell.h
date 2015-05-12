//
//  QSS08FashionCollectionViewCell.h
//  qingshow-ios-native
//
//  Created by ching show on 15/4/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSFashionCollectionViewProvider.h"
#define FASHION_COLLECTION_VIEW_INDENTIFIER @"QSS08FashionCollectionViewCell"
@interface QSS08FashionCollectionViewCell : UICollectionViewCell
@property (weak, nonatomic) IBOutlet UIImageView *backdropImage;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *arrowsImage;

@end
