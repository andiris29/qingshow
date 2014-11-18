//
//  QSCompareTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/18/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSCompareTableViewCell.h"
#import "UIImageView+MKNetworkKitAdditions.h"
@implementation QSCompareTableViewCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
- (void)bindWithDict:(NSDictionary*)dict
{
    [self.leftImgView setImageFromURL:[NSURL URLWithString:@"http://f.hiphotos.baidu.com/image/h%3D800%3Bcrop%3D0%2C0%2C1280%2C800/sign=1c9ba895d3a20cf45990f3df46322844/342ac65c10385343beeba8339013b07eca8088ff.jpg"]];
    [self.rightImgView setImageFromURL:[NSURL URLWithString:@"http://e.hiphotos.baidu.com/image/h%3D800%3Bcrop%3D0%2C0%2C1280%2C800/sign=8603fc8f0ff431ada3d24e397b0dcfdd/c75c10385343fbf286a5bf3eb37eca8065388f25.jpg"]];
}
@end
