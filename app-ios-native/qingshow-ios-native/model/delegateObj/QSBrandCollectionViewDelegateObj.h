//
//  QSBrandCollectionViewDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSWaterfallBasicDelegateObj.h"

@protocol QSBrandCollectionViewDelegateObjDelegate <NSObject>

- (void)didClickBrand:(NSDictionary*)brandDict;

@end


@interface QSBrandCollectionViewDelegateObj : QSWaterfallBasicDelegateObj

@property (weak, nonatomic) NSObject<QSBrandCollectionViewDelegateObjDelegate>* delegate;

@end
