//
//  QSItemContainerView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSItemContainerView : UIView

+ (QSItemContainerView*)generateView;

- (void)bindWithImageUrl:(NSArray*)imageUrlArray;

@end
