//
//  UINib+QSExtension.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/5/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "UINib+QSExtension.h"

@implementation UINib(QSExtension)
+ (id)generateViewWithNibName:(NSString*)nibName
{
    UINib* nib = [UINib nibWithNibName:nibName bundle:nil];
    NSArray* a = [nib instantiateWithOwner:nil options:nil];
    if (a.count) {
        return a[0];
    } else {
        return nil;
    }
}

@end
