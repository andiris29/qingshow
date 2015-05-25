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
    for (int i = 0; i < favoInfo.picUrlArray.count; i ++) {
        switch (i) {
            case 0:
                [self.suitImageView setImageFromURL:favoInfo.picUrlArray[i] placeHolderImage:nil];
                [self.suitButton setTitle:favoInfo.contentArray[i] forState:UIControlStateNormal];
                break;
                
            case 1:
                self.skuButton1.hidden = NO;
                [self.skuImageView1 setImageFromURL:favoInfo.picUrlArray[i] placeHolderImage:nil];
                [self.skuButton1 setTitle:favoInfo.contentArray[i] forState:UIControlStateNormal];
                
            case 2:
                self.skuButton2.hidden = NO;
                [self.skuImageView2 setImageFromURL:favoInfo.picUrlArray[i] placeHolderImage:nil];
                [self.skuButton2 setTitle:favoInfo.contentArray[i] forState:UIControlStateNormal];

                break;
            case 3:
                self.skuButton3.hidden = NO;
                [self.skuImageView3 setImageFromURL:favoInfo.picUrlArray[i] placeHolderImage:nil];
                [self.skuButton3 setTitle:favoInfo.contentArray[i] forState:UIControlStateNormal];
                break;
                
            case 4:
                self.skuButton4.hidden = NO;
                [self.skuImageView4 setImageFromURL:favoInfo.picUrlArray[i] placeHolderImage:nil];
                [self.skuButton4 setTitle:favoInfo.contentArray[i] forState:UIControlStateNormal];
                break;
                
            default:
                break;
        }
    }
}

@end
