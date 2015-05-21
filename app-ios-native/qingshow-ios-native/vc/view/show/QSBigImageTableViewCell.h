//
//  QSShowTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/14/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger, QSBigImageTableViewCellType) {
    QSBigImageTableViewCellTypeModel,
    QSBigImageTableViewCellTypeModelEmpty,
    QSBigImageTableViewCellTypeFashion,
};

@class QSBigImageTableViewCell;

@protocol QSBigImageTableViewCellDelegate <NSObject>

@optional
- (void)clickLikeBtn:(QSBigImageTableViewCell*)cell;
- (void)clickDetailBtn:(QSBigImageTableViewCell*)cell;
@end

@interface QSBigImageTableViewCell : UITableViewCell


@property (weak, nonatomic) IBOutlet UIView* dateContainer;

@property (weak, nonatomic) IBOutlet UIImageView* imgView;
@property (weak, nonatomic) IBOutlet UIView* modelContainer;
@property (weak, nonatomic) IBOutlet UIImageView* iconImgView;
@property (weak, nonatomic) IBOutlet UILabel* label1;
@property (weak, nonatomic) IBOutlet UIButton* likeButton;

@property (weak, nonatomic) NSObject<QSBigImageTableViewCellDelegate>* delegate;

@property (assign, nonatomic) QSBigImageTableViewCellType type;

+ (CGFloat)getHeightWithShow:(NSDictionary*)showDict;
+ (CGFloat)getHeightWithPreview:(NSDictionary*)previewDict;

- (void)bindWithDict:(NSDictionary*)dict;

- (IBAction)likeBtnPressed:(id)sender;

@end
