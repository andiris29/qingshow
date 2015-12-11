//
//  QSMatcherCollectionViewHeaderUserView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/24.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSMatcherCollectionViewHeaderUserView : UIView

@property (weak, nonatomic) IBOutlet UIImageView* headerImgView;
@property (weak, nonatomic) IBOutlet UIImageView* iconImgView;
+ (instancetype)generateView;

@end
