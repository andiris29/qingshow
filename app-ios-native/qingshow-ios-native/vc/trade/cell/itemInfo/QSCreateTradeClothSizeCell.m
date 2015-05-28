//
//  QSCreateTableViewSizeCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/26.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSCreateTradeClothSizeCell.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSItemUtil.h"
@implementation QSCreateTradeClothSizeCell

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/
- (void)bindWithDict:(NSDictionary *)dict
{
    QSItemCategory category = [QSItemUtil getItemCategory:dict];
    if (category == QSItemCategoryShangyi || category == QSItemCategoryDress || category == QSItemCategoryNeida) {
        self.titleLabel.text = @"上身数值";
        self.bustCircleOrWaistlineTextField.placeholder = @"胸围：70cm";
        self.shoulderOrHiplineTextField.placeholder = @"肩宽：30cm";
        self.bodyPartImgView.image = [UIImage imageNamed:@"category_shangyi"] ;
    }
    else if(category == QSItemCategoryPant)
    {
        self.titleLabel.text = @"下身数值";
        self.bustCircleOrWaistlineTextField.placeholder = @"腰围：70cm";
        self.shoulderOrHiplineTextField.placeholder = @"臀围：30cm";
        self.bodyPartImgView.image = [UIImage imageNamed:@"category_pants"];
    }
}
- (CGFloat)getHeightWithDict:(NSDictionary *)dict
{
    return 120.f ;
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
