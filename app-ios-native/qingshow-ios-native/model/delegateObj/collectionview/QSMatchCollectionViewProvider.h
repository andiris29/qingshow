//
//  QSMatchCollectionViewProvider.h
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSWaterfallBasicProvider.h"

@protocol QSMatchCollectionViewDelegate <NSObject>



@end
typedef enum : NSUInteger {
    U01Type = 1,
    S01Type,
} MatchCellProviderType;

@interface QSMatchCollectionViewProvider : QSWaterfallBasicProvider

@property(nonatomic,assign)NSObject<QSMatchCollectionViewDelegate>* delegate;
@property(nonatomic,assign)NSInteger type;

@end
