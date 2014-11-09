//
//  QSTableViewDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/7/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSBlock.h"
#import "QSModelListTableViewCell.h"

@protocol QSModelListTableViewDelegateObjDelegate <NSObject>

@optional
- (void)clickModel:(NSDictionary*)model;
- (void)followBtnPressed:(NSDictionary*)model;
- (void)scrollViewDidScroll:(UIScrollView *)scrollView;
@end

@interface QSModelListTableViewDelegateObj : NSObject<UITableViewDataSource, UITableViewDelegate, QSModelListTableViewCellDelegate, UIScrollViewDelegate>

@property (strong, nonatomic) NSMutableArray* resultArray;
@property (strong, nonatomic) ArrayNetworkBlock networkBlock;

#pragma mark - Init Method
- (id)initWithCellClass:(Class)cellClass identifier:(NSString*)identifier;
- (id)initWithCellNib:(UINib*)cellNib identifier:(NSString*)identifier;

#pragma mark - Bind
- (void)bindWithTableView:(UITableView*)tableView;

#pragma mark - Network
- (void)reloadData;
- (void)fetchDataOfPage:(int)page;


@property (weak, nonatomic) NSObject<QSModelListTableViewDelegateObjDelegate>* delegate;
@end
