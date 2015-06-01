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
#import "UIViewController+QSExtension.h"
#import "QSShowUtil.h"
#import "QSItemUtil.h"


@implementation QSU14DisplayCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

#pragma mark -
- (void)bindWithShow:(NSDictionary *)showDict
{
    //Show
    [self.showImageView setImageFromURL:[QSShowUtil getCoverUrl:showDict]];
    [self.showButton setTitle:[QSShowUtil getShowDesc:showDict] forState:UIControlStateNormal];
    
    //Item
    NSArray* itemArray = [QSShowUtil getItems:showDict];
    
    //
    self.showDict = showDict;
    self.itemArray = itemArray;
    
    for (int i = 0; i < self.itemImageViews.count; i++) {
        UIButton* itemBtn = self.itemButtons[i];
        UIImageView* itemImgView = self.itemImageViews[i];
        UIImageView* shadowImageView = self.shadow[i];
        if (i < itemArray.count) {
            itemBtn.hidden = NO;
            itemBtn.enabled = YES;
            itemImgView.hidden = NO;
            NSDictionary* itemDict = itemArray[i];
            [itemImgView setImageFromURL:[QSItemUtil getFirstImagesUrl:itemDict]];
            [itemBtn setTitle:[QSItemUtil getPrice:itemDict] forState:UIControlStateNormal];
            shadowImageView.image = [UIImage imageNamed:@"s03_model_shadow"];
        } else {
            itemBtn.hidden = YES;
            itemImgView.hidden = YES;
        }
    }
}

- (IBAction)itemButtonAction:(UIButton *)sender {
    if (sender.tag) {
        NSInteger flag = sender.tag -1;
        [self.currentVC  showItemDetailViewController:self.itemArray[flag]];
    }
}

@end
