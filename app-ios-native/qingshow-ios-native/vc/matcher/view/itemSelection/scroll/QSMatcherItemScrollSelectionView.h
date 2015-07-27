//
//  QSMatcherItemScrollSelectionView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSMatcherItemSelectionViewProtocol.h"
@interface QSMatcherItemScrollSelectionView : UIView <UICollectionViewDelegate, UICollectionViewDataSource, QSMatcherItemSelectionViewProtocol>

+ (instancetype)generateView;

@end
