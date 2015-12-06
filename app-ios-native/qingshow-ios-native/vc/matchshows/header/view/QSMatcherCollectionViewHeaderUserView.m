//
//  QSMatcherCollectionViewHeaderUserView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/24.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSMatcherCollectionViewHeaderUserView.h"
#import "UINib+QSExtension.h"
@implementation QSMatcherCollectionViewHeaderUserView
+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QSMatcherCollectionViewHeaderUserView"];
}
- (void)awakeFromNib {

    self.headerImgView.layer.masksToBounds = YES;
//    self.iconImgView.hidden = YES;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.headerImgView.frame = CGRectMake(2, 2, self.bounds.size.width - 4, self.bounds.size.height - 4);
    self.headerImgView.layer.cornerRadius = self.headerImgView.bounds.size.width / 2;
}
@end
