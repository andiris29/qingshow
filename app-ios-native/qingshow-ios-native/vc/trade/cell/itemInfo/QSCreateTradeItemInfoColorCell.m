//
//  QSCreateTradeItemInfoColorCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeItemInfoColorCell.h"
#import "QSItemUtil.h"
#import "QSTaobaoInfoUtil.h"
#import "QSTradeSelectButton.h"


#define BASE_X 85.f
#define MARGIN_X 10.f
#define BASE_Y 15.f
#define MARGIN_Y 5.f
#define TEXT_BTN_HEIGHT 26.f
#define IMG_BTN_HEIGHT 38.f

@interface QSCreateTradeItemInfoColorCell ()

@end

@implementation QSCreateTradeItemInfoColorCell

- (void)bindWithDict:(NSDictionary*)dict
{
    if (self.btnArray) {
        return;
        for (QSTradeSelectButton* btn in self.btnArray) {
            [btn removeFromSuperview];
        }
    }
    
    NSDictionary* taobaoInfo = [QSItemUtil getTaobaoInfo:dict];
    self.skusArray = [QSTaobaoInfoUtil getColorSkus:taobaoInfo];
    self.btnArray = [@[] mutableCopy];
    float screenWidth = [UIScreen mainScreen].bounds.size.width;
    
    __block float currentOriginX = BASE_X;
    __block float currentOriginY = BASE_Y;
    
    [self.skusArray enumerateObjectsUsingBlock:^(NSString* colorProp, NSUInteger idx, BOOL *stop) {
        NSString* colorName = [QSTaobaoInfoUtil getNameOfProperty:colorProp taobaoInfo:taobaoInfo];
        NSURL* imgUrl = [QSTaobaoInfoUtil getThumbnailUrlOfProperty:colorProp taobaoInfo:taobaoInfo];
        
        QSTradeSelectButton* btn = [[QSTradeSelectButton alloc] init];
        [self.btnArray addObject:btn];
        
        float btnHeight = 0.f;
        float btnWidth = 0.f;
        if (imgUrl) {
            btn.imageUrl = imgUrl;
            btnHeight = IMG_BTN_HEIGHT;
            btnWidth = IMG_BTN_HEIGHT;
        } else {
            btn.text = colorName;
            btnHeight = TEXT_BTN_HEIGHT;
            btnWidth = btn.frame.size.width;
        }

        [btn addTarget:self action:@selector(btnPressed:) forControlEvents:UIControlEventTouchUpInside];
        
        if (currentOriginX + btnWidth + MARGIN_X > screenWidth) {
            currentOriginX = BASE_X;
            currentOriginY = currentOriginY + MARGIN_Y + btnHeight;
        }
        
        btn.frame = CGRectMake(currentOriginX, currentOriginY, btnWidth, btnHeight);
        
        [self.contentView addSubview:btn];
        currentOriginX += btnWidth + MARGIN_X;
        
    }];
}

- (CGFloat)getHeightWithDict:(NSDictionary*)dict
{
    NSDictionary* taobaoInfo = [QSItemUtil getTaobaoInfo:dict];
    NSArray* colorArray = [QSTaobaoInfoUtil getColorSkus:taobaoInfo];
    float screenWidth = [UIScreen mainScreen].bounds.size.width;

    __block float currentOriginX = BASE_X;
    __block float currentOriginY = BASE_Y;
    
    __block float btnHeight = 0.f;
    [colorArray enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        NSString* colorProp = (NSString*)obj;
        NSString* colorName = [QSTaobaoInfoUtil getNameOfProperty:colorProp taobaoInfo:taobaoInfo];
        NSURL* imgUrl = [QSTaobaoInfoUtil getThumbnailUrlOfProperty:colorProp taobaoInfo:taobaoInfo];
        float btnWidth = 0.f;
        btnHeight = 0.f;
        if (imgUrl) {
            btnHeight = IMG_BTN_HEIGHT;
            btnWidth = [QSTradeSelectButton getSizeOfImgBtn].width;
        } else {
            btnHeight = TEXT_BTN_HEIGHT;
            btnWidth = [QSTradeSelectButton getWidthWithText:colorName];
        }

        
        if (currentOriginX + btnWidth + MARGIN_X > screenWidth) {
            currentOriginX = BASE_X;
            currentOriginY = currentOriginY + MARGIN_Y + btnHeight;
        }
        currentOriginX += btnWidth + MARGIN_X;
        
    }];
    return currentOriginY + btnHeight + BASE_Y;
}

- (void)updateWithSizeSelected:(NSString*)sku item:(NSDictionary*)itemDict
{
    NSDictionary* taobaoInfo = [QSItemUtil getTaobaoInfo:itemDict];
    if (!sku) {
        [self enableAllBtn];
    } else {
        for (int i = 0; i < self.btnArray.count; i++) {
            QSTradeSelectButton* btn = self.btnArray[i];
            NSString* colorSku = self.skusArray[i];
            btn.enabled = [QSTaobaoInfoUtil getIsAvaliableOfSize:sku color:colorSku taobaoInfo:taobaoInfo];
        }
    }
}

@end
