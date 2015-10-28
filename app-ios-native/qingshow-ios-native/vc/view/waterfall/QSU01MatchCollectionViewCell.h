//
//  QSU01MatchCollectionViewCell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/6/19.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSIImageLoadingCancelable.h"

@interface QSU01MatchCollectionViewCell : UICollectionViewCell <QSIImageLoadingCancelable>

@property (weak, nonatomic) IBOutlet UIImageView *matchImgView;
@property (weak, nonatomic) IBOutlet UILabel *likeNumLabel;


- (void)bindWithDic:(NSDictionary *)dict;
- (void)cancelImageLoading;
@end
