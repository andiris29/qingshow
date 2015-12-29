//
//  QSMatchStickyTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/29.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSMatchStickyTableViewCell.h"
#import "NSDictionary+QSExtension.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@interface QSMatchStickyTableViewCell ()

@property (strong, nonatomic) NSDictionary* stickyShow;

@end

@implementation QSMatchStickyTableViewCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)bindWithDict:(NSDictionary*)dict {
    NSString* stickyUrl = [dict stringValueForKeyPath:@"data.stickyShow.stickyCover"];
    if (stickyUrl) {
        self.stickyShow = [dict dictValueForKeyPath:@"data.stickyShow"];
        [self.stickyImageView setImageFromURL:[NSURL URLWithString:stickyUrl]];
        self.stickyImageView.hidden = NO;
    } else {
        self.stickyShow = nil;
        self.stickyImageView.hidden = YES;
    }
}
@end
