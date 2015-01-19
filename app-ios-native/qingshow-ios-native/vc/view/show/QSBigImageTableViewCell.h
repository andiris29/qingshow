//
//  QSShowTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/14/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSSingleImageScrollView.h"

typedef NS_ENUM(NSInteger, QSBigImageTableViewCellType) {
    QSBigImageTableViewCellTypeModel,
    QSBigImageTableViewCellTypeModelEmpty,
    QSBigImageTableViewCellTypeBrand,
    QSBigImageTableViewCellTypeFashion
};

@class QSBigImageTableViewCell;

@protocol QSBigImageTableViewCellDelegate <NSObject>

@optional
- (void)clickCommentBtn:(QSBigImageTableViewCell*)cell;
- (void)clickLikeBtn:(QSBigImageTableViewCell*)cell;
- (void)clickShareBtn:(QSBigImageTableViewCell*)cell;
- (void)clickDetailBtn:(QSBigImageTableViewCell*)cell;

@end

@interface QSBigImageTableViewCell : UITableViewCell <QSImageScrollViewBaseDelegate>

@property (weak, nonatomic) IBOutlet UIImageView* imgView;
@property (weak, nonatomic) IBOutlet UIView* modelContainer;
@property (weak, nonatomic) IBOutlet UIView* btnsContainer;
@property (weak, nonatomic) IBOutlet UIImageView* iconImgView;
@property (weak, nonatomic) IBOutlet UILabel* label1;
@property (weak, nonatomic) IBOutlet UILabel* label2;
@property (weak, nonatomic) IBOutlet UILabel* label3;
@property (weak, nonatomic) IBOutlet UIButton* detailBtn;

@property (weak, nonatomic) IBOutlet UIButton* commentBtn;
@property (weak, nonatomic) IBOutlet UIButton* shareBtn;
@property (weak, nonatomic) IBOutlet UIButton* likeBtn;
@property (weak, nonatomic) NSObject<QSBigImageTableViewCellDelegate>* delegate;

@property (assign, nonatomic) QSBigImageTableViewCellType type;

+ (CGFloat)getHeightWithShow:(NSDictionary*)showDict;
+ (CGFloat)getHeightWithPreview:(NSDictionary*)previewDict;
+ (CGFloat)getHeightWithBrand:(NSDictionary*)brandDict;

- (void)bindWithDict:(NSDictionary*)showDict;

- (IBAction)commentBtnPressed:(id)sender;
- (IBAction)likeBtnPressed:(id)sender;
- (IBAction)shareBtnPressed:(id)sender;
- (IBAction)detailBtnPressed:(id)sender;

@end
