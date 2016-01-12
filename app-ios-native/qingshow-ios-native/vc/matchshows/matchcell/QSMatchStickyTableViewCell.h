//
//  QSMatchStickyTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/29.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#define QSMatchStickyTableViewCellIdentifier @"QSMatchStickyTableViewCellIdentifier"
#define QSMatchStickyTableViewCellHeight 184

@interface QSMatchStickyTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UIImageView* stickyImageView;

- (void)bindWithDict:(NSDictionary*)dict;

@end
