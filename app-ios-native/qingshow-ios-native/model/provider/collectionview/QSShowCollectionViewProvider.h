//
//  QSShowWaterfallDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/6/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSWaterfallBasicProvider.h"
#import <Foundation/Foundation.h>
#import "QSMatchShowsCell.h"


@protocol QSShowProviderDelegate <QSAbstractScrollProviderDelegate>

@optional

- (void)didClickShow:(NSDictionary*)showDict provider:(QSAbstractListViewProvider*)provider;
- (void)didClickPeople:(NSDictionary*)peopleDict provider:(QSAbstractListViewProvider*)provider;

@end

@interface QSShowCollectionViewProvider : QSWaterfallBasicProvider< QSMatchShowCellDelegate>



@property (weak, nonatomic) NSObject<QSShowProviderDelegate>* delegate;

- (void)updateShow:(NSDictionary*)showDict;
- (id)init;

@end
