//
//  QSCreateTradeTableViewCellBase.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol QSCreateTradeTableViewCellBaseDelegate <NSObject>

@end

@interface QSCreateTradeTableViewCellBase : UITableViewCell

@property (weak, nonatomic) NSObject<QSCreateTradeTableViewCellBaseDelegate>* delegate;

- (void)bindWithDict:(NSDictionary*)dict;
- (CGFloat)getHeightWithDict:(NSDictionary*)dict;
- (id)getInputData;
- (void)hideKeyboard;
@end
