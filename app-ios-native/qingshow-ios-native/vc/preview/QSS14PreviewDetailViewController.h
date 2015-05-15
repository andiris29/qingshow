//
//  QSS14PreviewDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSVideoBaseViewController.h"

@interface QSS14PreviewDetailViewController : QSVideoBaseViewController


- (instancetype)initWithPreview:(NSDictionary*)previewDict;
@property (weak, nonatomic) IBOutlet UIButton *likeBtn;
- (IBAction)shareBtnPressed:(id)sender;
- (IBAction)likeBtnPressed:(id)sender;

@property (strong, nonatomic) UIImage* parentControllerSnapShot;

@end
