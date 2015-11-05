//
//  QSU01MatchCollectionViewCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/6/19.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU01MatchCollectionViewCell.h"
#import "UIImageView+MKNetworkKitAdditions.h"
@implementation QSU01MatchCollectionViewCell

- (void)awakeFromNib {
    // Initialization code
}
- (void)bindWithDic:(NSDictionary *)dict
{

}
- (void)cancelImageLoading {
    [self.matchImgView cancelImageLoadingOperation];
}
@end
