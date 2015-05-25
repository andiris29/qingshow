//
//  QSTopShowOneDayCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTopShowOneDayCell.h"
#import "QSShowUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
@implementation QSTopShowOneDayCell

- (void)awakeFromNib {
    // Initialization code
}
- (void)bindWithShow:(NSDictionary*)showDict
{
    [self.imgView setImageFromURL:[QSShowUtil getCoverUrl:showDict]];
    self.numLikeLabel.text = [QSShowUtil getNumberLikeDescription:showDict];
}
@end
