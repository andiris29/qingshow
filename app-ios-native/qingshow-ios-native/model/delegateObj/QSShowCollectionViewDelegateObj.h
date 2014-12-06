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


@protocol QSShowDelegateObjDelegate <NSObject, QSWaterfallBasicDelegateObjDelegate, QSShowCollectionViewCellDelegate>

@optional
- (void)scrollViewDidScroll:(UIScrollView *)scrollView;
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView;
- (void)didClickShow:(NSDictionary*)showDict;
- (void)addFavorShow:(NSDictionary*)showDict;
- (void)didClickPeople:(NSDictionary*)peopleDict;

@end

typedef NS_ENUM(NSInteger, QSShowDelegateObjType) {
    QSShowWaterfallDelegateObjTypeWithoutDate = 0,
    QSShowWaterfallDelegateObjTypeWithDate = 1

};

@interface QSShowCollectionViewDelegateObj : QSWaterfallBasicDelegateObj< QSShowCollectionViewCellDelegate>

@property (assign, nonatomic) QSShowDelegateObjType type;
@property (weak, nonatomic) NSObject<QSShowDelegateObjDelegate>* delegate;

- (void)updateShow:(NSDictionary*)showDict;

@end
