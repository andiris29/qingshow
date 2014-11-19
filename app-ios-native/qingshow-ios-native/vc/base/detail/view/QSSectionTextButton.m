//
//  QSSectionTextButton.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/19/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSSectionTextButton.h"

@implementation QSSectionTextButton

+ (QSSectionTextButton*)generateView
{
    UINib* nib = [UINib nibWithNibName:@"QSSectionTextButton" bundle:nil];
    NSArray* array = [nib instantiateWithOwner:self options:nil];
    return array[0];
}
@end
