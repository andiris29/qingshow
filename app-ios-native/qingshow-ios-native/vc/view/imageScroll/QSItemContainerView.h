//
//  QSItemContainerView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
@class QSItemContainerView;

@protocol QSItemContainerViewDelegate <NSObject>

- (void)didTapImageIndex:(int)index ofView:(QSItemContainerView*)view;

@end

@interface QSItemContainerView : UIView

@property (weak, nonatomic) NSObject<QSItemContainerViewDelegate>* delegate;

+ (QSItemContainerView*)generateView;

- (void)bindWithImageUrl:(NSArray*)imageUrlArray;

@end
