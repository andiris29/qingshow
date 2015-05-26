//
//  QSCreateTableViewSizeCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/26.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSCreateTableViewSizeCell.h"
#import "UIImageView+MKNetworkKitAdditions.h"
@implementation QSCreateTableViewSizeCell

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/
- (void)bindWithDict:(NSDictionary *)dict
{
    NSString *type = dict[@"type"];
    if (type) {
#warning expected TypeString
        if ([type isEqualToString:@""]) {
            self.bustCircleOrWaistlineTextField.placeholder = @"胸围：70cm";
            self.shoulderOrHiplineTextField.placeholder = @"肩宽：30cm";
        }
        else if([type isEqualToString:@""])
        {
            self.bustCircleOrWaistlineTextField.placeholder = @"腰围：70cm";
            self.shoulderOrHiplineTextField.placeholder = @"臀围：30cm";
        }
    }
#warning expected ImageUrl
    NSURL* imgUrl = dict[@"image"];
    if (imgUrl) {
        [self.bodyPartImgView setImageFromURL:imgUrl];
    }

}
- (CGFloat)getHeightWithDict:(NSDictionary *)dict
{
    return [dict[@"height"] floatValue] ;
}
- (CGFloat)getBustCircleOrWaistline
{
    return [self.bustCircleOrWaistlineTextField.text floatValue];
}
- (CGFloat)getShoulderOrHipline
{
    return [self.shoulderOrHiplineTextField.text floatValue];
}


@end
