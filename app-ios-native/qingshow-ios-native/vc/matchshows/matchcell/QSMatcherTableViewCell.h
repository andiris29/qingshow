//
//  QSMatcherTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>


#define QSMatcherTableViewCellId @"QSMatcherTableViewCellId"
#define QSMatcherTableViewCellHeight 340.f

@class QSMatcherTableViewCell;
@protocol QSMatcherTableViewCellDelegate <NSObject>

- (void)cell:(QSMatcherTableViewCell*)cell didClickUser:(NSDictionary*)dict;

@end

@interface QSMatcherTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel* timeLabel;
@property (weak, nonatomic) IBOutlet UILabel* dateLabel;
@property (weak, nonatomic) IBOutlet UIImageView* userHeadImgView;
@property (weak, nonatomic) IBOutlet UILabel* numLabel;
@property (weak, nonatomic) IBOutlet UIView* usersContainer;
@property (strong, nonatomic) IBOutletCollection(UIImageView) NSArray *showImgViews;
@property (strong, nonatomic) IBOutletCollection(UIImageView) NSArray *showForegroundImgViews;
@property (weak, nonatomic) NSObject<QSMatcherTableViewCellDelegate>* delegate;

@property (weak, nonatomic) IBOutlet UIView* bottomContainer;

- (void)bindWithDict:(NSDictionary*)dict;

@end
