//
//  QSMatchShowsCell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSIImageLoadingCancelable.h"
@protocol QSMatchShowCellDelegate <NSObject>

- (void)headerImgViewPressed:(id)sender;
- (void)matchImgViewPressed:(id)sender;

@end

@interface QSMatchShowsCell : UICollectionViewCell <QSIImageLoadingCancelable>
@property (weak, nonatomic) IBOutlet UIImageView *headerImgView;
@property (weak, nonatomic) IBOutlet UIView* headerImageTapView;
@property (weak, nonatomic) IBOutlet UILabel *userNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *viewNumlabel;
@property (weak, nonatomic) IBOutlet UIImageView *matchShowImgview;
@property (weak, nonatomic) IBOutlet UIImageView *bgImgView;
@property (weak, nonatomic) IBOutlet UILabel *timeLabel;

@property (assign,nonatomic) NSObject <QSMatchShowCellDelegate>* delegate;
@property (weak, nonatomic) IBOutlet UIImageView* rankImgView;
- (void)bindWithDic:(NSDictionary *)dict withIndex:(int)index;
@end
