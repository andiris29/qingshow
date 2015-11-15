//
//  QSImageEditingViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ImageCropperView.h"

typedef NS_ENUM(NSInteger, QSImageEditingViewControllerType) {
QSImageEditingViewControllerTypeHead,
QSImageEditingViewControllerTypeBg
};
@class QSImageEditingViewController;
@protocol QSImageEditingViewControllerDelegate <NSObject>

- (void)imageEditingUseImage:(UIImage*)img vc:(QSImageEditingViewController*)vc;
- (void)cancelImageEditing:(QSImageEditingViewController*)vc;

@end

@interface QSImageEditingViewController : UIViewController
@property (weak, nonatomic) IBOutlet ImageCropperView *cropperView;
- (IBAction)cancelBtnPressed:(id)sender;
- (IBAction)useBtnPressed:(id)sender;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *highCon;
@property (weak, nonatomic) NSObject<QSImageEditingViewControllerDelegate>* delegate;
- (id)initWithType:(QSImageEditingViewControllerType)type image:(UIImage*)img;
@end
