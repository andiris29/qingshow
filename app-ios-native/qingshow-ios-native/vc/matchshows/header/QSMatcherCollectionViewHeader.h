//
//  QSMatcherCollectionViewHeader.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSMatcherCollectionViewHeader : UICollectionViewCell

+ (instancetype)generateView;
- (void)bindWithDict:(NSDictionary*)dict;

@end
