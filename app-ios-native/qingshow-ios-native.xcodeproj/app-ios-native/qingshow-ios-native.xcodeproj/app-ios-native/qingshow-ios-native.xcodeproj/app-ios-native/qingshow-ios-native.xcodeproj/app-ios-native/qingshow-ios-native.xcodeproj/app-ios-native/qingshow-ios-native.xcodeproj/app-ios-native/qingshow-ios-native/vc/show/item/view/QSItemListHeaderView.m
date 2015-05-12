//
//  QSItemListHeaderView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSItemListHeaderView.h"

@implementation QSItemListHeaderView

+ (QSItemListHeaderView*)generateView
{
    UINib* nib = [UINib nibWithNibName:@"QSItemListHeaderView" bundle:nil];
    return [nib instantiateWithOwner:self options:nil][0];
}


@end
