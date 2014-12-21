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
    QSBigImageTableViewCellTypeBrand,
    QSBigImageTableViewCellTypeFashion
};

@interface QSBigImageTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UIImageView* imgView;
@property (weak, nonatomic) IBOutlet UIView* modelContainer;
@property (weak, nonatomic) IBOutlet UIView* btnsContainer;
@property (weak, nonatomic) IBOutlet UIImageView* iconImgView;
@property (weak, nonatomic) IBOutlet UILabel* label1;
@property (weak, nonatomic) IBOutlet UILabel* label2;
@property (weak, nonatomic) IBOutlet UILabel* label3;
@property (weak, nonatomic) IBOutlet UIButton* detailBtn;

@property (assign, nonatomic) QSBigImageTableViewCellType type;

+ (CGFloat)getHeighWithShow:(NSDictionary*)showDict;
- (void)bindWithDict:(NSDictionary*)showDict;

@end
