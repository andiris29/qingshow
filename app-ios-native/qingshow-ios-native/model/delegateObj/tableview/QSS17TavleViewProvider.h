//
//  QSS17TavleViewProvider.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/25.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"
#import "QSS17TopShowCell.h"

@class QSS17TavleViewProvider;

@protocol QSS17ProviderDelegate <QSAbstractScrollProviderDelegate>

@optional
- (void)didClickedDate:(NSDate*)date ofProvider:(QSS17TavleViewProvider*)provider;

@end

@interface QSS17TavleViewProvider : QSTableViewBasicProvider

@property(nonatomic,weak)NSObject<QSS17ProviderDelegate>* delegate;

@end
