//
//  QSBonusTableViewProvider.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/5.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"
@class QSBonusTableViewProvider;
@protocol QSBonusTableViewProviderDelegate <QSAbstractScrollProviderDelegate>

- (void)provider:(QSBonusTableViewProvider*)provider didTapBonus:(NSDictionary*)bonusDict;

@end


@interface QSBonusTableViewProvider : QSTableViewBasicProvider

@property (weak, nonatomic) NSObject<QSBonusTableViewProviderDelegate>* delegate;

@end
