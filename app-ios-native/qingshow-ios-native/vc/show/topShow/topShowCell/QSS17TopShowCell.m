//
//  QSS17TopShowCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/21.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS17TopShowCell.h"

@implementation QSS17TopShowCell

- (void)awakeFromNib {
    // Initialization code
    _isFavoBtnPressed = NO;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (IBAction)pressFavoritebtn:(id)sender {
    if (_isFavoBtnPressed == NO) {
        self.imageView.image = [UIImage imageNamed:@"s03_like_btn_hover.png"];
        _isFavoBtnPressed = YES;
    }
    else if(_isFavoBtnPressed == YES)
    {
        self.imageView.image = [UIImage imageNamed:@"s03_like_btn_full.png"];
        _isFavoBtnPressed = NO;
    }

}

- (void)bindWithDataArray:(NSArray *)dataArray
{
    
}
@end
