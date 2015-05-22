//
//  QSFavoInfo.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/22.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSFavoInfo : NSObject

//套装图片
@property (nonatomic, copy) NSString *suitUrl;

//套装文字
@property (nonatomic, copy) NSString *suitName;

//单品1图片
@property (nonatomic, copy) NSString *skuUrl1;

//单品1价格
@property (nonatomic ,copy) NSString *skuPrice1;

//单品2图片
@property (nonatomic, copy) NSString *skuUrl2;

//单品2价格
@property (nonatomic ,copy) NSString *skuPrice2;

//单品3图片
@property (nonatomic, copy) NSString *skuUrl3;

//单品3价格
@property (nonatomic ,copy) NSString *skuPrice3;

//单品4图片
@property (nonatomic, copy) NSString *skuUrl4;

//单品4价格
@property (nonatomic ,copy) NSString *skuPrice4;

//字典转模型
+ (instancetype)favoInfoWithDic:(NSDictionary *)favoInfo;

//数组转模型
+ (NSArray *)favoInfoWithDicArray:(NSArray *)dicArray;
@end
