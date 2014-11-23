//
//  QSTableViewBasicDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSBlock.h"


@protocol QSTableViewBasicDelegateObjDelegate <NSObject>

@optional
- (void)handleNetworkError:(NSError*)error;
- (void)scrollViewDidScroll:(UIScrollView *)scrollView;

@end

@interface QSTableViewBasicDelegateObj : NSObject<UITableViewDataSource, UITableViewDelegate, UIScrollViewDelegate>


@property (strong, nonatomic) NSMutableArray* resultArray;
@property (strong, nonatomic) ArrayNetworkBlock networkBlock;
@property (readonly, nonatomic) int currentPage;

#pragma mark  -

@property (weak, nonatomic) NSObject<QSTableViewBasicDelegateObjDelegate>* delegate;

#pragma mark - Init Method
- (id)initWithCellClass:(Class)cellClass identifier:(NSString*)identifier;
- (id)initWithCellNib:(UINib*)cellNib identifier:(NSString*)identifier;

#pragma mark - Bind
- (void)bindWithTableView:(UITableView*)tableView;

#pragma mark - Network
- (void)reloadData;
- (MKNetworkOperation*)fetchDataOfPage:(int)page;


#pragma mark - Private 
@property (strong, nonatomic) UITableView* tableView;
#pragma mark - Method to be Override
- (void)registerCell;
@end
