//
//  QSShowWaterfallDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/6/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSWaterfallBasicProvider.h"
#import <Foundation/Foundation.h>
#import "QSShowCollectionViewCell.h"


@protocol QSShowProviderDelegate <QSAbstractScrollProviderDelegate>

@optional

- (void)didClickShow:(NSDictionary*)showDict;
- (void)addFavorShow:(NSDictionary*)showDict;
- (void)didClickPeople:(NSDictionary*)peopleDict;

@end

typedef NS_ENUM(NSInteger, QSShowDelegateObjType) {
    QSShowWaterfallDelegateObjTypeWithoutDate = 0,
    QSShowWaterfallDelegateObjTypeWithDate = 1

};

@interface QSShowCollectionViewProvider : QSWaterfallBasicProvider< QSShowCollectionViewCellDelegate>

@property (assign, nonatomic) QSShowDelegateObjType type;
@property (weak, nonatomic) NSObject<QSShowProviderDelegate>* delegate;

- (void)updateShow:(NSDictionary*)showDict;


@end
