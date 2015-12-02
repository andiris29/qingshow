//
//  QSS23MatcherPreviewViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/7/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSAbstractRootViewController.h"


@protocol QSS23MatcherPreviewViewControllerDelegate <NSObject>
- (void)vc:(UIViewController*)vc didCreateNewMatcher:(NSDictionary*)matcherDict;

@end

@interface QSS23MatcherPreviewViewController : UIViewController

@property (weak, nonatomic) IBOutlet UIImageView *imgView;

@property (weak, nonatomic) NSObject<QSS23MatcherPreviewViewControllerDelegate>* delegate;

- (instancetype)initWithItems:(NSArray*)items rects:(NSArray*)itemRects coverImages:(UIImage*)coverImage;

- (IBAction)backBtnPressed:(id)sender;
- (IBAction)submitBtnPressed:(id)sender;

@end
