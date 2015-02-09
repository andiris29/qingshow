//
//  QSCommentTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSCommentTableViewCell;

@protocol QSCommentTableViewCellDelegate <NSObject>

- (void)didTapIcon:(QSCommentTableViewCell*)cell;

@end

@interface QSCommentTableViewCell : UITableViewCell

+ (CGSize)getCellSize:(NSDictionary*)commentDict;
+ (CGSize)getCommentSize:(NSString*)str;

@property (strong, nonatomic) IBOutlet UIImageView* iconImageView;
@property (strong, nonatomic) IBOutlet UILabel* nameLabel;
@property (strong, nonatomic) IBOutlet UILabel* dateLabel;
@property (strong, nonatomic) IBOutlet UILabel* contentLabel;
@property (strong, nonatomic) IBOutlet UIView* splitter;

@property (weak, nonatomic) NSObject<QSCommentTableViewCellDelegate>* delegate;
- (void)bindWithComment:(NSDictionary*)commentDict;
- (IBAction)didTapIconImage:(UIGestureRecognizer*)ges;


@end
