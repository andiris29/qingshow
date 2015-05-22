//
//  QSU14DisplayCell.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU14DisplayCell.h"
#import "QSFavoInfo.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@implementation QSU14DisplayCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

#pragma mark -- setValue for cell
- (void)setValueForSubViewsWith:(QSFavoInfo *)favoInfo
{
    NSURL *suitUrl = [NSURL URLWithString:favoInfo.suitUrl];
    [self.suitImageView setImageFromURL:suitUrl placeHolderImage:nil];
    [self.suitButton setTitle:favoInfo.suitName forState:UIControlStateNormal];

    if (favoInfo.skuUrl1) {
        self.skuButton1.hidden = NO;
        NSURL *url = [NSURL URLWithString:favoInfo.skuUrl1];
        [self.skuImageView1 setImageFromURL:url placeHolderImage:nil];
        [self.skuButton1 setTitle:favoInfo.skuPrice1 forState:UIControlStateNormal];
    }else{
        self.skuButton1.hidden = YES;
    }
    if (favoInfo.skuUrl2) {
        self.skuButton2.hidden = NO;
        NSURL *url = [NSURL URLWithString:favoInfo.skuUrl2];
        [self.skuImageView2 setImageFromURL:url placeHolderImage:nil];
        [self.skuButton2 setTitle:favoInfo.skuPrice2 forState:UIControlStateNormal];
    }else{
        self.skuButton2.hidden = YES;
    }
    if (favoInfo.skuUrl3) {
        self.skuButton3.hidden = NO;
        NSURL *url = [NSURL URLWithString:favoInfo.skuUrl3];
        [self.skuImageView3 setImageFromURL:url placeHolderImage:nil];
        [self.skuButton3 setTitle:favoInfo.skuPrice3 forState:UIControlStateNormal];
    }else{
        self.skuButton3.hidden = YES;
    }
    if (favoInfo.skuUrl4) {
        self.skuButton4.hidden = NO;

        NSURL *url = [NSURL URLWithString:favoInfo.skuUrl4];
        [self.skuImageView4 setImageFromURL:url placeHolderImage:nil];
        [self.skuButton4 setTitle:favoInfo.skuPrice4 forState:UIControlStateNormal];
    }else{
        self.skuButton4.hidden = YES;
    }
}

@end
