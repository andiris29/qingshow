//
//  QSS06CompareViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/18/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSS06CompareViewController : UIViewController
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *typeSegment;
- (IBAction)typeSegmentValueChanged:(id)sender;
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@end
