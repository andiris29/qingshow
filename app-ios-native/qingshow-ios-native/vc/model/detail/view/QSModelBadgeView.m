//
//  QSModelBadgeView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSModelBadgeView.h"
#import "QSSectionButtonGroup.h"

@implementation QSModelBadgeView

- (void)awakeFromNib
{
    QSSectionButtonGroup* group = [[QSSectionButtonGroup alloc] init];
    [self.sectionGroupContainer addSubview:group];
}

+ (QSModelBadgeView*)generateView
{
    UINib* nib = [UINib nibWithNibName:@"QSModelBadgeView" bundle:nil];
    NSArray* array = [nib instantiateWithOwner:self options:nil];
    return array[0];
}

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
