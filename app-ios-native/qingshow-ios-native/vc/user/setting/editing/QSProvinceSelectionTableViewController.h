//
//  QSProvinceSelectionTableViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
@class QSProvinceSelectionTableViewController;

@protocol QSProvinceSelectionTableViewControllerDelegate <NSObject>

- (void)provinceSelectionVc:(QSProvinceSelectionTableViewController*)vc didSelectionProvince:(NSString*)province city:(NSString*)city;

@end

@interface QSProvinceSelectionTableViewController : UITableViewController

- (id)init;
- (id)initWithProvinceName:(NSString*)provinceName cityListDict:(NSDictionary*)citys;

@property (weak, nonatomic) UIViewController<QSProvinceSelectionTableViewControllerDelegate>* delegate;

@end
