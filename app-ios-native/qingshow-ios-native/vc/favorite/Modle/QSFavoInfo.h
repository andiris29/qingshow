//
//  QSFavoInfo.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/22.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSFavoInfo : NSObject

#warning 这个skuUrl1,2,3,4搞成数组?
//图片
@property (nonatomic, strong) NSMutableArray *picUrlArray;

//图片信息
@property (nonatomic, strong) NSMutableArray *contentArray;


//字典转模型
+ (instancetype)favoInfoWithDic:(NSDictionary *)favoInfo;

//数组转模型
+ (NSArray *)favoInfoWithDicArray:(NSArray *)dicArray;
@end
