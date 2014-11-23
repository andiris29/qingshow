//
//  QSU05HairGenderTableViewController.h
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/23.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSU05HairGenderTableViewController;

@protocol CodeUpdateViewControllerDelegate <NSObject>

- (void)codeUpdateViewController:(QSU05HairGenderTableViewController *)vc
                     forCodeType:(NSString *)codeType
                  bySelectedCode:(NSArray *)codes;

@end

@interface QSU05HairGenderTableViewController : UITableViewController
@property (nonatomic, strong) NSArray *codeTable;
@property (nonatomic, strong) NSArray *selectCodes;
@property (nonatomic, strong) NSString *codeType;
@property (nonatomic, weak) id <CodeUpdateViewControllerDelegate> delegate;
@end
