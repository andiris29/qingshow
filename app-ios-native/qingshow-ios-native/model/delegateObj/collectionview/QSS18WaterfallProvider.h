//
//  QSS18Provider.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSWaterfallBasicProvider.h"

@class QSS18WaterfallProvider;

@protocol QSS18WaterfallProviderDelegate <QSAbstractScrollProviderDelegate>

@optional
- (void)didClickShow:(NSDictionary*)show ofProvider:(QSS18WaterfallProvider*)provider;

@end

@interface QSS18WaterfallProvider : QSWaterfallBasicProvider

@property (strong, nonatomic) NSDate* date;
@property (weak, nonatomic) NSObject<QSS18WaterfallProviderDelegate>* delegate;

- (instancetype)initWithDate:(NSDate*)date;

@end
