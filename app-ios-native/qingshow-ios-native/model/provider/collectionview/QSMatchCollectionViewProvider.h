//
//  QSMatchCollectionViewProvider.h
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSWaterfallBasicProvider.h"
#import "QSMatchShowsCell.h"


@class QSMatchCollectionViewProvider;

@protocol QSMatchCollectionViewProviderDelegate <QSAbstractScrollProviderDelegate>

- (void)provider:(QSMatchCollectionViewProvider*)provider didSelectedCellInCollectionView:(id)sender;
- (void)provider:(QSMatchCollectionViewProvider*)provider didClickHeaderImgView:(id)sender;

@end

typedef MKNetworkOperation* (^MatcherProviderHeaderNetworkBlock)(TopOwnerBlock, ErrorBlock);

@interface QSMatchCollectionViewProvider : QSWaterfallBasicProvider<QSMatchShowCellDelegate>

@property(nonatomic,assign)NSObject<QSMatchCollectionViewProviderDelegate>* delegate;

@property (strong, nonatomic) MatcherProviderHeaderNetworkBlock headerNetworkBlock;
@property (strong, nonatomic) NSDate* currentDate;
@end
