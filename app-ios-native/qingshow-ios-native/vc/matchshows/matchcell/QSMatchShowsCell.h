//
//  QSMatchShowsCell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol QSMatchShowCellDelegate <NSObject>

- (void)headerImgViewPressed:(id)sender;

@end

@interface QSMatchShowsCell : UICollectionViewCell
@property (weak, nonatomic) IBOutlet UIImageView *headerImgView;
@property (weak, nonatomic) IBOutlet UILabel *userNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *likeNumlabel;
@property (weak, nonatomic) IBOutlet UIImageView *bodyTypeImgView;
@property (weak, nonatomic) IBOutlet UIImageView *matchShowImgview;

@property (assign,nonatomic) NSObject <QSMatchShowCellDelegate>* delegate;

- (void)bindWithDic:(NSDictionary *)dict;
@end
