   //
//  QSMatcherItemSelectionView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSMatcherItemSelectionViewProtocol.h"


@interface QSMatcherItemPageSelectionView : UIView <UIScrollViewDelegate, QSMatcherItemSelectionViewProtocol>



+ (instancetype)generateView;

@end
