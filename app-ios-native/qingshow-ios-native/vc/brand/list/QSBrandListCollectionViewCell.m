//
//  QSBrandListCollectionViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSBrandListCollectionViewCell.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@implementation QSBrandListCollectionViewCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)bindWithBrandDict:(NSDictionary*)brandDict
{
#warning 测试数据
    [self.brandImageView setImageFromURL:[NSURL URLWithString:@"http://d.hiphotos.baidu.com/image/h%3D800%3Bcrop%3D0%2C0%2C1280%2C800/sign=b61e1813940a304e4d22adfae1f3c4f4/9358d109b3de9c829a5995986f81800a19d843ec.jpg"]];
}

@end
