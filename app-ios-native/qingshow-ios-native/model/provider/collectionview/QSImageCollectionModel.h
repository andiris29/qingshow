//
//  QSImageCollectionModel.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/9/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
typedef NS_ENUM(NSUInteger, QSImageCollectionModelType)
{
    QSImageCollectionModelTypeShow,
    QSImageCollectionModelTypeItem,
    QSImageCollectionModelTypeDate
};

@interface QSImageCollectionModel : NSObject

@property (assign, nonatomic) QSImageCollectionModelType type;
@property (strong, nonatomic) id data;

@end
