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
    return [[self alloc]initWithDic:favoInfo];
}

- (instancetype)initWithDic:(NSDictionary *)dic
{
    if (self == [super init]) {
        
#warning TODO 模型转化
        
        NSArray *urlStrArray = [NSArray arrayWithObjects:@"http://trial01.focosee.com/qs201505/201505140100s.jpg",@"http://trial01.focosee.com/qs201505/201505140110s.jpg",@"http://trial01.focosee.com/qs201505/201505140120s.jpg",@"http://trial01.focosee.com/qs201505/201505140122s.jpg",@"http://trial01.focosee.com/qs201505/201505140123s.jpg",nil];
        
        NSArray *contentStr = [NSArray arrayWithObjects:@"蕾丝T恤 x 条纹七分裤",@"168",@"168",@"172",@"188",@"199", nil];
        
        for (int i = 0; i < urlStrArray.count; i ++) {
            NSString *urlStr = urlStrArray[i];
            NSURL *url = [NSURL URLWithString:urlStr];
            [self.picUrlArray addObject:url];
        }
        [self.contentArray addObjectsFromArray:contentStr];
        
        
    }
    return self;
}
#pragma mark -- 数组转模型
+ (NSArray *)favoInfoWithDicArray:(NSArray *)dicArray
{
    NSMutableArray * favoinfoArray = [[NSMutableArray alloc] init];
    
    for (int i = 0; i < dicArray.count; i ++) {
        NSDictionary *dic = dicArray[i];
        QSFavoInfo *favoInfo = [[QSFavoInfo alloc] initWithDic:dic];
        [favoinfoArray addObject:favoInfo];
    }
    
    return favoinfoArray;
}
@end
