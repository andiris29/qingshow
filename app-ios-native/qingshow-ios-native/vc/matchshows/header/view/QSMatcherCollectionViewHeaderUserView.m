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
    self.headerImgView.layer.cornerRadius = self.headerImgView.bounds.size.width / 2;
    self.headerImgView.layer.masksToBounds = YES;
    self.iconImgView.hidden = YES;
}

@end
