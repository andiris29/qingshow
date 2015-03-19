//
//  QSCreateTradeItemInfoSizeCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeItemInfoSizeCell.h"
#import "QSItemUtil.h"
#import "QSTaobaoInfoUtil.h"
#import "QSTradeSelectButton.h"

#define BASE_X 85.f
#define MARGIN_X 10.f
#define BASE_Y 15.f
#define BTN_HEIGHT 27.f
#define DELTA_Y 30.f

@interface QSCreateTradeItemInfoSizeCell ()

@end

@implementation QSCreateTradeItemInfoSizeCell
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (void)bindWithDict:(NSDictionary*)dict
{
    if (self.btnArray) {
        for (QSTradeSelectButton* btn in self.btnArray) {
            [btn removeFromSuperview];
        }
    }
    
    NSDictionary* taobaoInfo = [QSItemUtil getTaobaoInfo:dict];
    self.skusArray = [QSTaobaoInfoUtil getSizeSkus:taobaoInfo];
    self.btnArray = [@[] mutableCopy];
    float screenWidth = [UIScreen mainScreen].bounds.size.width;
    
    __block float currentOriginX = BASE_X;
    __block float currentOriginY = BASE_Y;

    [self.skusArray enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        NSString* sizeProp = (NSString*)obj;
        NSString* sizeName = [QSTaobaoInfoUtil getNameOfProperty:sizeProp taobaoInfo:taobaoInfo];
        
        QSTradeSelectButton* btn = [[QSTradeSelectButton alloc] init];
        [self.btnArray addObject:btn];
        [btn addTarget:self action:@selector(btnPressed:) forControlEvents:UIControlEventTouchUpInside];
        btn.text = sizeName;
        
        if (currentOriginX + btn.frame.size.width + MARGIN_X > screenWidth) {
            currentOriginX = BASE_X;
            currentOriginY = currentOriginY + DELTA_Y;
        }
        
        CGRect rect = btn.frame;
        rect.origin = CGPointMake(currentOriginX, currentOriginY);
        btn.frame = rect;
        

        [self.contentView addSubview:btn];
        currentOriginX += btn.frame.size.width + MARGIN_X;

    }];
}



- (CGFloat)getHeightWithDict:(NSDictionary*)dict
{
    NSDictionary* taobaoInfo = [QSItemUtil getTaobaoInfo:dict];
    NSArray* sizeArray = [QSTaobaoInfoUtil getSizeSkus:taobaoInfo];
    float screenWidth = [UIScreen mainScreen].bounds.size.width;
    
    __block float currentOriginX = BASE_X;
    __block float currentOriginY = BASE_Y;
    
    [sizeArray enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        NSString* sizeProp = (NSString*)obj;
        NSString* sizeName = [QSTaobaoInfoUtil getNameOfProperty:sizeProp taobaoInfo:taobaoInfo];
        
        float btnWidth = [QSTradeSelectButton getWidthWithText:sizeName];
        
        if (currentOriginX + btnWidth + MARGIN_X > screenWidth) {
            currentOriginX = BASE_X;
            currentOriginY = currentOriginY + DELTA_Y;
        }
        currentOriginX += btnWidth + MARGIN_X;
        
    }];
    return currentOriginY + BTN_HEIGHT + BASE_Y;
}
@end
