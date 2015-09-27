//
//  QSIRootContentViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/9/26.
//  Copyright © 2015年 QS. All rights reserved.
//

#ifndef QSIRootContentViewController_h
#define QSIRootContentViewController_h
@protocol QSMenuProviderDelegate;

@protocol QSIRootContentViewController <NSObject>

@property (weak, nonatomic) NSObject<QSMenuProviderDelegate>* menuProvider;
- (void)updateMenuDot;

@end


#endif /* QSIRootContentViewController_h */
