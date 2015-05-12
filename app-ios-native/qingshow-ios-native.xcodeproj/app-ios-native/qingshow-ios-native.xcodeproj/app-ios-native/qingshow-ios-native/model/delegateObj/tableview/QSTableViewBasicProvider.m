//
//  QSTableViewBasicDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"
#import "MKNetworkOperation.h"
#import "NSNumber+QSExtension.h"
#import "QSMetadataUtil.h"
#import "QSAbstractListViewProvider+Protect.h"

@interface QSTableViewBasicProvider ()

@property (assign, nonatomic) Class cellClass;
@property (strong, nonatomic) UINib* cellNib;
@property (strong, nonatomic) NSString* identifier;
@end

@implementation QSTableViewBasicProvider

#pragma mark - Init
- (id)initWithCellClass:(Class)cellClass identifier:(NSString*)identifier
{
    self = [self initWIthIdentifier:identifier];
    if (self) {
        self.cellClass = cellClass;
    }
    return self;
}

- (id)initWithCellNib:(UINib*)cellNib identifier:(NSString*)identifier
{
    self = [self initWIthIdentifier:identifier];
    if (self) {
        self.cellNib = cellNib;
    }
    return self;
}
- (id)initWIthIdentifier:(NSString*)identifier
{
    self = [self init];
    if (self) {
        self.identifier = identifier;
    }
    return self;
}

#pragma mark -
- (void)dealloc
{
    self.view.delegate = nil;
    self.view.dataSource = nil;
}
#pragma mark - 
- (void)bindWithTableView:(UITableView*)tableView
{
    self.view = tableView;
    self.view.dataSource = self;
    self.view.delegate = self;
    tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    
    [self addRefreshControl];
    [self registerCell];
}
- (void)registerCell
{
    //Method to be override
}

#pragma mark - Network
- (MKNetworkOperation*)fetchDataOfPage:(int)page completion:(VoidBlock)block
{
    return [self fetchDataOfPage:page viewRefreshBlock:^{
        [self.view reloadData];
    } completion:block];
}

#pragma mark - TableView
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.resultArray.count;
}
- (void)removeData:(NSDictionary*)data withAnimation:(BOOL)fAnimate
{
    NSUInteger i = [self.resultArray indexOfObject:data];
    if (i > self.resultArray.count) {
        [self.view reloadData];
    } else {
        [self.resultArray removeObject:data];
        NSIndexPath* indexPath = [NSIndexPath indexPathForRow:i inSection:0];
        UITableViewRowAnimation a = fAnimate? UITableViewRowAnimationAutomatic : UITableViewRowAnimationNone;
        [self.view deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:a];
        
        NSDictionary* metaData = [self.metadataDict mutableCopy];
        [QSMetadataUtil addTotalNum:-1ll forDict:metaData];
        self.metadataDict = metaData;
    }
}

@end
