//
//  QSImageCollectionViewProvider.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/9/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSWaterfallBasicProvider.h"
#import "QSImageCollectionModel.h"
#import "QSImageCollectionViewCell.h"
@class QSImageCollectionViewProvider;

@protocol QSImageCollectionViewProviderDelegate <QSAbstractScrollProviderDelegate>

@optional
- (void)didClickModel:(QSImageCollectionModel*)model provider:(QSImageCollectionViewProvider*)provider;

@end

@interface QSImageCollectionViewProvider : QSWaterfallBasicProvider

@property (weak, nonatomic) NSObject<QSImageCollectionViewProviderDelegate>* delegate;

@end
