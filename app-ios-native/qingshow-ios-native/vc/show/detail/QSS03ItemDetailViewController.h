//
//  QSS03ItemDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSSingleImageScrollView.h"

@interface QSS03ItemDetailViewController : UIViewController <QSImageScrollViewBaseDelegate>

@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (weak, nonatomic) IBOutlet UIButton *brandBtn;
@property (weak, nonatomic) IBOutlet UIButton *shopBtn;
@property (weak, nonatomic) IBOutlet UILabel *desLabel;

- (id)initWithItems:(NSArray*)items;

- (IBAction)closeBtnPressed:(id)sender;
- (IBAction)brandBtnPressed:(id)sender;
- (IBAction)shopBtnPressed:(id)sender;
@property (weak, nonatomic) IBOutlet UIView *imageContainer;

@end
