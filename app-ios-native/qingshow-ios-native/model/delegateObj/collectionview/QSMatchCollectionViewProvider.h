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

@interface QSMatchCollectionViewProvider : QSWaterfallBasicProvider

@property(nonatomic,assign)NSObject<QSMatchCollectionViewDelegate>* delegate;

@end
