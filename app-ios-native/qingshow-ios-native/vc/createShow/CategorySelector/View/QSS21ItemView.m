//
//  QSS21ItemView.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS21ItemView.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@implementation QSS21ItemView

- (void)setSubViewsValueWith:(NSDictionary *)selectedDic
{
    NSString *itemName = self.itemDic[@"name"];
    self.titleLabel.text = itemName;
    
    NSString *imgUrl = self.itemDic[@"icon"];
    NSRange range = [imgUrl rangeOfString:@".png"];
    NSString *rangeStr = [imgUrl substringToIndex:range.location];
    NSString *imgSelectedUrl = [NSString stringWithFormat:@"%@_grey.png",rangeStr];
    if (self.itemDic == selectedDic) {
        [self.imgView setImageFromURL:[NSURL URLWithString:imgSelectedUrl] placeHolderImage:[UIImage imageNamed:@"selectedHoderImg"]];
    }else {
        //缓存selectedimage
        UIImageView *cashView = [[UIImageView alloc] init];
        [cashView setImageFromURL:[NSURL URLWithString:imgSelectedUrl]];
        
        [self.imgView setImageFromURL:[NSURL URLWithString:imgUrl] placeHolderImage:[UIImage imageNamed:@"hoderImg"]];
    }
}
@end
