//
//  QSRemixView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/1.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSRemixView;

@protocol QSRemixViewDelegate <NSObject>

- (void)remixView:(QSRemixView*)view didTapItem:(NSDictionary*)item;

@end

@interface QSRemixView : UIView

- (instancetype)initWithFrame:(CGRect)frame;

- (void)bindWithMasterItem:(NSDictionary*)masterItem remixInfo:(NSDictionary*)remixInfo;
@property (weak, nonatomic) NSObject<QSRemixViewDelegate>* delegate;
@end
