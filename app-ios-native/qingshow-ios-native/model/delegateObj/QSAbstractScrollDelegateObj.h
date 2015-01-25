//
//  QSAbstractScrollDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 1/25/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSBlock.h"

@protocol QSAbstractScrollDelegateObjDelegate <NSObject>

@optional
- (void)handleNetworkError:(NSError*)error;
- (void)scrollViewDidScroll:(UIScrollView *)scrollView;
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView;
@end

@interface QSAbstractScrollDelegateObj : NSObject <UIScrollViewDelegate>

@property (strong, nonatomic) NSMutableArray* resultArray;
@property (strong, nonatomic) NSDictionary* metadataDict;
@property (strong, nonatomic) ArrayNetworkBlock networkBlock;
@property (strong, nonatomic) FilterBlock filterBlock;
@property (assign, nonatomic) int currentPage;

@property (weak, nonatomic) NSObject<QSAbstractScrollDelegateObjDelegate>* delegate;

#pragma mark - Network
- (void)reloadData;
- (MKNetworkOperation*)fetchDataOfPage:(int)page;
- (MKNetworkOperation*)fetchDataOfPage:(int)page completion:(VoidBlock)block;
@end
