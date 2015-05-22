//
//  QSFavoInfo.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/22.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSFavoInfo.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@implementation QSFavoInfo
#pragma mark -- 字典转模型
+ (instancetype)favoInfoWithDic:(NSDictionary *)favoInfo
{
#warning TODO 模型转化
    return nil;
}

- (instancetype)initWithDit:(NSDictionary *)dic
{
    if (self == [super init]) {
        
#warning TODO 模型转化
        
        self.suitUrl = @"http://trial01.focosee.com/qs201505/201505140100s.jpg";
        
        self.skuUrl1 = @"http://trial01.focosee.com/qs201505/201505140110s.jpg";
        
        self.skuUrl2 = @"http://trial01.focosee.com/qs201505/201505140120s.jpg";
        
        self.skuUrl3 = @"http://trial01.focosee.com/qs201505/201505140122s.jpg";
        
        self.skuUrl4 = @"http://trial01.focosee.com/qs201505/201505140123s.jpg";
        
        self.suitName = @"蕾丝T恤 x 条纹七分裤";
        
        self.skuPrice1 = @"￥128";
        
        self.skuPrice2 = @"￥128";
        
        self.skuPrice3 = @"￥128";
        
        self.skuPrice4 = @"￥128";
        
    }
    return self;
}
#pragma mark -- 数组转模型
+ (NSArray *)favoInfoWithDicArray:(NSArray *)dicArray
{
    NSMutableArray * favoinfoArray = [[NSMutableArray alloc] init];
    
    for (int i = 0; i < dicArray.count; i ++) {
        NSDictionary *dic = dicArray[i];
        QSFavoInfo *favoInfo = [[QSFavoInfo alloc] initWithDit:dic];
        [favoinfoArray addObject:favoInfo];
    }
    
    return favoinfoArray;
}
@end
