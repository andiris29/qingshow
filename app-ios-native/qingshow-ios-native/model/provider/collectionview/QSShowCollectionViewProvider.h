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
#import "QSMatchShowsCell.h"


@protocol QSShowProviderDelegate <QSAbstractScrollProviderDelegate>

@optional

- (void)didClickShow:(NSDictionary*)showDict provider:(QSAbstractListViewProvider*)provider;
- (void)addFavorShow:(NSDictionary*)showDict  provider:(QSAbstractListViewProvider*)provider;
- (void)didClickPeople:(NSDictionary*)peopleDict provider:(QSAbstractListViewProvider*)provider;
- (void)didClickPlayButtonOfShow:(NSDictionary*)showDict provider:(QSAbstractListViewProvider*)provider;

- (void)didSelectedCellInCollectionView:(id)sender;
- (void)didClickHeaderImgView:(id)sender;

@end

typedef NS_ENUM(NSInteger, QSShowProviderType) {
    QSShowProviderTypeWithoutDate = 0,
    QSShowProviderTypeWithDate = 1,
    QSShowProviderTypeNew = 2
};


@interface QSShowCollectionViewProvider : QSWaterfallBasicProvider< QSShowCollectionViewCellDelegate,QSMatchShowCellDelegate>

@property (assign, nonatomic) QSShowProviderType type;

@property (weak, nonatomic) NSObject<QSShowProviderDelegate>* delegate;

- (void)updateShow:(NSDictionary*)showDict;
- (id)init;

@end
