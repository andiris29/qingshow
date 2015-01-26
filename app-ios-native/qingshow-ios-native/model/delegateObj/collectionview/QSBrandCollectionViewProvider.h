//
//  QSBrandCollectionViewDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSWaterfallBasicProvider.h"

@protocol QSBrandCollectionViewProviderDelegate <QSAbstractScrollProviderDelegate>

- (void)didClickBrand:(NSDictionary*)brandDict;

@end


@interface QSBrandCollectionViewProvider : QSWaterfallBasicProvider

@property (weak, nonatomic) NSObject<QSBrandCollectionViewProviderDelegate>* delegate;

@end
