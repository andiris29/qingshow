//
//  QSS23MatcherPreviewViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/7/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSAbstractRootViewController.h"


@interface QSS23MatcherPreviewViewController : UIViewController
@property (weak, nonatomic) IBOutlet UIImageView *imgView;

- (instancetype)initWithItems:(NSArray*)items coverImages:(UIImage*)coverImage menuProvider:(NSObject<QSMenuProviderDelegate>*)menuProvider;

- (IBAction)backBtnPressed:(id)sender;
- (IBAction)submitBtnPressed:(id)sender;

@end
