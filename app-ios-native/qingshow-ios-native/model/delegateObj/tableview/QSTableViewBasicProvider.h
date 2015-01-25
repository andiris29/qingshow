//
//  QSTableViewBasicDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//


#import "QSAbstractListViewProvider.h"

@interface QSTableViewBasicProvider : QSAbstractListViewProvider<UITableViewDataSource, UITableViewDelegate>

#pragma mark - Init Method
- (id)initWithCellClass:(Class)cellClass identifier:(NSString*)identifier;
- (id)initWithCellNib:(UINib*)cellNib identifier:(NSString*)identifier;

#pragma mark - Bind
- (void)bindWithTableView:(UITableView*)tableView;

#pragma mark - 
- (void)removeData:(NSDictionary*)data withAnimation:(BOOL)fAnimate;

#pragma mark - Private 
@property (weak, nonatomic) UITableView* view;
#pragma mark - Method to be Override
- (void)registerCell;

@property (strong, nonatomic) NSArray* additionalResult;
@end
