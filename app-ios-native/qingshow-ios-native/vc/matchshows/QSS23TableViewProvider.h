//
//  QSS23TableViewProvider.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/12.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"
#import "QSS21TableViewProvider.h"
@class QSS23TableViewProvider;

@protocol QSS23TableViewProviderDelegate<QSAbstractScrollProviderDelegate>

- (void)provider:(QSS23TableViewProvider*)provider didSelectCategory:(NSDictionary*)category;
@end

#warning TODO Refactor

@interface QSS23TableViewProvider : QSS21TableViewProvider

@property (weak, nonatomic) NSObject<QSS23TableViewProviderDelegate>* delegate;

@end
