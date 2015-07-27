//
//  QSItemCollectionViewProvider.h
//  qingshow-ios-native
//
//  Created by wxy325 on 2/1/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSWaterfallBasicProvider.h"

@protocol QSItemProviderDelegate <QSAbstractScrollProviderDelegate>

@optional
- (void)didClickItem:(NSDictionary*)itemDict;

@end

typedef NS_ENUM(NSInteger, QSItemDelegateObjType) {
    QSItemWaterfallDelegateObjTypeWithoutDate = 0,
    QSItemWaterfallDelegateObjTypeWithDate = 1
    
};

@interface QSItemCollectionViewProvider : QSWaterfallBasicProvider

@property (assign, nonatomic) QSItemDelegateObjType type;
@property (weak, nonatomic) NSObject<QSItemProviderDelegate>* delegate;

@end
