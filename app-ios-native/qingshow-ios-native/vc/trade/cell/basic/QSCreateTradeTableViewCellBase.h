//
//  QSCreateTradeTableViewCellBase.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSCreateTradeTableViewCellBase : UITableViewCell

- (void)bindWithDict:(NSDictionary*)dict;
- (CGFloat)getHeightWithDict:(NSDictionary*)dict;
- (id)getInputData;
- (void)hideKeyboard;

@end
