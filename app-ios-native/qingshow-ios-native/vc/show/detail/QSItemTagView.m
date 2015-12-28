//
//  QSItemTagView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/30.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSItemTagView.h"
#import "UINib+QSExtension.h"
#import "QSLayoutUtil.h"

@implementation QSItemTagView
- (void)awakeFromNib {
    self.backgroundImageView.image = [self.backgroundImageView.image resizableImageWithCapInsets:UIEdgeInsetsMake(0, 30, 0, 10)];
}
+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QSItemTagView"];
}

- (void)updateSize {
    CGSize labelSize = [QSLayoutUtil sizeForString:self.tagLabel.text withMaxWidth:INFINITY height:self.tagLabel.bounds.size.height font:self.tagLabel.font];
    CGRect rect = self.frame;
    rect.size.width = labelSize.width + 40;
    self.frame = rect;
}
@end
