//
//  QSP02ModelDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSDetailBaseViewController.h"

@interface QSP02ModelDetailViewController : QSDetailBaseViewController

@property (strong, nonatomic) IBOutlet UITableView* followingTableView;
@property (strong, nonatomic) IBOutlet UITableView* followerTableView;
@property (weak, nonatomic) IBOutlet UITableView *showTableView;

- (id)initWithModel:(NSDictionary*)peopleDict;
@end
