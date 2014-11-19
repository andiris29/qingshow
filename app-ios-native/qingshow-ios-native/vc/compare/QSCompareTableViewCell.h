//
//  QSCompareTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/18/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSCompareTableViewCell : UITableViewCell

@property (strong, nonatomic) IBOutlet UIImageView* leftImgView;
@property (strong, nonatomic) IBOutlet UIImageView* rightImgView;

- (void)bindWithDict:(NSDictionary*)dict;
@end
