//
//  QSShowWaterfallDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/6/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSWaterfallBasicDelegateObj.h"
#import <Foundation/Foundation.h>
#import "QSShowCollectionViewCell.h"


@protocol QSShowDelegateObjDelegate <NSObject, QSWaterfallBasicDelegateObjDelegate>

@optional
- (void)scrollViewDidScroll:(UIScrollView *)scrollView;
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView;
- (void)didClickShow:(NSDictionary*)showDict;
- (void)addFavorShow:(NSDictionary*)showDict;
@end

typedef NS_ENUM(NSInteger, QSShowDelegateObjType) {
    QSShowWaterfallDelegateObjTypeWithDate,
    QSShowWaterfallDelegateObjTypeWithoutDate
};

@interface QSShowCollectionViewDelegateObj : QSWaterfallBasicDelegateObj< QSShowCollectionViewCellDelegate>

@property (assign, nonatomic) QSShowDelegateObjType type;
@property (weak, nonatomic) NSObject<QSShowDelegateObjDelegate>* delegate;

@end
